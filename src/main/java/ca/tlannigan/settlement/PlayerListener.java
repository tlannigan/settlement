package ca.tlannigan.settlement;

import ca.tlannigan.settlement.utils.DatabaseHandler;
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

import static java.util.Objects.isNull;


public class PlayerListener implements Listener {

    private FileConfiguration config;

    public PlayerListener(FileConfiguration config) {
        super();
        this.config = config;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(!player.hasPlayedBefore()) {
            // Create player profile in database
            DatabaseHandler DatabaseHandler = new DatabaseHandler(config);
            DatabaseHandler.createPlayer(player);

            // Give player Settlement Blueprint item
            ItemStack blueprint = new ItemStack(Material.WRITTEN_BOOK);
            BookMeta bm = (BookMeta) blueprint.getItemMeta();

            bm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3&lSettlement Blueprint"));
            bm.addPage("Use this blueprint to start your first settlement!");
            bm.setTitle("Settlement");
            bm.setAuthor("Settlement");
            blueprint.setItemMeta(bm);

            player.getInventory().addItem(blueprint);

            DatabaseHandler.closeClient();
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
                    player.sendMessage("Title: " + bm.getTitle());
                    player.sendMessage("Start setting up your settlement!");
                }
            }
        }
    }
}
