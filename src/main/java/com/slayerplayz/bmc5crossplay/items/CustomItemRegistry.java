package com.slayerplayz.bmc5crossplay.items;

import com.slayerplayz.bmc5crossplay.BMC5Crossplay;
import com.slayerplayz.bmc5crossplay.core.Color;
import java.util.*;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

public final class CustomItemRegistry {
    private final BMC5Crossplay plugin;
    private final Map<String, CustomItem> items = new LinkedHashMap<>();
    private final NamespacedKey itemKey;
    public CustomItemRegistry(BMC5Crossplay plugin) { this.plugin = plugin; this.itemKey = new NamespacedKey(plugin, "custom_item"); reload(); }
    public void reload() {
        items.clear();
        var section = plugin.getConfigFile("items.yml").getConfigurationSection("items");
        if (section == null) return;
        for (String id : section.getKeys(false)) {
            var c = section.getConfigurationSection(id); if (c == null) continue;
            Material material = Material.matchMaterial(c.getString("material", "STONE"));
            if (material == null) { plugin.getLogger().warning("Ignoring item with invalid material: " + id); continue; }
            List<String> lore = c.getStringList("lore").stream().map(Color::text).toList();
            items.put(id.toLowerCase(Locale.ROOT), new CustomItem(id, material, Color.text(c.getString("name", id)), lore, c.getString("rarity", "COMMON"), c.getInt("custom-model-data"), c.getDouble("buy-price", -1), c.getDouble("sell-price", -1)));
        }
    }
    public Collection<CustomItem> all() { return Collections.unmodifiableCollection(items.values()); }
    public CustomItem get(String id) { return items.get(id.toLowerCase(Locale.ROOT)); }
    public ItemStack create(String id, int amount) {
        CustomItem item = get(id); if (item == null || amount < 1) return null;
        ItemStack stack = new ItemStack(item.material(), Math.min(amount, item.material().getMaxStackSize()));
        ItemMeta meta = stack.getItemMeta(); meta.displayName(Color.component(item.name())); meta.lore(item.lore().stream().map(Color::component).toList());
        if (item.modelData() > 0) meta.setCustomModelData(item.modelData());
        meta.getPersistentDataContainer().set(itemKey, PersistentDataType.STRING, item.id());
        stack.setItemMeta(meta); return stack;
    }
    public String identify(ItemStack stack) {
        if (stack == null || !stack.hasItemMeta()) return null;
        return stack.getItemMeta().getPersistentDataContainer().get(itemKey, PersistentDataType.STRING);
    }
}