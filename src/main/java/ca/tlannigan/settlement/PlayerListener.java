package ca.tlannigan.settlement;

import com.destroystokyo.paper.ParticleBuilder;
import com.sk89q.worldedit.extent.clipboard.Clipboard;
import com.sk89q.worldedit.math.BlockVector3;
import org.bson.Document;
import org.bukkit.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static java.util.Objects.requireNonNull;


public class PlayerListener implements Listener {

    private final Plugin plugin;
    private final DatabaseHandler dbHandler;
    private HashMap<String, Location> confirmedBuildLocations;

    public PlayerListener(FileConfiguration config) {
        super();
        this.plugin = Bukkit.getPluginManager().getPlugin("Settlement");
        this.dbHandler = new DatabaseHandler(config);
        this.confirmedBuildLocations = new HashMap<String, Location>();
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if (!player.hasPlayedBefore()) {
            // Create player profile in database
            dbHandler.createPlayer(getUUID(player));

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
        if (event.getHand() == EquipmentSlot.HAND && event.getMaterial() == Material.WRITTEN_BOOK) {
            Action action = event.getAction();

            if (action == Action.RIGHT_CLICK_AIR || action == Action.RIGHT_CLICK_BLOCK) {
                ItemStack item = requireNonNull(event.getItem());
                BookMeta bm = (BookMeta) item.getItemMeta();

                if (bm != null && Objects.equals(bm.getTitle(), "Settlement")) {
                    Player player = event.getPlayer();
                    String uuid = getUUID(player);
                    Location playerLoc = player.getLocation();

                    Document playerDoc = dbHandler.getPlayer(uuid);
                    Document playerSettlement = playerDoc.get("settlement", Document.class);
                    Document playerHome = playerSettlement.get("home", Document.class);

                    if (playerHome.getInteger("level") == 0) {
                        StructureBuilder structBuilder = new StructureBuilder(player);
                        Clipboard newHome = structBuilder.load("home1");
                        BlockVector3 dimensions = newHome.getDimensions();

                        if (confirmedBuildLocations.containsKey(uuid)) {
                            player.sendMessage("Settling down here");
                            Location buildLocation = confirmedBuildLocations.get(uuid);

                            List<Integer> homeCoords = playerHome.getList("location", Integer.class);
                            homeCoords.set(0, buildLocation.getBlockX());
                            homeCoords.set(1, buildLocation.getBlockY());
                            homeCoords.set(2, buildLocation.getBlockZ());

                            dbHandler.updatePlayer(uuid, "settlement.home.level", 1);
                            dbHandler.updatePlayer(uuid, "settlement.home.location", homeCoords);

                            structBuilder.build(newHome);
                        } else {
                            confirmedBuildLocations.put(uuid, playerLoc);
                            createParticleBoundary(player, newHome);
                            player.sendMessage("Right-click again to confirm building your home here, or wait 5s to cancel.");
                            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                                confirmedBuildLocations.remove(uuid);
                            }, 100L);

                        }
                    }
                }
            }
        }
    }

    private void createParticleBoundary(Player player, Clipboard clipboard) {
        BlockVector3 origin = clipboard.getOrigin();
        BlockVector3 max = clipboard.getMaximumPoint();
        BlockVector3 min = clipboard.getMinimumPoint();
        BlockVector3 dimensions = clipboard.getDimensions();

        double minOffsetX = min.getBlockX() - origin.getBlockX();
        double maxOffsetX = max.getBlockX() - origin.getBlockX();
        double minOffsetZ = min.getBlockZ() - origin.getBlockZ();
        double maxOffsetZ = max.getBlockZ() - origin.getBlockZ();

        Location[] locations = new Location[8];
        for (int i = 0; i < 8; i++) {
            locations[i] = player.getLocation();
        }

        // Lower y-level boundary points
        locations[0].setX(locations[0].getX() + maxOffsetX);
        locations[0].setZ(locations[0].getZ() + maxOffsetZ);

        locations[1].setX(locations[1].getX() + maxOffsetX);
        locations[1].setZ(locations[1].getZ() + minOffsetZ);

        locations[2].setX(locations[2].getX() + minOffsetX);
        locations[2].setZ(locations[2].getZ() + minOffsetZ);

        locations[3].setX(locations[3].getX() + minOffsetX);
        locations[3].setZ(locations[3].getZ() + maxOffsetZ);

        // Upper y-level boundary points
        locations[4].setX(locations[4].getX() + maxOffsetX);
        locations[4].setZ(locations[4].getZ() + maxOffsetZ);
        locations[4].setY(locations[4].getY() + dimensions.getY());

        locations[5].setX(locations[5].getX() + maxOffsetX);
        locations[5].setZ(locations[5].getZ() + minOffsetZ);
        locations[5].setY(locations[5].getY() + dimensions.getY());

        locations[6].setX(locations[6].getX() + minOffsetX);
        locations[6].setZ(locations[6].getZ() + minOffsetZ);
        locations[6].setY(locations[6].getY() + dimensions.getY());

        locations[7].setX(locations[7].getX() + minOffsetX);
        locations[7].setZ(locations[7].getZ() + maxOffsetZ);
        locations[7].setY(locations[7].getY() + dimensions.getY());

        for (int i = 0; i < 8; i++) {
            new ParticleBuilder(Particle.REDSTONE)
                    .data(new Particle.DustOptions(Color.RED, 5.0f))
                    .count(5)
                    .location(locations[i])
                    .receivers(player)
                    .spawn();
        }
    }

    private String getUUID(Player player) {
        return player.getUniqueId().toString();
    }
}
