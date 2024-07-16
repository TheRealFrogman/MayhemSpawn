package org.forg.mayhemspawn.integration;

import com.sk89q.worldedit.regions.CuboidRegion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.forg.mayhemspawn.MayhemArena.IOnEachPlayerEnd;
import org.forg.mayhemspawn.MayhemArena.IOnEachPlayerTimer;
import org.forg.mayhemspawn.MayhemArena.MayhemArena;

public class MayhemArenaHooked {
    private static Economy economy;
    private static final IOnEachPlayerEnd onEachPlayerEnd =
        (Player player, boolean isWinner) -> {
            if (isWinner) {
                economy.depositPlayer(player, 1000);
                player.sendTitle("Победа", "награда: " + String.valueOf(1000), 2, 48, 2);
                player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 2, 1);
            } else {
                player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 2, 1);
                player.damage(100);
            }
        };
    private static final IOnEachPlayerTimer onEachPlayerTimer
            = (Player player, int remainedSec)->{
                player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
                player.sendTitle(String.valueOf(remainedSec), null, 2, 16, 2);
            };
    public static MayhemArena createWithHooks(
            World world,
            String arenaName,
            CuboidRegion region,
            int timerTicks
    ) {
        RegisteredServiceProvider<Economy> economyProvider = MayhemPlugin
                .getInstance()
                .getServer()
                .getServicesManager()
                .getRegistration(Economy.class);
        economy = economyProvider.getProvider();

        return MayhemArena.create(
                world,
                arenaName,
                region,
                timerTicks,
                onEachPlayerTimer,
                onEachPlayerEnd
        );
    }
}
