package org.forg.mayhemspawn.config;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.forg.mayhemspawn.MayhemArena.MayhemArena;
import org.forg.mayhemspawn.integration.MayhemArenaHooked;
import org.forg.mayhemspawn.integration.MayhemPlugin;

import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MayhemConfig {
    private final JavaPlugin plugin; private final Configuration config;
    private MayhemConfig(JavaPlugin plugin, Configuration config) {
        this.plugin = plugin;
        this.config = config;
    }
    private static MayhemConfig instance;
    public static MayhemConfig getInstance(JavaPlugin plugin, Configuration config) {
        if (instance == null) instance = new MayhemConfig(plugin, config);
        return instance;
    }
    private static final Map<String, Map<String, MayhemArena>> worlds = new LinkedHashMap<>();

    public void load() {
        worlds.clear();
        ConfigurationSection worldsSection = config.getConfigurationSection("worlds");
        if (worldsSection == null) return;

        for (String worldName : worldsSection.getKeys(false)) {
            ConfigurationSection worldSection = worldsSection.getConfigurationSection(worldName);
            if (worldSection == null) continue;
            Map<String, MayhemArena> regionMap = new LinkedHashMap<>();

            for (String regionName : worldSection.getKeys(false)) {
                ConfigurationSection regionSection = worldSection.getConfigurationSection(regionName);
                if (regionSection == null) continue;

                ConfigurationSection section1 = regionSection.getConfigurationSection("p1");
                ConfigurationSection section2 = regionSection.getConfigurationSection("p2");
                int timer = regionSection.getInt("timer");

                assert section1 != null;
                assert section2 != null;

                CuboidRegion region = new CuboidRegion(
                        new BlockVector3(section1.getInt("x"), section1.getInt("y"), section1.getInt("z")),
                        new BlockVector3(section2.getInt("x"), section2.getInt("y"), section2.getInt("z"))
                        );

                World world = MayhemPlugin.getInstance().getServer().getWorld(worldName);
                regionMap.put(
                        regionName,
                        MayhemArenaHooked
                                .createWithHooks(world, regionName,region,timer)
                );
            }

            worlds.put(worldName, regionMap);
        }
    }
    public void addRegionForWorld(World world, String regionName, MayhemArena arena) {
        String lowercased = regionName.toLowerCase();
        worlds.computeIfAbsent(world.getName(), k -> new LinkedHashMap<>()).put(lowercased, arena);
        saveRegionsToConfig();
    }

    public Map<String, MayhemArena> getRegionsForWorldName(String worldName) {
        return worlds.getOrDefault(worldName, new LinkedHashMap<>());
    }
    @Nullable
    public MayhemArena getRegion(String worldName, String regionName) {
        String lowercased = regionName.toLowerCase();
        Map<String, MayhemArena> worldRegions = worlds.getOrDefault(worldName, new LinkedHashMap<>());
        if (worldRegions.containsKey(lowercased)) {
            return worldRegions.get(lowercased);
        }
        return null;
    }

    private void saveRegionsToConfig() {
        ConfigurationSection worldsSection = config.createSection("worlds");
        for (Map.Entry<String, Map<String, MayhemArena>> worldEntry : worlds.entrySet()) {

            String worldName = worldEntry.getKey();
            ConfigurationSection worldSection = worldsSection.createSection(worldName);

            for (Map.Entry<String, MayhemArena> regionEntry : worldEntry.getValue().entrySet()) {
                String regionName = regionEntry.getKey();
                MayhemArena arena = regionEntry.getValue();
                ConfigurationSection regionSection = worldSection.createSection(regionName);

                BlockVector3 v1 = arena.region.getPos1();
                BlockVector3 v2 = arena.region.getPos2();

                regionSection.set("timer", arena.getTimerSeconds());

                ConfigurationSection pointOneSection = regionSection.createSection("p1");
                pointOneSection.set("x", v1.x());
                pointOneSection.set("y", v1.y());
                pointOneSection.set("z", v1.z());

                ConfigurationSection pointTwoSection = regionSection.createSection("p2");
                pointTwoSection.set("y", v2.y());
                pointTwoSection.set("z", v2.z());
                pointTwoSection.set("x", v2.x());
            }
        }
        plugin.saveConfig();
    }

}
