package org.forg.mayhemspawn.MayhemArena.base;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class MayhemArena {
    protected final JavaPlugin plugin;
    public final CuboidRegion region;
    public final World activeWorld;
    public final String arenaName;
    public BlockVector3 activatorLocation;
    public MayhemArena(
            JavaPlugin plugin,
            World activeWorld,
            String arenaName,
            CuboidRegion region,
            BlockVector3 activatorLocation
    ) {
        this.region = region;
        this.activeWorld = activeWorld;
        this.arenaName = arenaName;
        this.plugin = plugin;
        this.activatorLocation = activatorLocation;
    }
    private boolean running = false;
    public boolean isStarted() { return running; }
    public boolean isFinished() {
        return !running;
    }
    protected void startArena() {
        if(isStarted())
            throw new IllegalStateException("Arena already started");
        running = true;
    }
    public void finishArena() {
        if (isFinished()) throw new IllegalStateException("Arena already finished");
        running = false;
    }
}
