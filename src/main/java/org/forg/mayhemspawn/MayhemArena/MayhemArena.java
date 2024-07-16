package org.forg.mayhemspawn.MayhemArena;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.apache.commons.lang.ObjectUtils;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.forg.mayhemspawn.integration.MayhemPlugin;
import org.forg.mayhemspawn.util.Result;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class MayhemArena {
    public final CuboidRegion region;
    private final int timerTicks;
    private final World activeWorld;
    private final String arenaName;
    IOnEachPlayerTimer onEachPlayerTimer;
    IOnEachPlayerEnd onEachPlayerEnd;
    public int getTimerSeconds() { return this.timerTicks / 20;}
    private MayhemArena(
            World activeWorld,
            String arenaName,
            CuboidRegion region,
            int timerTicks,
            IOnEachPlayerTimer onEachPlayerTimer,
            IOnEachPlayerEnd onEachPlayerEnd
    ) {
        this.region = region;
        this.timerTicks = timerTicks;
        this.activeWorld = activeWorld;
        this.arenaName = arenaName;
        this.onEachPlayerTimer = onEachPlayerTimer;
        this.onEachPlayerEnd = onEachPlayerEnd;
    }
    public static MayhemArena create(
            World world,
            String arenaName,
            CuboidRegion region,
            int timerSeconds,
            @Nullable IOnEachPlayerTimer onEachPlayerTimer,
            @Nullable IOnEachPlayerEnd onEachPlayerEnd
    ) {
        MayhemArena arena = new MayhemArena(
                world,
                arenaName,
                region,
                timerSeconds * 20,
                onEachPlayerTimer,
                onEachPlayerEnd
        );

        return arena;
    }
    private static final int COUNTDOWN_DELAY = 0; private static final int TICKS_IN_SECONDS = 20;
    public Result<ObjectUtils.Null> startCountdownIfNotStarted() {
        if(countdownTask != null && !countdownTask.isCancelled()) { // определяет можно ли начать
            return Result.failure("Игра уже началась");
        }
        Set<Player> activePlayers = activeWorld.getPlayers().stream()
                .filter(Objects::nonNull)
                .filter(player -> {
                    Location pl = player.getLocation();
                    BlockVector3 vector = new BlockVector3(pl.getBlockX(),pl.getBlockY(),pl.getBlockZ());
                    boolean playerInRegion = region.contains(vector);
                    return playerInRegion;
                })
                .collect(Collectors.toSet());
        if(activePlayers.size() < 2) {
            return Result.failure("Слишком мало игроков");
        }
        countdownTask = new RunnableCountdown(
                activePlayers,
                region,
                timerTicks,
                onEachPlayerTimer,
                onEachPlayerEnd
        );
        countdownTask.runTaskTimer(MayhemPlugin.getInstance(), COUNTDOWN_DELAY, TICKS_IN_SECONDS);

        return Result.success(null);
    }
    private RunnableCountdown countdownTask;
    public void stopGame() {
        if (countdownTask != null && !countdownTask.isCancelled()) {
            countdownTask.setFinished(true);
            countdownTask.cancel();
        }
    }
}
