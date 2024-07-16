package org.forg.mayhemspawn.actions;

import org.bukkit.entity.Player;

public class MayhemPermissions {
    public static boolean canActivate(Player player) {
        return player.hasPermission("mayhemspawn.*")
                || player.hasPermission("mayhemspawn.activate");
    }
    public static boolean canTool(Player player) {
        return player.hasPermission("mayhemspawn.*") || player.hasPermission("mayhemspawn.tool");
    }
    public static boolean canCreate(Player player) {
        return player.hasPermission("mayhemspawn.*");
    }
    public static boolean canButtonset(Player player) {
        return player.hasPermission("mayhemspawn.*");
    }
    public static boolean canSelect(Player player) {
        return player.hasPermission("mayhemspawn.*")
                || player.hasPermission("mayhemspawn.select");
    }
}
