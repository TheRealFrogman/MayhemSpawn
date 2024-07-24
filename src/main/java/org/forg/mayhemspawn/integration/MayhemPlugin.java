package org.forg.mayhemspawn.integration;

import dev.jorel.commandapi.CommandAPI;
import org.bukkit.plugin.java.JavaPlugin;
import org.forg.mayhemspawn.actions.MayhemActions;
import org.forg.mayhemspawn.config.MayhemConfig;

public final class MayhemPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Plugin Launching");
        saveDefaultConfig();

        MayhemConfig.init(this, getConfig());
        MayhemConfig.getInstance().load();

        MayhemActions actions = new MayhemActions(this);
        getServer().getPluginManager().registerEvents(new MayhemButtonListener(actions),this);

        new MayhemCommands().init(this).register(this);

        getLogger().info("Plugin initialized and ready to go");
    }

    @Override
    public void onDisable() {
        CommandAPI.unregister("mspawn");
    }
}
