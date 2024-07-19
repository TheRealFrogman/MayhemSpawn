package org.forg.mayhemspawn.MayhemArena;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;

public class MayhemArenaBuilderRepository {
    private static final Map<Player, RewardedTimedMayhemArenaBuilder> builders = new HashMap<>();
    public void add(Player p, RewardedTimedMayhemArenaBuilder b) {
        builders.put(p, b);
    }
    public void remove(Player p) {
        builders.remove(p);
    }
    @Nullable
    public RewardedTimedMayhemArenaBuilder findByPlayer(Player p) {
        return builders.get(p);
    }
}
