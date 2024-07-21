package org.forg.mayhemspawn.MayhemArena.base;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class TimedMayhemArenaBase extends MayhemArena {
    public static final int TICKS_IN_SECONDS = 20;
    public int interval = TICKS_IN_SECONDS;
//    public int getTimerSeconds() { return this.timerTicks / TICKS_IN_SECONDS;}
    public int timerTicks;
    public TimedMayhemArenaBase(
            JavaPlugin plugin,
            World activeWorld,
            String arenaName,
            CuboidRegion region,
            BlockVector3 activatorLocation,
            int timerTicks
    ) {
        super(plugin, activeWorld, arenaName, region, activatorLocation);
        this.timerTicks = timerTicks;
        remainedTicks = timerTicks;
    }
    private int remainedTicks;
    private int countdownDelay;
    public void setCountdownDelay(int delay) {
        if (delay < 0) throw new IllegalArgumentException("can't be negative");
        this.countdownDelay = delay;
    }
    private boolean isEnoughPlayers() {
        return activePlayers.size() >= 2;
    }
    public void startArenaCountdown(Player starter) {
        activePlayers = activeWorld.getPlayers().stream()
                .filter(Objects::nonNull)
                .filter(player -> {
                    Location pl = player.getLocation();
                    BlockVector3 vector = new BlockVector3(pl.getBlockX(),pl.getBlockY(),pl.getBlockZ());
                    boolean playerInRegion = region.contains(vector);
                    return playerInRegion;
                })
                .collect(Collectors.toSet());

        if(!isEnoughPlayers()) {
            starter.sendMessage("Игроков не может быть меньше 2");
            return;
        }

        super.startArena();
        starter.sendMessage("Вы начали арену");
        new BukkitRunnable() {
            @Override public void run() {
                arenaOnTickTemplate(this::cancel);
            }
        }.runTaskTimer(plugin, countdownDelay, interval);
    }
    private boolean isPlayerInRegion(Player player, CuboidRegion region) {
        Location pl = player.getLocation();
        BlockVector3 vector = new BlockVector3(pl.getBlockX(), pl.getBlockY(), pl.getBlockZ());
        return region.contains(vector);
    }

    protected Set<Player> activePlayers;

    private void arenaOnTickTemplate(
            CancelController scheduledCancelController
    ) {
        if (remainedTicks > 0) {
            activePlayers.stream()
                    .filter(Objects::nonNull)
                    .forEach(p -> onEachPlayerTimer(p, remainedTicks / 20));

            remainedTicks -= 20;
        } else {
            scheduledCancelController.cancel();
            remainedTicks = timerTicks; // это сбрасывает таймер, если не сбросить, то в первый раз сработает как надо, а потом будет моментально взрываться
            finishArena();
            Set<Player> winners = activePlayers.stream()
                    .filter(Objects::nonNull)
                    .filter(player -> !isPlayerInRegion(player,region))
                    .collect(Collectors.toSet());

            activePlayers.stream()
                    .filter(Objects::nonNull)
                    .forEach(player -> onEachPlayerEnd(player, winners.contains(player)));
        }
    }
    abstract protected void onEachPlayerTimer(Player player, int remainedSec);
    abstract protected void onEachPlayerEnd(Player player, boolean isWinner);
    private interface CancelController {
        void cancel();
    }
}
