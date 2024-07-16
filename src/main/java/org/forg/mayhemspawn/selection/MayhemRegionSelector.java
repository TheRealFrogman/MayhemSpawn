package org.forg.mayhemspawn.selection;

import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.forg.mayhemspawn.integration.MayhemPlugin;
import org.forg.mayhemspawn.util.Result;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MayhemRegionSelector {
    private static final NamespacedKey PERSISTENT_KEY
            = new NamespacedKey(MayhemPlugin.getInstance(), "selector");
    private static ItemStack tool;

    private static final Map<Player, BlockVector3> playerPos1 = new HashMap<>();
    private static final Map<Player, BlockVector3> playerPos2 = new HashMap<>();
    private static final Map<Player, CuboidRegion> playerSelectedRegions = new HashMap<>();
    private MayhemRegionSelector(){}
    public static ItemStack get() {
        if (tool != null) return tool;

        tool = new ItemStack(Material.WOODEN_SHOVEL, 1);

        ItemMeta toolMeta = tool.getItemMeta();
        toolMeta.setDisplayName(ChatColor.BLUE + "Mayhem Spawn селектор");
        List<String> listLore = new ArrayList<>();
        listLore.add("Чтобы выделить регион,");
        listLore.add("выберите x и y");
        listLore.add("используйте лкм и пкм соответственно.");
        listLore.add("\n");
        listLore.add("Сохраняется между сессиями");
        toolMeta.setLore(listLore);

        toolMeta.getPersistentDataContainer().set(
                PERSISTENT_KEY,
                PersistentDataType.BOOLEAN,
                true
        );
        tool.setItemMeta(toolMeta);
        return tool;
    }
    public static boolean isValidSelector(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        if(meta == null) return false;
        PersistentDataContainer pdc = meta.getPersistentDataContainer();
        return pdc.has(PERSISTENT_KEY,PersistentDataType.BOOLEAN);
    }
    public static void selectPos_One(Player p,BlockVector3 vector) {
        playerPos1.put(p, vector);
    }
    public static void selectPos_Two(Player p,BlockVector3 vector) {
        playerPos2.put(p, vector);
    }
    public static Result<CuboidRegion> getRegionForPlayer(Player p) {
        if (playerPos1.containsKey(p) && playerPos2.containsKey(p)) {
            BlockVector3 v1 = playerPos1.get(p), v2 = playerPos2.get(p);
            CuboidRegion region = new CuboidRegion(v1,v2);
            MayhemRegionSelector.playerSelectedRegions.put(p, region);
            return Result.success(region);
        } else return Result.failure("No region");
    }
}
