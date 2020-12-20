package ca.tlannigan.settlement;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

import static java.util.Objects.isNull;


public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();

        if(!player.hasPlayedBefore()) {
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
                    player.sendMessage("Title: " + bm.getTitle());
                    player.sendMessage("Start setting up your settlement!");
                }
            }
        }
    }

}
