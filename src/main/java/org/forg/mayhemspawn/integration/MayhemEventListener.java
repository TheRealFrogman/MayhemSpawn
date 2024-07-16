package org.forg.mayhemspawn.integration;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.forg.mayhemspawn.actions.MayhemActions;
import org.forg.mayhemspawn.selection.MayhemRegionSelector;
import org.forg.mayhemspawn.util.Result;

public class MayhemEventListener implements Listener {
    @EventHandler
    void onBlockClick(PlayerInteractEvent e) {
        Player player = e.getPlayer();
        Block block = e.getClickedBlock();
        ItemStack itemInHand = player.getInventory().getItemInMainHand();
        Action action = e.getAction();

        if(block == null) return;
        if(!action.equals(Action.LEFT_CLICK_BLOCK)) {
            if(!action.equals(Action.RIGHT_CLICK_BLOCK)) return;
        }

        if(!MayhemRegionSelector.isValidSelector(itemInHand)) return;
        if(action.equals(Action.LEFT_CLICK_BLOCK)) {
            Location l = block.getLocation();
            BlockVector3 bv1 = new BlockVector3(l.getBlockX(),l.getBlockY(),l.getBlockZ());
            MayhemActions.selectPos_One(player,bv1);
            player.sendMessage(
                    ChatColor.AQUA + "[MayhemSpawn] "+ ChatColor.WHITE + "Первая точка определена"
            );
            e.setCancelled(true);
        }
        if(action.equals(Action.RIGHT_CLICK_BLOCK)) {
            Location l = block.getLocation();
            BlockVector3 bv2 = new BlockVector3(l.getBlockX(),l.getBlockY(),l.getBlockZ());
            MayhemActions.selectPos_Two(player,bv2);
            player.sendMessage(
                    ChatColor.AQUA + "[MayhemSpawn] "+ ChatColor.WHITE + "Вторая точка определена"
            );
            e.setCancelled(true);
        }

        Result<CuboidRegion> regionResult = MayhemRegionSelector.getRegionForPlayer(player);
        if (regionResult.isSuccess())
            player.sendMessage(
                    ChatColor.AQUA + "[MayhemSpawn] "+ ChatColor.WHITE + "Можно создавать регион"
            );
    }
}
