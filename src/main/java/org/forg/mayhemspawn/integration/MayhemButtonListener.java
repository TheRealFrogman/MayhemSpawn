package org.forg.mayhemspawn.integration;

import com.sk89q.worldedit.math.BlockVector3;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.forg.mayhemspawn.actions.MayhemActions;

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
                actions.startArenaByButton(e.getPlayer(), new BlockVector3(
                        block.getLocation().getBlockX(),
                        block.getLocation().getBlockY(),
                        block.getLocation().getBlockZ()
                ));
        }
    }
}
