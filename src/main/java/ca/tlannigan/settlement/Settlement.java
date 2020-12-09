package ca.tlannigan.settlement;

import ca.tlannigan.settlement.commands.CommandDescription;
import org.bukkit.plugin.java.JavaPlugin;

public class Settlement extends JavaPlugin {
    @Override
    public void onEnable(){
        getLogger().info("onEnable is called!");
        this.getCommand("description").setExecutor(new CommandDescription());
    }

    @Override
    public void onDisable(){
        // Fired when the server stop and disables all plugins
    }
}
