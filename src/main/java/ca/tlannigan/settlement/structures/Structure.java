package ca.tlannigan.settlement.structures;

import org.bukkit.Bukkit;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;

import java.util.Iterator;

abstract class Structure {
    final Player player;

    protected Structure(Player player) {
        this.player = player;
    }

    abstract boolean canUpdate(int structLvl); // Check if structure can be upgraded to the requested level

    abstract void updateStructure(); // Update the physical structure

    abstract void updatePlayer(); // Update the player document in the database

    boolean playerHasAdvancement(String namespacedKey) {
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
