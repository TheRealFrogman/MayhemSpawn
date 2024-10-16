package org.forg.mayhemspawn.actions;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;
import org.forg.mayhemspawn.MayhemArena.MayhemArenaBuilderRepository;
import org.forg.mayhemspawn.MayhemArena.RewardedTimedMayhemArena;
import org.forg.mayhemspawn.MayhemArena.RewardedTimedMayhemArenaBuilder;
import org.forg.mayhemspawn.config.MayhemConfig;

import java.util.UUID;

public class MayhemActions  implements  IMayhemActions {
    private final MayhemArenaBuilderRepository builderRepo = new MayhemArenaBuilderRepository();
    private final JavaPlugin plugin;
    public MayhemActions(
            JavaPlugin plugin
    ) {
        this.plugin = plugin;
    }
    @Override
    public void createBaseArena(UUID playerUUID, World world, String arenaName) throws Exception {
        RewardedTimedMayhemArenaBuilder builder
                = new RewardedTimedMayhemArenaBuilder(plugin, world, arenaName);
        builderRepo.add(playerUUID, builder);
        // p.sendMessage("Вы создали арену, теперь настройте ее");
    }

    @Override
    public void confirmArena(UUID playerUUID) throws Exception {
        RewardedTimedMayhemArenaBuilder builder = builderRepo.findByPlayer(playerUUID);
        if (builder == null) {
            throw new Exception("Нет арены для подтверждения");
        }

        if (!builder.hasAtleastOneActivator()) {
            throw new Exception("Вы не установили кнопку");
        }
        if (!builder.isRegionSet()) {
            throw new Exception("Вы не установили регион");
        }
        if (!builder.isRewardSet()) {
            throw new Exception("Вы не установили награду");
        }
        if (!builder.isTimerSet()) {
            throw new Exception("Вы не установили таймер");
        }
        if (!builder.canBuild()) return;

        RewardedTimedMayhemArena arena = builder.build();
        builderRepo.remove(playerUUID);
        MayhemConfig.getInstance().addRegion(arena);
    }

    @Override
    public void selectArena(UUID playerUUID, String worldName, String arenaName) throws Exception {
        RewardedTimedMayhemArena arena = MayhemConfig.getInstance().getRegion(worldName, arenaName);

        if (arena == null) {
            throw new Exception("В конфиге нет такой арены");
        }
        var builder = new RewardedTimedMayhemArenaBuilder(plugin, arena.activeWorld, arena.arenaName)
                .setRegion(arena.region)
                .setTimerTicks(arena.timerTicks)
                .setRewardMoney(arena.reward);

        for (BlockVector3 location : arena.activatorLocations) builder.addActivator(location);

        builderRepo.add(playerUUID,builder);
    }
    @Override
    public void setRewardMoney(UUID playerUUID, int rewardMoney) throws Exception {
        RewardedTimedMayhemArenaBuilder builder = builderRepo.findByPlayer(playerUUID);
        if (builder == null) {
            throw new Exception("У вас нет выбранного региона");
        }
        builder.setRewardMoney(rewardMoney);
    }

    @Override
    public void setTimer(UUID playerUUID, int timer) throws Exception {
        RewardedTimedMayhemArenaBuilder builder = builderRepo.findByPlayer(playerUUID);
        if (builder == null) {
            throw new Exception("У вас нет выбранного региона");
        }
        builder.setTimerTicks(timer);
    }

    @Override
    public void setRegion(UUID playerUUID, CuboidRegion region) throws Exception {
        RewardedTimedMayhemArenaBuilder builder = builderRepo.findByPlayer(playerUUID);
        if (builder == null) {
            throw new Exception("У вас нет выбранной арены");
        }
        builder.setRegion(region);
    }

    @Override
    public void startArenaByButton(String worldName, BlockVector3 activatorLocation) throws Exception {
        RewardedTimedMayhemArena arena = MayhemConfig
                .getInstance()
                .getByActivatorLocation(worldName, activatorLocation);

        if (arena == null) {
            throw new Exception("Арена не найдена");
        }
        if (!arena.region.contains(activatorLocation)) {
            throw new Exception("Кнопка должна быть внутри региона");
        }
        if (arena.isStarted()) {
            throw new Exception("Арена уже начата");
        }

        arena.start();
    }

    @Override
    public void startArenaByCommand(String worldName, String arenaName) throws Exception {
        RewardedTimedMayhemArena arena = MayhemConfig
                .getInstance()
                .getRegion(worldName, arenaName);
        if (arena == null) {
            throw new Exception("Нет такой арены");
        }
        if (arena.isStarted()) {
            throw new Exception("Арена уже начата");
        } else {
            arena.start();
        }
    }
    @Override
    public void addButton(UUID playerUUID, BlockVector3 location) throws Exception {
        RewardedTimedMayhemArenaBuilder builder = builderRepo.findByPlayer(playerUUID);
        if (builder == null) {
            throw new Exception("У вас нет выбранного региона");
        }
        if(builder.region == null)
            throw new Exception("Перед установкой кнопки установите регион");
        if (!builder.region.contains(location)) {
            throw new Exception("Кнопка должна быть внутри региона");
        }
        builder.addActivator(location);
    }

}
