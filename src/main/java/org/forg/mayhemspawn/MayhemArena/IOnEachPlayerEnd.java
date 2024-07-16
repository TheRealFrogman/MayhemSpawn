package org.forg.mayhemspawn.MayhemArena;

import org.bukkit.entity.Player;

public interface IOnEachPlayerEnd {
    void run(Player p, boolean isWinner);
}
