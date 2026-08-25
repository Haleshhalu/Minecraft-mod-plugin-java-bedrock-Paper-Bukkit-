package com.slayerplayz.bmc5crossplay.shop;

import com.slayerplayz.bmc5crossplay.BMC5Crossplay;
import com.slayerplayz.bmc5crossplay.core.Color;
import com.slayerplayz.bmc5crossplay.items.*;
import java.util.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.ItemMeta;

public final class ShopService {
    private final BMC5Crossplay plugin;
    public ShopService(BMC5Crossplay plugin) { this.plugin = plugin; }
    public void open(Player p) {
        Inventory inv = Bukkit.createInventory(new ShopHolder(), 54, Color.component("&8BMC5 Shop"));
        int slot = 0;
        for (CustomItem item : plugin.items().all()) {
            if (slot >= 45) break;
            ItemStack display = plugin.items().create(item.id(), 1); if (display == null) continue;
            ItemMeta meta = display.getItemMeta(); List<String> lore = new ArrayList<>(item.lore());
            lore.add(Color.text("&7Buy: &6" + item.buy())); lore.add(Color.text("&7Sell: &6" + item.sell())); lore.add(Color.text("&eLeft-click to buy 1")); lore.add(Color.text("&eRight-click to sell 1"));
            meta.lore(lore.stream().map(Color::component).toList()); display.setItemMeta(meta); inv.setItem(slot++, display);
        }
        inv.setItem(49, button(Material.BARRIER, "&cClose")); p.openInventory(inv);
    }
    private ItemStack button(Material m, String name) { ItemStack i = new ItemStack(m); ItemMeta x = i.getItemMeta(); x.displayName(Color.component(name)); i.setItemMeta(x); return i; }
    public void click(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player p) || !(e.getInventory().getHolder() instanceof ShopHolder)) return;
        e.setCancelled(true);
        if (e.getRawSlot() == 49) { p.closeInventory(); return; }
        ItemStack clicked = e.getCurrentItem(); String id = plugin.items().identify(clicked); CustomItem item = plugin.items().get(id == null ? "" : id);
        if (item == null) return;
        boolean buying = e.isLeftClick(); double price = buying ? item.buy() : item.sell();
        if (!Double.isFinite(price) || price < 0) return;
        if (buying) plugin.economy().withdraw(p.getUniqueId(), price).thenAccept(ok -> Bukkit.getScheduler().runTask(plugin, () -> {
            if (!ok || !p.isOnline()) { if (!ok) p.sendMessage(plugin.message("insufficient")); return; }
            Map<Integer, ItemStack> leftover = p.getInventory().addItem(plugin.items().create(item.id(), 1));
            if (!leftover.isEmpty()) plugin.economy().deposit(p.getUniqueId(), price);
            else p.sendMessage(plugin.message("transaction").replace("{quantity}", "1").replace("{item}", item.name()).replace("{symbol}", plugin.currencySymbol()).replace("{amount}", String.valueOf(price)));
        })); else {
            ItemStack target = plugin.items().create(item.id(), 1);
            if (!p.getInventory().containsAtLeast(target, 1)) return;
            p.getInventory().removeItemAnySlot(target); plugin.economy().deposit(p.getUniqueId(), price);
            p.sendMessage(plugin.message("transaction").replace("{quantity}", "1").replace("{item}", item.name()).replace("{symbol}", plugin.currencySymbol()).replace("{amount}", String.valueOf(price)));
        }
    }
    public static final class ShopHolder implements InventoryHolder { public Inventory getInventory() { return null; } }
}