package org.forg.mayhemspawn.actions;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.World;

import java.util.UUID;

public interface IMayhemActions {
    void createBaseArena(UUID playerUUID, World world, String arenaName) throws Exception;
    void confirmArena(UUID playerUUID) throws Exception;
    void selectArena(UUID playerUUID, String worldName, String arenaName) throws Exception;
    void setRewardMoney(UUID playerUUID, int rewardMoney) throws Exception;
    void setTimer(UUID playerUUID, int timer) throws Exception;
    void setRegion(UUID playerUUID, CuboidRegion region) throws Exception;
    void startArenaByButton(String worldName, BlockVector3 activatorLocation) throws Exception;
    void startArenaByCommand(String worldName, String arenaName) throws Exception;
    void addButton(UUID playerUUID, BlockVector3 location) throws Exception;
}
