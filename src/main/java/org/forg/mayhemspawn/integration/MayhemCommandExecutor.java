package org.forg.mayhemspawn.integration;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.forg.mayhemspawn.MayhemArena.MayhemArena;
import org.forg.mayhemspawn.actions.MayhemActions;
import org.forg.mayhemspawn.config.MayhemConfig;
import org.forg.mayhemspawn.selection.MayhemRegionSelector;
import org.forg.mayhemspawn.util.Result;

public class MayhemCommandExecutor implements org.bukkit.command.CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if(!label.equals("mspawn")) return true;
        if(args.length == 0) return false;
        if(!(commandSender instanceof Player)) {
            commandSender.sendMessage("Только игроки могут использовать комаду");
            return true;
        }
        Player player = (Player) commandSender;
        if(args[0].equals("tool")) {
            Result<ItemStack> toolResult = MayhemActions.getTool(player);
            if(!toolResult.isSuccess()) { return true; }

            ItemStack tool =  toolResult.getValue();
            player.getInventory().addItem(tool);
        }
        if(args[0].equals("create")) {
            if(args.length < 3) {
                player.sendMessage(ChatColor.AQUA + "[MayhemSpawn] " + ChatColor.WHITE + "Формат: mspawn create <имя> <таймер>");
                return true;
            }
            String arenaName = args[1];
            World world = player.getWorld();
            int timer;
            try{
                timer = Integer.parseInt(args[2]);
            } catch (NumberFormatException err) {
                player.sendMessage(ChatColor.AQUA + "[MayhemSpawn] " + ChatColor.WHITE + "Формат: mspawn create <имя> <таймер>");
                return true;
            }

            Result<CuboidRegion> regionResult = MayhemRegionSelector.getRegionForPlayer(player);

            if(!regionResult.isSuccess()) return true;

            Result<MayhemArena> arenaResult = MayhemActions.createArena(
                    player,
                    world,
                    arenaName,
                    regionResult.getValue(),
                    timer
                );
            if(!arenaResult.isSuccess()) { return true; }

            MayhemConfig
                    .getInstance(null,null)
                    .addRegionForWorld(world,arenaName, arenaResult.getValue());

            player.sendMessage(
                    ChatColor.AQUA + "[MayhemSpawn] "+ ChatColor.WHITE + "Арена создана, можно начинать"
            );

        }
        if(args[0].equals("start") || args[0].equals("activate")) {
            if(args.length < 2) {
                player.sendMessage(
                        ChatColor.AQUA + "[MayhemSpawn] "+ ChatColor.WHITE + "Формат: mspawn start <имя>"
                );
                return true;
            }
            String arenaName = args[1];
            String worldName = player.getWorld().getName();
            MayhemArena arena = MayhemConfig
                    .getInstance(null,null)
                    .getRegion(worldName,arenaName);

            if(arena == null) {
                player.sendMessage("Арена с таким именем не найдена");
                return true;
            }
            MayhemActions.startArena(player, arena);
        }
        if(args[0].equals("buttonset")) {
            Location playerLocation = player.getLocation();
            try {
                MayhemActions.buttonSet(player,
                    new BlockVector3(
                            playerLocation.getBlockX(),
                            playerLocation.getBlockY(),
                            playerLocation.getBlockZ()
                    )
                );
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }

        return true;
    }
}
