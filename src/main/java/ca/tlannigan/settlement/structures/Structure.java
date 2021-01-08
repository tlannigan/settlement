package ca.tlannigan.settlement.structures;

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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.advancement.Advancement;
import org.bukkit.advancement.AdvancementProgress;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Iterator;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

abstract class Structure {
    final Player player;
    final Location loc;

    protected Structure(Player player) {
        this.player = player;
        this.loc = player.getLocation();
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

        return advProgress.isDone();
    }

    public Clipboard load(String name) {
        Plugin plugin = requireNonNull(Bukkit.getServer().getPluginManager().getPlugin("Settlement"));
        File file;
        Clipboard clipboard = null;
        file = new File(plugin.getDataFolder().getAbsolutePath() + "\\" + name + ".schem");
        ClipboardFormat format = ClipboardFormats.findByFile(file);

        try {
            if (format != null) {
                ClipboardReader reader = format.getReader(new FileInputStream(file));
                clipboard = reader.read();
            } else {
                player.sendMessage("The file " + name + ".schem does not exist in the Settlement/Schematics folder. Contact an administrator.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return clipboard;
    }

    public void build(Clipboard clipboard) {
        org.bukkit.World world = loc.getWorld();

        if (nonNull(world)) {
            World adaptedWorld = BukkitAdapter.adapt(world);
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld)) {
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ()))
                        .ignoreAirBlocks(true)
                        .build();
                Operations.complete(operation);
            } catch (WorldEditException e) {
                e.printStackTrace();
            }
        }

        System.out.println(clipboard.getDimensions());
    }
}
