package org.forg.mayhemspawn.MayhemArena;

import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MayhemArenaBuilderRepository {
    private static final Map<UUID, RewardedTimedMayhemArenaBuilder> builders = new HashMap<>();
    public void add(UUID playerUUID, RewardedTimedMayhemArenaBuilder b) {
        builders.put(playerUUID, b);
    }
    public void remove(UUID playerUUID) {
        builders.remove(playerUUID);
    }
    @Nullable
    public RewardedTimedMayhemArenaBuilder findByPlayer(UUID playerUUID) {
        return builders.get(playerUUID);
    }
}
