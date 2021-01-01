package ca.tlannigan.settlement;

import com.sk89q.worldedit.EditSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.WorldEditException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormat;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardFormats;
import com.sk89q.worldedit.extent.clipboard.io.ClipboardReader;
import com.sk89q.worldedit.function.operation.Operation;
import com.sk89q.worldedit.function.operation.Operations;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.session.ClipboardHolder;
import com.sk89q.worldedit.world.World;
import org.bson.Document;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;


public class PlayerListener implements Listener {

    private final DatabaseHandler dbHandler;

    public PlayerListener(FileConfiguration config) {
        super();
        this.dbHandler = new DatabaseHandler(config);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            // Create player profile in database
            dbHandler.createPlayer(player.getUniqueId().toString());

            // Create and give player Settlement Blueprint item
            ItemStack blueprint = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bm = (BookMeta) blueprint.getItemMeta();

            if (bm != null) {
                bm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3&lSettlement Blueprint"));
                bm.addPage("Use this blueprint to start your first settlement!");
                bm.setTitle("Settlement");
                bm.setAuthor("Settlement");
                blueprint.setItemMeta(bm);

                player.getInventory().addItem(blueprint);
            }
        }
    }

    @EventHandler
    public void onBookInteract(PlayerInteractEvent event) {
        if (event.getMaterial() == Material.WRITTEN_BOOK) {
            ItemStack item = event.getItem();
            BookMeta bm = null;
            if (item != null) {
                bm = (BookMeta) item.getItemMeta();
            }
            if (
                    bm != null &&
                            bm.getTitle() != null &&
                            bm.getTitle().equals("Settlement")
            ) {
                if (!isNull(event.getClickedBlock())) {
                    Player player = event.getPlayer();
                    String uuid = getUUID(player);

                    Document playerDoc = dbHandler.getPlayer(uuid);
                    Document playerHome = playerDoc.get("settlement.home", Document.class);

                    if (playerHome.getInteger("level") == 0) {
                        player.sendMessage("Starting your settlement at this location");
                        Location clickedLocation = event.getClickedBlock().getLocation();

                        List<Integer> homeCoords = playerHome.getList("location", Integer.class);
                        homeCoords.set(0, clickedLocation.getBlockX());
                        homeCoords.set(1, clickedLocation.getBlockY());
                        homeCoords.set(2, clickedLocation.getBlockZ());

                        dbHandler.updatePlayer(uuid, "settlement.home.level", 1);
                        dbHandler.updatePlayer(uuid, "settlement.home.location", homeCoords);
                    }
                }
            }
        }
    }

    private Clipboard loadStructure() {
        Plugin plugin = Bukkit.getServer().getPluginManager().getPlugin("Settlement");
        File file;
        Clipboard clipboard = null;
        if (nonNull(plugin)) {
            file = new File(plugin.getDataFolder().getAbsolutePath() + "/house.schem");
            ClipboardFormat format = ClipboardFormats.findByFile(file);

            try {
                ClipboardReader reader = format.getReader(new FileInputStream(file));
                clipboard = reader.read();
            } catch (NullPointerException | IOException e) {
                e.printStackTrace();
            }
        }

        return clipboard;
    }

    private void createStructure(Player player, Clipboard clipboard, List<Integer> coords) {
        org.bukkit.World world = Bukkit.getWorld(getUUID(player));

        if (nonNull(world)) {
            World adaptedWorld = BukkitAdapter.adapt(world);
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(coords.get(0), coords.get(1), coords.get(2)))
                        .ignoreAirBlocks(false)
                        .build();
                Operations.complete(operation);
            } catch (WorldEditException e) {
                e.printStackTrace();
            }
        }
    }

    private String getUUID(Player player) {
        return player.getUniqueId().toString();
    }
}
