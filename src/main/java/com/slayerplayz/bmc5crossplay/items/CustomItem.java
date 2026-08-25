package com.slayerplayz.bmc5crossplay.items;

import java.util.List;
import org.bukkit.Material;

public record CustomItem(String id, Material material, String name, List<String> lore, String rarity, int modelData, double buy, double sell) {}