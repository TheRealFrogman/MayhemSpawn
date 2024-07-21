package org.forg.mayhemspawn.MayhemArena;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

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
    public BlockVector3 activatorLocation;
    public RewardedTimedMayhemArenaBuilder setActivator(BlockVector3 activatorLocation) {
        if(activatorLocation == null) throw new IllegalArgumentException("Can't be null");

        this.activatorLocation = activatorLocation;
        return this;
    }
    public boolean isActivatorSet(){
        return activatorLocation != null;
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
                isActivatorSet() &&
                isRegionSet() &&
                isRewardSet();
    }

    public RewardedTimedMayhemArena build() {
        if(canBuild())
            return new RewardedTimedMayhemArena(
                plugin,
                activeWorld,
                arenaName,
                region,
                activatorLocation,
                timerTicks,
                reward
            );
        else throw new IllegalStateException("Can't be null");
    }
}
