package com.slayerplayz.bmc5crossplay.rpg;

import com.slayerplayz.bmc5crossplay.BMC5Crossplay;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public final class RpgService {
    public record Stats(int level, double xp, double health, double mana, double strength, double defense, double dexterity, double magic, double luck) {}
    private final BMC5Crossplay plugin;
    private final Map<UUID, Stats> stats = new ConcurrentHashMap<>();
    public RpgService(BMC5Crossplay plugin) { this.plugin = plugin; }
    public Stats get(Player p) { return stats.computeIfAbsent(p.getUniqueId(), id -> new Stats(1, 0, 20, 100, 1, 1, 1, 1, 1)); }
    public void awardXp(Player p, double amount) {
        if (!Double.isFinite(amount) || amount <= 0) return;
        Stats s = get(p); double xp = s.xp() + amount; int level = s.level();
        int max = plugin.getConfig().getInt("rpg.max-level", 100);
        while (level < max && xp >= requiredXp(level)) { xp -= requiredXp(level); level++; p.sendMessage(plugin.message("level-up").replace("{level}", String.valueOf(level))); }
        stats.put(p.getUniqueId(), new Stats(level, xp, s.health(), s.mana(), s.strength(), s.defense(), s.dexterity(), s.magic(), s.luck()));
    }
    public double requiredXp(int level) { return plugin.getConfig().getDouble("rpg.xp-base", 100) * Math.pow(plugin.getConfig().getDouble("rpg.xp-growth", 1.15), Math.max(0, level - 1)); }
}