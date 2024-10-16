package org.forg.mayhemspawn.config;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.antlr.v4.runtime.misc.NotNull;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;
import org.forg.mayhemspawn.MayhemArena.RewardedTimedMayhemArena;
import org.forg.mayhemspawn.MayhemArena.RewardedTimedMayhemArenaBuilder;

import javax.annotation.Nullable;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class MayhemConfig {
    private final JavaPlugin plugin; private final Configuration config;
    private MayhemConfig(JavaPlugin plugin, Configuration config) {
        this.plugin = plugin;
        this.config = config;
    }

    public static void init(JavaPlugin plugin, Configuration config) {
        instance = new MayhemConfig(plugin, config);
    }
    private static MayhemConfig instance;
    public static MayhemConfig getInstance() {
        return instance;
    }
    private static final Map<String, Map<String, RewardedTimedMayhemArena>> worlds = new LinkedHashMap<>();

    // надо реализовать считывание листов кнопок теперь
    public void load() {
        worlds.clear();

        ConfigurationSection worldsSection = config.getConfigurationSection("worlds");

        assert worldsSection != null;
        for (String worldName : worldsSection.getKeys(false)) {
            ConfigurationSection worldSection = worldsSection.getConfigurationSection(worldName);
            Map<String, RewardedTimedMayhemArena> regionMap = new LinkedHashMap<>();

            assert worldSection != null;
            for (String regionName : worldSection.getKeys(false)) {
                ConfigurationSection regionSection = worldSection.getConfigurationSection(regionName);

                try{
                    assert regionSection != null;
                    ConfigurationSection section1 = regionSection.getConfigurationSection("p1");
                    ConfigurationSection section2 = regionSection.getConfigurationSection("p2");
                    int timer = regionSection.getInt("timer");

                    assert section1 != null;
                    assert section2 != null;
                    CuboidRegion region = new CuboidRegion(
                            new BlockVector3(section1.getInt("x"), section1.getInt("y"), section1.getInt("z")),
                            new BlockVector3(section2.getInt("x"), section2.getInt("y"), section2.getInt("z"))
                    );

                    int reward = regionSection.getInt("reward");
//                    ConfigurationSection activatorSection = regionSection.getConfigurationSection("activator");
//
//
//                    assert activatorSection != null;
//                    BlockVector3 activatorLocation = new BlockVector3(
//                            activatorSection.getInt("x"),
//                            activatorSection.getInt("y"),
//                            activatorSection.getInt("z")
//                    );
//
                    List<List<Integer>> activatorsList = (List<List<Integer>>) regionSection.getList("activators");

                    World world = plugin.getServer().getWorld(worldName);
                    var builder = new RewardedTimedMayhemArenaBuilder(plugin,world,regionName)
                            .setRegion(region)
                            .setRewardMoney(reward)
                            .setTimerTicks(timer);

                    for (List<Integer> ints : activatorsList) {
                        builder.addActivator(new BlockVector3(ints.get(0), ints.get(1), ints.get(2)));
                    }

                    regionMap.put(regionName,builder.build());
                } catch (NullPointerException ex) {
                    plugin.getLogger().severe(
                            "Конфиг для региона " + regionName + " не валидный");
                }

            }

            worlds.put(worldName, regionMap);
        }
    }

    public void addRegion(@NotNull RewardedTimedMayhemArena arena) {
        String regionName = arena.arenaName;
        World world = arena.activeWorld;
        String lowercased = regionName.toLowerCase();
        worlds.computeIfAbsent(world.getName(), k -> new LinkedHashMap<>()).put(lowercased, arena);
        saveRegionsToConfig();
    }

    public Map<String, RewardedTimedMayhemArena> getRegionsForWorldName(@NotNull String worldName) {
        return worlds.getOrDefault(worldName, new LinkedHashMap<>());
    }
    @Nullable
    public RewardedTimedMayhemArena getRegion(@NotNull String worldName, @NotNull String regionName) {
        String lowercased = regionName.toLowerCase();
        Map<String, RewardedTimedMayhemArena> worldRegions = worlds.getOrDefault(worldName, new LinkedHashMap<>());
        if (worldRegions.containsKey(lowercased)) {
            return worldRegions.get(lowercased);
        }
        return null;
    }

    @Nullable
    public RewardedTimedMayhemArena getByActivatorLocation(@NotNull String worldName, @NotNull BlockVector3 location) {
        Map<String, RewardedTimedMayhemArena> worldRegions = worlds.getOrDefault(worldName, new LinkedHashMap<>());
        for (RewardedTimedMayhemArena arena: worldRegions.values()) {
            for (BlockVector3 activatorLocation: arena.activatorLocations) {
                if (location.equals(activatorLocation)) {
                    return arena;
                }
            }
        }
        return null;
    }

    private void saveRegionsToConfig() {
        ConfigurationSection worldsSection = config.createSection("worlds");
        for (Map.Entry<String, Map<String, RewardedTimedMayhemArena>> worldEntry : worlds.entrySet()) {

            String worldName = worldEntry.getKey();
            ConfigurationSection worldSection = worldsSection.createSection(worldName);

            for (Map.Entry<String, RewardedTimedMayhemArena> regionEntry : worldEntry.getValue().entrySet()) {
                String regionName = regionEntry.getKey();
                RewardedTimedMayhemArena arena = regionEntry.getValue();
                ConfigurationSection regionSection = worldSection.createSection(regionName);

                regionSection.set("timer", arena.timerTicks);
                regionSection.set("reward", arena.reward);

                //я это изменил
                List<Integer[]> activatorList = new ArrayList<>();
                for (BlockVector3 location : arena.activatorLocations) {
                    Integer[] arr = {location.x(),location.y(),location.z()};
                    activatorList.add(arr);
                }
                regionSection.set("activators", activatorList);

                BlockVector3 v1 = arena.region.getPos1();
                BlockVector3 v2 = arena.region.getPos2();

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
