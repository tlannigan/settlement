package ca.tlannigan.settlement;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;

public class PlayerListener implements Listener {

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        ItemStack blueprint = new ItemStack(Material.WRITTEN_BOOK);
        BookMeta bm = (BookMeta) blueprint.getItemMeta();

        bm.setDisplayName(ChatColor.translateAlternateColorCodes('&', "&3&lSettlement Blueprint"));
        bm.addPage("Settlement\nUse this blueprint to start your first settlement!");
        bm.setAuthor("MrCreamsicle");
        blueprint.setItemMeta(bm);

        event.getPlayer().getInventory().addItem(blueprint);
    }

}
