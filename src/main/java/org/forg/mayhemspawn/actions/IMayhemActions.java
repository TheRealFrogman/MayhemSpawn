package org.forg.mayhemspawn.actions;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;

public interface IMayhemActions {
    void createBaseArena(Player p, World world, String arenaName);
    void confirmArena(Player p);
    void selectArena(Player p, String worldName, String arenaName);
    void setRewardMoney(Player p, int rewardMoney);
    void setTimer(Player p, int timer);
    void setRegion(Player p, CuboidRegion region);
    void startArenaByButton(Player p, BlockVector3 activatorLocation);
    void startArenaByCommand(Player p, String arenaName);
    void buttonset(Player p, BlockVector3 location);

}
