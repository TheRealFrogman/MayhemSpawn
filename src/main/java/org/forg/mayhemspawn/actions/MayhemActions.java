package org.forg.mayhemspawn.actions;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.forg.mayhemspawn.MayhemArena.MayhemArena;
import org.forg.mayhemspawn.integration.MayhemArenaHooked;
import org.forg.mayhemspawn.selection.MayhemRegionSelector;
import org.forg.mayhemspawn.util.Result;

public class MayhemActions {
    private static final String defaultNoPermMessage = "No permission";
    public static Result<ItemStack> getTool(Player p) {
        if(MayhemPermissions.canTool(p)) {
            return Result.success(MayhemRegionSelector.get());
        } else return Result.failure(defaultNoPermMessage);
    }
    public static Result<MayhemArena> createArena(
        Player p,
        World world,
        String arenaName,
        CuboidRegion region,
        int timer
    ) {
        if (MayhemPermissions.canCreate(p)) {
            MayhemArena arena = MayhemArenaHooked.createWithHooks(
                    world,
                    arenaName,
                    region,
                    timer);
            return Result.success(arena);
        } else return Result.failure(defaultNoPermMessage);
    }

    public static boolean startArena(Player player, MayhemArena arena) {
        if (!MayhemPermissions.canActivate(player)) return false;
        arena.startCountdownIfNotStarted();
        return true;
    }

    public static void selectPos_One(Player player, BlockVector3 v){
        if (!MayhemPermissions.canSelect(player)) return;
        MayhemRegionSelector.selectPos_One(player,v);
    }
    public static void selectPos_Two(Player player, BlockVector3 v){
        if (!MayhemPermissions.canSelect(player)) return;
        MayhemRegionSelector.selectPos_Two(player,v);
    }

    public static void buttonSet(Player p, BlockVector3 v) throws Exception{
        if (!MayhemPermissions.canButtonset(p)) return;
        throw new Exception("Not implemented");
    }
}
