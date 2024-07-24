package org.forg.mayhemspawn.integration;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import dev.jorel.commandapi.CommandAPICommand;
import dev.jorel.commandapi.arguments.IntegerArgument;
import dev.jorel.commandapi.arguments.StringArgument;
import org.bukkit.Bukkit;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.Switch;
import org.bukkit.command.CommandSender;
import org.bukkit.help.HelpTopic;
import org.bukkit.plugin.java.JavaPlugin;
import org.forg.mayhemspawn.actions.MayhemActions;

public class MayhemCommands {
    MayhemActions actions;
    public CommandAPICommand init(JavaPlugin plugin) {
        actions = new MayhemActions(plugin);
        String fullHelp = new StringBuilder()
                .append("/mspawn create <arenaName>: Создает новую арену.\n")
                .append("/mspawn set region: Устанавливает регион арены (требует выделения региона с помощью WorldEdit).\n")
                .append("/mspawn set button: Устанавливает кнопку активации арены.\n")
                .append("/mspawn set reward <amount>: Устанавливает награду за победу в арене.\n")
                .append("/mspawn set timer <seconds>: Устанавливает время арены в секундах.\n")
                .append("/mspawn confirm: Подтверждает создание арены.\n")
                .append("/mspawn select <arenaName>: Выбирает существующую арену для редактирования.")
                .toString();

        String shortHelp = new StringBuilder()
                .append("/mspawn create <arenaName> - Создать арену\n")
                .append("/mspawn set region - Установить регион\n")
                .append("/mspawn set button - Установить кнопку\n")
                .append("/mspawn set reward <amount> - Установить награду\n")
                .append("/mspawn set timer <seconds> - Установить таймер\n")
                .append("/mspawn confirm - Подтвердить\n")
                .append("/mspawn select <arenaName> - Выбрать арену для редактирования")
                .toString();

        return new CommandAPICommand("mspawn")
            .withPermission("mspawn.*")
            .withHelp(shortHelp,fullHelp)
            .withSubcommands(
                helpCommand(),
                createCommand(),
                // startCommand(),
                confirmCommand(),
                selectCommand()
            )
            .withSubcommands(
                setCommand()
                    .withSubcommands(
                        setButtonCommand(),
                        setRewardCommand(),
                        setRegionCommand(),
                        setTimerCommand()
                    )
                );
    }
    private CommandAPICommand setTimerCommand() {

        return new CommandAPICommand("timer")
                .withPermission("mspawn.*")
                .withArguments(new IntegerArgument("seconds"))
                .executesPlayer((player, commandArguments) -> {
                    try{
                        if(!(commandArguments.get("seconds") instanceof Integer seconds)){
                            player.sendMessage("Неверный формат числа");
                            return;
                        }
                        actions.setTimer(player.getUniqueId(),seconds * 20);
                        player.sendMessage("Вы установили таймер");
                    } catch (Exception e) {
                        player.sendMessage(e.getMessage());
                    }
                });
    }
    private CommandAPICommand createCommand(){
        return new CommandAPICommand("create")
                .withPermission("mspawn.*")
                .withArguments(new StringArgument("arenaName"))
                .executesPlayer((player, commandArguments) -> {
                    try{
                        actions.createBaseArena(
                                player.getUniqueId(),
                                player.getWorld(),
                                commandArguments.getRaw("arenaName"));
                        player.sendMessage("Вы создали арену, теперь настройте ее");
                    } catch (Exception e) {
                        player.sendMessage(e.getMessage());
                    }
                });
    }
    private CommandAPICommand confirmCommand(){
        return new CommandAPICommand("confirm")
                .withPermission("mspawn.*")
                .executesPlayer((player, commandArguments) -> {
                    try {
                        actions.confirmArena(player.getUniqueId());
                        player.sendMessage("Арена готова");
                    } catch (Exception e) {
                        player.sendMessage(e.getMessage());
                    }
                });
    }
    private CommandAPICommand selectCommand(){
        return new CommandAPICommand("select")
                .withPermission("mspawn.*")
                .withArguments(new StringArgument("arenaName"))
                .executesPlayer((player, commandArguments) -> {
                    try{
                        String arenaName = commandArguments.getRaw("arenaName");
                        actions.selectArena(
                                player.getUniqueId(),
                                player.getWorld().getName(),
                                arenaName
                        );
                        player.sendMessage("Вы успешно выбрали регион " + arenaName);
                    } catch (Exception e) {
                        player.sendMessage(e.getMessage());
                    }
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
                    try{
                    } catch (Exception e) {
                        player.sendMessage(e.getMessage());
                    }
                    Block targetBlock = player.getTargetBlock(null ,4);
                    BlockData targetBlockData = targetBlock.getBlockData();
                    if(targetBlockData instanceof Switch sw1tch) {
                        if(Tag.BUTTONS.isTagged(sw1tch.getMaterial())){
                            try{
                                actions.buttonset(
                                        player.getUniqueId(),
                                        new BlockVector3(
                                                targetBlock.getX(),
                                                targetBlock.getY(),
                                                targetBlock.getZ()
                                        ));
                                player.sendMessage("Вы установили кнопку");
                            } catch (Exception e) {
                                player.sendMessage(e.getMessage());
                            }
                        }
                    } else player.sendMessage("Вы должны смотреть на кнопку");
                });
    }
    private CommandAPICommand setRewardCommand(){
        return new CommandAPICommand("reward")
                .withPermission("mspawn.*")
                .withArguments(new IntegerArgument("amount"))
                .executesPlayer((player, commandArguments) -> {
                    try{
                        if(!(commandArguments.get("amount") instanceof Integer amount)){
                            player.sendMessage("Неверный формат числа");
                            return;
                        }
                        actions.setRewardMoney(player.getUniqueId(), amount);
                        player.sendMessage("Вы установили награду");
                    } catch (Exception e) {
                        player.sendMessage(e.getMessage());
                    }
                });
    }
    private CommandAPICommand setRegionCommand(){
        return new CommandAPICommand("region")
                .withPermission("mspawn.*")
                .executesPlayer((player, commandArguments) -> {
                    try{
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
                        actions.setRegion(player.getUniqueId(),new CuboidRegion(region.getMinimumPoint(), region.getMaximumPoint()));
                        player.sendMessage("Вы установили регион");
                    } catch (Exception e) {
                        player.sendMessage(e.getMessage());
                    }
                });
    }
    private CommandAPICommand helpCommand(){
        return new CommandAPICommand("help")
                .executesPlayer((player, commandArguments) -> {
                    Bukkit.getServer().dispatchCommand(player, "help mspawn");
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
