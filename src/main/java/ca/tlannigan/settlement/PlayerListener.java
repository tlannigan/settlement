package ca.tlannigan.settlement;

import ca.tlannigan.settlement.utils.DatabaseHandler;
import org.bson.BsonInt32;
import org.bson.Document;
import org.bukkit.ChatColor;
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

import static java.util.Objects.isNull;


public class PlayerListener implements Listener {

    private DatabaseHandler dbHandler;

    public PlayerListener(FileConfiguration config) {
        super();
        this.dbHandler = new DatabaseHandler(config);
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(!player.hasPlayedBefore()) {
            // Create player profile in database
            dbHandler.createPlayer(player);

            // Give player Settlement Blueprint item
            ItemStack blueprint = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bm = (BookMeta) blueprint.getItemMeta();

            bm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3&lSettlement Blueprint"));
            bm.addPage("Use this blueprint to start your first settlement!");
            bm.setTitle("Settlement");
            bm.setAuthor("Settlement");
            blueprint.setItemMeta(bm);

            player.getInventory().addItem(blueprint);
        }
    }

    @EventHandler
    public void onBookInteract(PlayerInteractEvent event) {
        if (event.getMaterial() == Material.WRITTEN_BOOK) {
            ItemStack item = event.getItem();
            BookMeta bm = (BookMeta) item.getItemMeta();
            if (bm.getTitle().equals("Settlement")) {
                if (!isNull(event.getClickedBlock())) {
                    Player player = event.getPlayer();
                    Document playerDoc = dbHandler.getPlayer(player);
                    int playerHomeLevel = playerDoc
                            .get("settlement", Document.class)
                            .get("home", Document.class)
                            .getInteger("level");

                    if (playerHomeLevel == 0) {
                        player.sendMessage("Starting your settlement at this location");
//                        // TODO: Increase home level to 1
                    }
                }
            }
        }
    }
}
