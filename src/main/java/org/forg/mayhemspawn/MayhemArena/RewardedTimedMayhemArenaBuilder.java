package org.forg.mayhemspawn.MayhemArena;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class RewardedTimedMayhemArenaBuilder {
    public final JavaPlugin plugin;
    public final World activeWorld;
    public final String arenaName;
    public RewardedTimedMayhemArenaBuilder(
        JavaPlugin plugin, World activeWorld, String arenaName){
        this.plugin = plugin;
        this.activeWorld = activeWorld;
        this.arenaName = arenaName;
    }
    public int timerTicks = 0;
    public RewardedTimedMayhemArenaBuilder setTimerTicks(int timerTicks){
        if(timerTicks < 1) throw new IllegalArgumentException("Can't be less than 1");
        this.timerTicks = timerTicks;
        return this;
    }
    public boolean isTimerSet(){
        return timerTicks > 0;
    }
    public int reward = 0;
    public RewardedTimedMayhemArenaBuilder setRewardMoney(int reward){
        if(reward < 1) throw new IllegalArgumentException("Can't be less than 1");
        this.reward = reward;
        return this;
    }
    public boolean isRewardSet(){
        return reward > 0;
    }
    public List<BlockVector3> activatorLocations = new ArrayList<>();
    public RewardedTimedMayhemArenaBuilder addActivator(BlockVector3 activatorLocation) {
        this.activatorLocations.add(activatorLocation);
        return this;
    }
    public boolean hasAtleastOneActivator(){
        return !activatorLocations.isEmpty();
    }
    public CuboidRegion region;
    public RewardedTimedMayhemArenaBuilder setRegion(CuboidRegion region) {
        if(region == null) throw new IllegalArgumentException("Can't be null");

        this.region = region;
        return this;
    }
    public boolean isRegionSet(){
        return region != null;
    }

    public boolean canBuild() {
        return isTimerSet() &&
                hasAtleastOneActivator() &&
                isRegionSet() &&
                isRewardSet();
    }

    public RewardedTimedMayhemArena build() {
        final int MIN_PLAYERS_DEFAULT = 2;
        if(canBuild())
            return new RewardedTimedMayhemArena(
                plugin,
                activeWorld,
                arenaName,
                region,
                activatorLocations,
                timerTicks,
                reward,
                MIN_PLAYERS_DEFAULT
            );
        else throw new IllegalStateException("Canbuild can't be null");
    }
}
