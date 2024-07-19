package org.forg.mayhemspawn.actions;

import org.bukkit.entity.Player;

class MayhemPermissions {
    public static boolean canActivate(Player player) {
        return player.hasPermission("mayhemspawn.*")
                || player.hasPermission("mayhemspawn.activate");
    }
    public static boolean canCreate(Player player) {
        return player.hasPermission("mayhemspawn.*");
    }
    public static boolean canButtonset(Player player) {
        return player.hasPermission("mayhemspawn.*");
    }
}
