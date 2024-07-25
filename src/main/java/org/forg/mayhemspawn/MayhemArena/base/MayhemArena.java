package org.forg.mayhemspawn.MayhemArena.base;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class MayhemArena {
    protected final JavaPlugin plugin;
    public final CuboidRegion region;
    public final World activeWorld;
    public final String arenaName;
    public BlockVector3 activatorLocation;
    protected Set<Player> activePlayers = new HashSet<>();
    protected int minPlayers;
    public MayhemArena(
            JavaPlugin plugin,
            World activeWorld,
            String arenaName,
            CuboidRegion region,
            BlockVector3 activatorLocation,
            int minPlayers
    ) {
        this.region = region;
        this.activeWorld = activeWorld;
        this.arenaName = arenaName;
        this.plugin = plugin;
        this.activatorLocation = activatorLocation;
        this.minPlayers = minPlayers;
    }
    private boolean running = false;
    public boolean isStarted() { return running; }
    public boolean isFinished() {
        return !running;
    }
    protected void definePlayersAndStartArena() throws Exception {
        activePlayers.clear();

        activePlayers = activeWorld.getPlayers().stream()
                .filter(Objects::nonNull)
                .filter(player -> {
                    Location pl = player.getLocation();
                    BlockVector3 vector = new BlockVector3(pl.getBlockX(),pl.getBlockY(),pl.getBlockZ());
                    boolean playerInRegion = region.contains(vector);
                    return playerInRegion;
                })
                .collect(Collectors.toSet());

        if(isStarted())
            throw new IllegalStateException("Arena already started");
        if(activePlayers.size() < minPlayers) {
            throw new Exception("Игроков не может быть меньше " + minPlayers);
        }

        running = true;

        afterStart();
    }
    public void finishArena() {
        if (isFinished()) throw new IllegalStateException("Arena already finished");
        running = false;
    }
    protected abstract void afterStart() throws Exception;
    protected boolean isPlayerInRegion(Player player, CuboidRegion region) {
        Location pl = player.getLocation();
        BlockVector3 vector = new BlockVector3(pl.getBlockX(), pl.getBlockY(), pl.getBlockZ());
        return region.contains(vector);
    }

}
