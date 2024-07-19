package org.forg.mayhemspawn.actions;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.forg.mayhemspawn.MayhemArena.MayhemArenaBuilderRepository;
import org.forg.mayhemspawn.MayhemArena.RewardedTimedMayhemArena;
import org.forg.mayhemspawn.MayhemArena.RewardedTimedMayhemArenaBuilder;
import org.forg.mayhemspawn.config.MayhemConfig;


// этот класс проверяет на пермишен и выполняет логику
// также отправляет игроку уведомление специфичные экшену
// на уровне интеграции с командами тоже отправляются сообщения, но только о невалидности команды
public class MayhemActions implements IMayhemActions {
    private final MayhemArenaBuilderRepository repo = new MayhemArenaBuilderRepository();
    private final JavaPlugin plugin;
    public MayhemActions(
            JavaPlugin plugin
    ) {
        this.plugin = plugin;
    }
    @Override
    public void createBaseArena(Player p, World world, String arenaName) {
        if (MayhemPermissions.canCreate(p)) {
            RewardedTimedMayhemArenaBuilder builder
                    = new RewardedTimedMayhemArenaBuilder(plugin, world, arenaName);
            repo.add(p,builder);
            p.sendMessage("Вы создали арену, теперь настройте ее");
        } else {
            p.sendMessage("Нет прав на создание арены");
        }
    }

    @Override
    public void confirmArena(Player p) {
        if (MayhemPermissions.canCreate(p)) {
            try {
                RewardedTimedMayhemArenaBuilder builder = repo.findByPlayer(p);
                if(builder == null) {
                    p.sendMessage("Нет арены для подтверждения");
                    return;
                }

                if(builder.activatorLocation == null) {
                    p.sendMessage("Вы не установили кнопку");
                }
                if(builder.region == null) {
                    p.sendMessage("Вы не установили регион");
                }

                if(builder.reward < 1) {
                    p.sendMessage("Вы не установили награду");
                }

                if(builder.timerTicks < 1) {
                    p.sendMessage("Вы не установили таймер");
                }
                RewardedTimedMayhemArena arena;
                try {
                    arena = builder.build();
                } catch (RuntimeException err) {
                    return;
                }
                repo.remove(p);
                MayhemConfig.getInstance().addRegion(arena);
                p.sendMessage("Арена готова");
            } catch (NullPointerException e) {
                p.sendMessage("У вас не выбрана арена");
            }
        } else p.sendMessage("Нет прав на создание арены");
    }

    @Override
    public void selectArena(Player p, String worldName, String arenaName) {
        if (MayhemPermissions.canCreate(p)) {
        RewardedTimedMayhemArena arena = MayhemConfig.getInstance().getRegion(worldName, arenaName);

        if(arena == null) {
            p.sendMessage("В конфиге нет такой арены");
            return;
        }
        repo.add(
                p,
                new RewardedTimedMayhemArenaBuilder(plugin, arena.activeWorld, arena.arenaName)
                        .setActivator(arena.activatorLocation)
                        .setRegion(arena.region)
                        .setTimerTicks(arena.timerTicks)
                        .setRewardMoney(arena.reward)
        );
            p.sendMessage("Вы успешно выбрали регион " + arenaName);
        } else p.sendMessage("Нет прав на создание арены");
    }


    @Override
    public void setRewardMoney(Player p, int rewardMoney) {
        if (MayhemPermissions.canCreate(p)) {
            RewardedTimedMayhemArenaBuilder builder = repo.findByPlayer(p);
            if(builder == null) {
                p.sendMessage("У вас нет выбранного региона");
                return;
            }
            builder.setRewardMoney(rewardMoney);
            p.sendMessage("Вы установили награду");
        } else p.sendMessage("Нет прав на создание арены");
    }

    @Override
    public void setTimer(Player p, int timer) {
        if (MayhemPermissions.canCreate(p)) {
            RewardedTimedMayhemArenaBuilder builder = repo.findByPlayer(p);
            if(builder == null) {
                p.sendMessage("У вас нет выбранного региона");
                return;
            }
            builder.setTimerTicks(timer);
            p.sendMessage("Вы установили таймер");
        } else p.sendMessage("Нет прав на создание арены");
    }

    @Override
    public void setRegion(Player p, CuboidRegion region) {
        if (MayhemPermissions.canCreate(p)) {
            RewardedTimedMayhemArenaBuilder builder = repo.findByPlayer(p);
            if(builder == null) {
                p.sendMessage("У вас нет выбранной арены");
                return;
            }
            builder.setRegion(region);
            p.sendMessage("Вы установили регион");
        } else p.sendMessage("Нет прав на создание арены");
    }

    @Override
    public void startArenaByButton(Player p, BlockVector3 activatorLocation) {
        if (!MayhemPermissions.canActivate(p)) {
            p.sendMessage("Нет прав на активацию");
            return;
        }

        RewardedTimedMayhemArena arena = MayhemConfig
                .getInstance()
                .getByActivatorLocation(p.getWorld().getName(),activatorLocation);

        if(arena == null) {
//            p.sendMessage("Нет такой арены");
            return;
        }
        if (!arena.region.contains(activatorLocation)) {
            p.sendMessage("Кнопка должна быть внутри региона");
            return;
        }
        if (arena.isStarted()) {
            p.sendMessage("Арена уже начата");
            return;
        }
//        if (!arena.isEnoughPlayers()) {
//            p.sendMessage("Недостаточно игроков");
//            return;
//        }
        arena.startArenaCountdown(p);
        p.sendMessage("Вы начали арену");
    }

    @Override
    public void startArenaByCommand(Player p, String arenaName) {
        if (!MayhemPermissions.canActivate(p)) {
            p.sendMessage("Нет прав на активацию");
            return;
        }

        RewardedTimedMayhemArena arena = MayhemConfig
                .getInstance()
                .getRegion(p.getWorld().getName(), arenaName);

        if(arena == null) {
            p.sendMessage("Нет такой арены");
            return;
        }
        if (arena.isStarted()) {
            p.sendMessage("Арена уже начата");
        } else {
            arena.startArenaCountdown(p);
            p.sendMessage("Вы начали арену");
        }
    }

    @Override
    public void buttonset(Player p, BlockVector3 location) {
        if (MayhemPermissions.canButtonset(p)) {
            RewardedTimedMayhemArenaBuilder builder = repo.findByPlayer(p);
            if(builder == null) {
                p.sendMessage("У вас нет выбранного региона");
                return;
            }
            if (!builder.region.contains(location)) {
                p.sendMessage("Кнопка должна быть внутри региона");
                return;
            }
            builder.setActivator(location);
            p.sendMessage("Вы установили кнопку");
        } else p.sendMessage("Нет прав на создание арены");
    }
}
