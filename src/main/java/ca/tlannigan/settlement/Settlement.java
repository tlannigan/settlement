package ca.tlannigan.settlement;

import org.bukkit.plugin.java.JavaPlugin;

public class Settlement extends JavaPlugin {
    @Override
    public void onEnable(){
        getLogger().info("onEnable is called!");
    }

    @Override
    public void onDisable(){
        // Fired when the server stop and disables all plugins
    }
}
