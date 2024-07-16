package org.forg.mayhemspawn.MayhemArena;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

class RunnableCountdown extends BukkitRunnable {
    private final IOnEachPlayerTimer onEachTimer;
    private final IOnEachPlayerEnd onEachPlayerEnd;
    private int remainedTicks;
    void setRemainedTicks(int t) {
        this.remainedTicks = t;
    }
    private boolean isFinished = false;
    void setFinished(boolean b) {
        this.isFinished = b;
    }
    private Set<Player> activePlayers;
    private CuboidRegion region;

    RunnableCountdown(
            Set<Player> activePlayers,
            CuboidRegion region,
            int ticks,
            IOnEachPlayerTimer onEachTimer,
            IOnEachPlayerEnd onEachPlayerEnd
    ) {
        this.activePlayers = activePlayers;
        this.region = region;
        this.remainedTicks = ticks;
        this.onEachTimer = onEachTimer;
        this.onEachPlayerEnd = onEachPlayerEnd;
    }

    @Override
    public void run() {
        if (remainedTicks > 0 && !isFinished) {
            activePlayers.stream()
                    .filter(Objects::nonNull)
                    .forEach(p -> onEachTimer.run(p, this.remainedTicks / 20));

            this.remainedTicks -= 20;
        } else {
            this.cancel();
            setFinished(true);
            Set<Player> winners = activePlayers.stream()
                    .filter(Objects::nonNull)
                    .filter(player -> !isPlayerInRegion(player,region))
                    .collect(Collectors.toSet());

            activePlayers.stream()
                    .filter(Objects::nonNull)
                    .forEach(player -> onEachPlayerEnd.run(player, winners.contains(player)));
        }
    }

    private boolean isPlayerInRegion(Player player, CuboidRegion region) {
        Location pl = player.getLocation();
        BlockVector3 vector = new BlockVector3(pl.getBlockX(), pl.getBlockY(), pl.getBlockZ());
        return region.contains(vector);
    }

}
