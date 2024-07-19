package org.forg.mayhemspawn.MayhemArena;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class RewardedTimedMayhemArenaBuilder {
    private final JavaPlugin plugin;
    private final World activeWorld;
    private final String arenaName;
    public RewardedTimedMayhemArenaBuilder(
        JavaPlugin plugin, World activeWorld, String arenaName){
        this.plugin = plugin;
        this.activeWorld = activeWorld;
        this.arenaName = arenaName;
    }
    public int timerTicks = 0;
    public RewardedTimedMayhemArenaBuilder setTimerTicks(int timerTicks){
        this.timerTicks = timerTicks;
        return this;
    }
    public int reward = 0;
    public RewardedTimedMayhemArenaBuilder setRewardMoney(int reward){
        this.reward = reward;
        return this;
    }
    public BlockVector3 activatorLocation;
    public RewardedTimedMayhemArenaBuilder setActivator(BlockVector3 activatorLocation) {
        this.activatorLocation = activatorLocation;
        return this;
    }
    public CuboidRegion region;
    public RewardedTimedMayhemArenaBuilder setRegion(CuboidRegion region) {
        this.region = region;
        return this;
    }

    public boolean canBuild() {
        return Objects.nonNull(region) &&
                Objects.nonNull(activatorLocation) &&
                timerTicks > 1 &&
                reward > 1;
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
