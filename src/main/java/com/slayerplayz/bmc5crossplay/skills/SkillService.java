package com.slayerplayz.bmc5crossplay.skills;

import com.slayerplayz.bmc5crossplay.BMC5Crossplay;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.entity.Player;

public final class SkillService {
    public enum Skill { MINING, FARMING, COMBAT, FISHING, WOODCUTTING, EXPLORATION }
    public record SkillProgress(int level, double xp) {}
    private final BMC5Crossplay plugin;
    private final Map<UUID, EnumMap<Skill, SkillProgress>> data = new ConcurrentHashMap<>();
    public SkillService(BMC5Crossplay plugin) { this.plugin = plugin; }
    public SkillProgress get(Player p, Skill skill) { return data.computeIfAbsent(p.getUniqueId(), x -> new EnumMap<>(Skill.class)).getOrDefault(skill, new SkillProgress(1, 0)); }
    public void awardXp(Player p, Skill skill, double amount) {
        if (!Double.isFinite(amount) || amount <= 0) return;
        SkillProgress s = get(p, skill); int max = plugin.getConfig().getInt("skills." + skill + ".max-level", 100); int level = s.level(); double xp = s.xp() + amount;
        while (level < max && xp >= required(level)) { xp -= required(level); level++; }
        data.computeIfAbsent(p.getUniqueId(), x -> new EnumMap<>(Skill.class)).put(skill, new SkillProgress(level, xp));
    }
    private double required(int level) { return 50 * Math.pow(1.12, level - 1); }
}