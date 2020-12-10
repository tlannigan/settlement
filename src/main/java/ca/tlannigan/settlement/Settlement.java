package ca.tlannigan.settlement;

import ca.tlannigan.settlement.commands.CommandDescription;
import org.bukkit.plugin.java.JavaPlugin;

public class Settlement extends JavaPlugin {
    @Override
    public void onEnable(){
        getLogger().info("Settlements will reign");
        this.getCommand("description").setExecutor(new CommandDescription());
        getServer().getPluginManager().registerEvents(new PlayerListener(), this);
    }

    @Override
    public void onDisable(){
        // Fired when the server stop and disables all plugins
    }
}
