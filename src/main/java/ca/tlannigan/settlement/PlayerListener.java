package ca.tlannigan.settlement;

import com.sk89q.worldedit.extent.clipboard.Clipboard;
import org.bson.Document;
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

import java.util.List;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;


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
            ItemStack item = requireNonNull(event.getItem());
            BookMeta bm = (BookMeta) item.getItemMeta();

            Player player = event.getPlayer();
            String uuid = getUUID(player);

            Document playerDoc = dbHandler.getPlayer(uuid);
            Document playerSettlement = playerDoc.get("settlement", Document.class);
            Document playerHome = playerSettlement.get("home", Document.class);

            if (playerHome.getInteger("level") == 0) {
                player.sendMessage("Starting your settlement at this location");
                Location playerLoc = player.getLocation();

                List<Integer> homeCoords = playerHome.getList("location", Integer.class);
                homeCoords.set(0, playerLoc.getBlockX());
                homeCoords.set(1, playerLoc.getBlockY() + 1);
                homeCoords.set(2, playerLoc.getBlockZ());

                dbHandler.updatePlayer(uuid, "settlement.home.level", 1);
                dbHandler.updatePlayer(uuid, "settlement.home.location", homeCoords);

                StructureBuilder structBuilder = new StructureBuilder(player, playerLoc);
                Clipboard newHome = structBuilder.load("home1");
                structBuilder.build(newHome);
            }
        }
    }

    private String getUUID(Player player) {
        return player.getUniqueId().toString();
    }
}
