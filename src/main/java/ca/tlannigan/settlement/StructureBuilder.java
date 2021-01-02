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
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import static java.util.Objects.nonNull;
import static java.util.Objects.requireNonNull;

public class StructureBuilder {

    final private Player player;
    final private Location loc;

    public StructureBuilder(Player player, Location loc) {
        this.player = player;
        this.loc = loc;
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
            System.out.println(2);
            World adaptedWorld = BukkitAdapter.adapt(world);
            try (EditSession editSession = WorldEdit.getInstance().newEditSession(adaptedWorld)) {
                System.out.println(3);
                Operation operation = new ClipboardHolder(clipboard)
                        .createPaste(editSession)
                        .to(BlockVector3.at(loc.getBlockX(), loc.getBlockY() + 1, loc.getBlockZ()))
                        .ignoreAirBlocks(true)
                        .build();
                Operations.complete(operation);
                System.out.println(4);
            } catch (WorldEditException e) {
                e.printStackTrace();
            }
        }
    }
}
