package org.forg.mayhemspawn.integration;

import org.bukkit.plugin.java.JavaPlugin;
import org.forg.mayhemspawn.config.MayhemConfig;

public final class MayhemPlugin extends JavaPlugin {
    @Override
    public void onEnable() {
        getLogger().info("Plugin Launching");
        saveDefaultConfig();

        MayhemConfig.getInstance(this, getConfig()).load();

        getCommand("mspawn").setExecutor(new MayhemCommandExecutor());
        getServer().getPluginManager().registerEvents(new MayhemEventListener(),this);
//        CuboidRegion region = mayhemConfig.getRegion("world", "spawn");
//        Map<String, CuboidRegion> regions = mayhemConfig.getRegionsForWorldName("world");
//        mayhemConfig.addRegionForWorld("nether","my-region",new CuboidRegion(
//                new BlockVector3(123,10,123),
//                new BlockVector3(-123,0,-123)
//        ));

        getLogger().info("Plugin initialized and ready to go");
    }

    @Override
    public void onDisable() {

    }
    public static JavaPlugin getInstance() {
        return getPlugin(MayhemPlugin.class);
    }
}
