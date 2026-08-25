package com.slayerplayz.bmc5crossplay.backpack;

import com.slayerplayz.bmc5crossplay.BMC5Crossplay;
import com.slayerplayz.bmc5crossplay.core.Color;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.NamespacedKey;
import org.bukkit.persistence.PersistentDataType;

public final class BackpackService {
    private final BMC5Crossplay plugin;
    private final NamespacedKey backpackKey;
    public BackpackService(BMC5Crossplay plugin) { this.plugin = plugin; this.backpackKey = new NamespacedKey(plugin, "backpack"); }
    public void open(Player p) {
        String level = plugin.getConfig().getString("backpack.default-level", "SMALL");
        int slots = plugin.getConfig().getInt("backpack.levels." + level + ".slots", 9);
        p.openInventory(Bukkit.createInventory(new BackpackHolder(), slots, Color.component("&8Backpack &7(" + level + ")")));
    }
    public void click(InventoryClickEvent e) {
        if (!(e.getInventory().getHolder() instanceof BackpackHolder)) return;
        if (e.isShiftClick() || e.getClick() == ClickType.NUMBER_KEY || e.getClick() == ClickType.DOUBLE_CLICK || e.getClick() == ClickType.SWAP_OFFHAND) e.setCancelled(true);
        ItemStack item = e.getCurrentItem();
        if (item != null && item.getType() != Material.AIR && item.hasItemMeta()
                && item.getItemMeta().getPersistentDataContainer().has(backpackKey, PersistentDataType.BYTE)) e.setCancelled(true);
    }
    public static final class BackpackHolder implements InventoryHolder { public Inventory getInventory() { return null; } }
}