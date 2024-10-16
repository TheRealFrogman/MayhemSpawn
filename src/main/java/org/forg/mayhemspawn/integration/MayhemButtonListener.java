package org.forg.mayhemspawn.integration;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.forg.mayhemspawn.actions.MayhemActions;
import org.forg.mayhemspawn.config.MayhemConfig;

public class MayhemButtonListener implements Listener {
    MayhemActions actions;
    public MayhemButtonListener(MayhemActions actions) {
        this.actions = actions;
    }
    @EventHandler
    public void onButtonClicks(PlayerInteractEvent e) {
        Block block = e.getClickedBlock();
        Player player = e.getPlayer();
        if(block == null) return;
        if(Tag.BUTTONS.isTagged(block.getBlockData().getMaterial())) {
            if(player.hasPermission("mspawn.activate"))
                try{
                    var arena = MayhemConfig.getInstance().getByActivatorLocation(player.getWorld().getName(),new BlockVector3(
                            block.getLocation().getBlockX(),
                            block.getLocation().getBlockY(),
                            block.getLocation().getBlockZ()
                    ));
                    if(arena == null) return; // это чтобы мы не стартовали каждый раз несуществующую арену. чтоб не писалось каждый раз "Арена не найдена"
                    actions.startArenaByButton(player.getWorld().getName(), new BlockVector3(
                            block.getLocation().getBlockX(),
                            block.getLocation().getBlockY(),
                            block.getLocation().getBlockZ()
                    ));
                    player.sendMessage("Вы начали арену");
                } catch (Exception err) {
                    player.sendMessage(err.getMessage());
                }
        }
    }
}
