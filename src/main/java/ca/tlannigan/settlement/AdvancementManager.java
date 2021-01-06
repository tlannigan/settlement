package ca.tlannigan.settlement;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.Iterator;

public class AdvancementManager {
    public static boolean hasAdvancement(Player player, String namespacedKey) {
        Advancement adv = null;
        for (Iterator<Advancement> iter = Bukkit.getServer().advancementIterator(); iter.hasNext(); ) {
            Advancement serverAdv = iter.next();
            if (serverAdv.getKey().getKey().equals(namespacedKey)) {
                adv = serverAdv;
                break;
            }
        }
        AdvancementProgress advProgress = player.getAdvancementProgress(adv);
        if (advProgress.isDone()) {
            return true;
        }
        return false;
    }
}
