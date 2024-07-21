package org.forg.mayhemspawn.integration;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.CommandPermission;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Switch;
import org.bukkit.plugin.java.JavaPlugin;
import org.forg.mayhemspawn.actions.MayhemActions;

public class MayhemCommands {
    MayhemActions actions;
    public MayhemCommands(JavaPlugin plugin) {
        actions= new MayhemActions(plugin);

        new CommandAPICommand("mspawn")
            .withPermission("mspawn.*")
            .withSubcommands(
                createCommand(),
                // startCommand(),
                confirmCommand(),
                selectCommand()
            )
            .withSubcommands(setCommand()
                    .withSubcommands(
                        setButtonCommand(),
                        setRewardCommand(),
                        setRegionCommand(),
                        setTimerCommand()
                    )
                ).register(plugin);
    }
    private CommandAPICommand setTimerCommand() {

        return new CommandAPICommand("timer")
                .withPermission("mspawn.*")
                .withArguments(new IntegerArgument("seconds"))
                .executesPlayer((player, commandArguments) -> {
                    if(!(commandArguments.get("seconds") instanceof Integer seconds)){
                        player.sendMessage("Неверный формат числа");
                        return;
                    }
                    actions.setTimer(player,seconds * 20);
                });
    }
    private CommandAPICommand createCommand(){
        return new CommandAPICommand("create")
                .withPermission("mspawn.*")
                .withArguments(new StringArgument("arenaName"))
                .executesPlayer((player, commandArguments) -> {
                    actions.createBaseArena(
                            player,
                            player.getWorld(),
                            commandArguments.getRaw("arenaName"));
                });
    }
    private CommandAPICommand confirmCommand(){
        return new CommandAPICommand("confirm")
                .withPermission("mspawn.*")
                .executesPlayer((player, commandArguments) -> {
                    actions.confirmArena(player);
                });
    }
    private CommandAPICommand selectCommand(){
        return new CommandAPICommand("select")
                .withPermission("mspawn.*")
                .withArguments(new StringArgument("arenaName"))
                .executesPlayer((player, commandArguments) -> {
                    actions.selectArena(player, player.getWorld().getName(), commandArguments.getRaw("arenaName"));
                });
    }

    private CommandAPICommand setCommand(){
        return new CommandAPICommand("set")
                .withPermission("mspawn.*");
    }
    private CommandAPICommand setButtonCommand(){
        return new CommandAPICommand("button")
                .withPermission("mspawn.*")
                .executesPlayer((player, commandArguments) -> {
                    Block targetBlock = player.getTargetBlock(null ,4);
                    BlockData targetBlockData = targetBlock.getBlockData();
                    if(targetBlockData instanceof Switch sw1tch) {
                        if(Tag.BUTTONS.isTagged(sw1tch.getMaterial())){
                            actions.buttonset(
                                    player,
                                    new BlockVector3(
                                            targetBlock.getX(),
                                            targetBlock.getY(),
                                            targetBlock.getZ()
                                    ));
                        }
                    } else player.sendMessage("Вы должны смотреть на кнопку");
                });
    }
    private CommandAPICommand setRewardCommand(){
        return new CommandAPICommand("reward")
                .withPermission("mspawn.*")
                .withArguments(new IntegerArgument("amount"))
                .executesPlayer((player, commandArguments) -> {
                    if(!(commandArguments.get("amount") instanceof Integer amount)){
                        player.sendMessage("Неверный формат числа");
                        return;
                    }
                    actions.setRewardMoney(player, amount);
                });
    }
    private CommandAPICommand setRegionCommand(){
        return new CommandAPICommand("region")
                .withPermission("mspawn.*")
                .executesPlayer((player, commandArguments) -> {
                    WorldEdit we = WorldEdit.getInstance();
                    Region region;
                    LocalSession WE_session = we.getSessionManager()
                            .findByName(player.getName());
                    if(WE_session == null) {
                        player.sendMessage("Сначала выделите регион");
                        return;
                    }

                    try {
                        region = we.getSessionManager()
                                .findByName(player.getName())
                                .getSelection();
                    } catch (IncompleteRegionException e) {
                        player.sendMessage("Нужно выделить обе точки");
                        return;
                    }

                    actions.setRegion(player,new CuboidRegion(region.getMinimumPoint(), region.getMaximumPoint()));

                });
    }
    //    private CommandAPICommand startCommand(){
    //        return new CommandAPICommand("start")
    //                .withArguments(new StringArgument("arenaName"))
    //                .withPermission("mayhemspawn.activate")
    //                .executesPlayer((player, commandArguments) -> {
    //                    actions.startArena(player, commandArguments.getRaw("arenaName"));
    //                });
    //    }
}
