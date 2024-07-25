package org.forg.mayhemspawn.MayhemArena;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;
import org.forg.mayhemspawn.MayhemArena.base.TimedMayhemArenaBase;

public class RewardedTimedMayhemArena extends TimedMayhemArenaBase {
    private final Economy economy;
    public int reward;
    public void setRewardMoney(int reward) {
        this.reward = reward;
    }
    public RewardedTimedMayhemArena(
            JavaPlugin plugin,
            World activeWorld,
            String arenaName,
            CuboidRegion region,
            BlockVector3 activatorLocation,
            int timerTicks,
            int reward,
            int minPlayers
    ) {
        super(plugin, activeWorld, arenaName, region, activatorLocation, timerTicks, minPlayers);
        this.reward = reward;
        RegisteredServiceProvider<Economy> economyProvider = plugin
                .getServer()
                .getServicesManager()
                .getRegistration(Economy.class);
        assert economyProvider != null;
        economy = economyProvider.getProvider();
    }
    @Override
    protected void onEachPlayerTimer(Player player, int remainedSec) {
        player.playSound(player, Sound.BLOCK_NOTE_BLOCK_CHIME, 1, 1);
        player.sendTitle(String.valueOf(remainedSec), null, 2, 16, 2);
    }
    @Override
    protected void onEachPlayerEnd(Player player, boolean isWinner) {
        if (isWinner) {
            economy.depositPlayer(player, reward);
            player.sendTitle("Победа", "награда: " + reward, 2, 48, 2);
            player.playSound(player, Sound.ENTITY_FIREWORK_ROCKET_LARGE_BLAST, 2, 1);
        } else {
            player.playSound(player, Sound.ENTITY_GENERIC_EXPLODE, 2, 1);
            player.damage(100);
        }
    }
    public void start() throws Exception {
        this.definePlayersAndStartArena();
    }

}
