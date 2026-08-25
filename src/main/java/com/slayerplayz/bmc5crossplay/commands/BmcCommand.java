package com.slayerplayz.bmc5crossplay.commands;

import com.slayerplayz.bmc5crossplay.BMC5Crossplay;
import com.slayerplayz.bmc5crossplay.core.Color;
import com.slayerplayz.bmc5crossplay.rpg.RpgService.Stats;
import com.slayerplayz.bmc5crossplay.skills.SkillService.Skill;
import java.util.Locale;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public final class BmcCommand implements CommandExecutor, TabCompleter {
    private final BMC5Crossplay plugin;
    public BmcCommand(BMC5Crossplay plugin) { this.plugin = plugin; }
    @Override public boolean onCommand(CommandSender s, Command c, String label, String[] a) {
        String name = c.getName().toLowerCase(Locale.ROOT);
        if (name.equals("balance") || name.equals("bal") || name.equals("money")) {
            if (!(s instanceof Player p)) return true;
            plugin.economy().getBalance(p.getUniqueId()).thenAccept(v -> Bukkit.getScheduler().runTask(plugin, () -> p.sendMessage(plugin.message("balance").replace("{symbol}", plugin.currencySymbol()).replace("{amount}", String.format("%.2f", v)).replace("{currency}", plugin.currencyName())))); return true;
        }
        if (name.equals("pay")) {
            if (!(s instanceof Player p) || a.length != 2) { s.sendMessage(Color.text("&cUsage: /pay <player> <amount>")); return true; }
            Player target = Bukkit.getPlayerExact(a[0]); double amount = parse(a[1]);
            if (target == null) { s.sendMessage(plugin.message("player-not-found")); return true; }
            if (!Double.isFinite(amount) || amount <= 0) { s.sendMessage(plugin.message("invalid-amount")); return true; }
            plugin.economy().pay(p.getUniqueId(), target.getUniqueId(), amount).thenAccept(ok -> Bukkit.getScheduler().runTask(plugin, () -> { if (!ok) p.sendMessage(plugin.message("insufficient")); else { p.sendMessage(plugin.message("paid").replace("{player}", target.getName()).replace("{amount}", String.valueOf(amount)).replace("{symbol}", plugin.currencySymbol())); target.sendMessage(plugin.message("received").replace("{player}", p.getName()).replace("{amount}", String.valueOf(amount)).replace("{symbol}", plugin.currencySymbol())); } })); return true;
        }
        if (name.equals("adminmoney")) {
            if (a.length != 3) { s.sendMessage(Color.text("&cUsage: /adminmoney give|take|set <player> <amount>")); return true; }
            Player t = Bukkit.getPlayerExact(a[1]); double amount = parse(a[2]); if (t == null || !Double.isFinite(amount) || amount < 0) { s.sendMessage(plugin.message("invalid-amount")); return true; }
            plugin.economy().getBalance(t.getUniqueId()).thenCompose(old -> plugin.economy().deposit(t.getUniqueId(), nameValue(a[0]).equals("give") ? amount : nameValue(a[0]).equals("take") ? -amount : 0).thenCompose(ok -> nameValue(a[0]).equals("set") ? plugin.players().setBalance(t.getUniqueId(), amount).thenApply(v -> true) : java.util.concurrent.CompletableFuture.completedFuture(ok))).thenAccept(ok -> Bukkit.getScheduler().runTask(plugin, () -> s.sendMessage(ok ? Color.text("&aBalance updated.") : plugin.message("invalid-amount")))); return true;
        }
        if (name.equals("shop")) { if (a.length == 1 && a[0].equalsIgnoreCase("reload")) { plugin.reloadPluginConfigs(); s.sendMessage(plugin.message("reloaded")); } else if (s instanceof Player p) plugin.shop().open(p); return true; }
        if (name.equals("shopadmin")) { if (a.length > 0 && a[0].equalsIgnoreCase("reload")) { plugin.reloadPluginConfigs(); s.sendMessage(plugin.message("reloaded")); } else s.sendMessage(Color.text("&e/shopadmin reload &7or &e/shopadmin price <id> <buy> <sell>")); return true; }
        if (name.equals("stats") && s instanceof Player p) { Stats st = plugin.rpg().get(p); s.sendMessage(Color.text("&6RPG Level &e"+st.level()+" &7XP &e"+String.format("%.1f", st.xp()))); s.sendMessage(Color.text("&cHealth "+st.health()+" &bMana "+st.mana()+" &6Strength "+st.strength()+" &7Defense "+st.defense())); return true; }
        if (name.equals("skills") && s instanceof Player p) { for (Skill skill : Skill.values()) { var x = plugin.skills().get(p, skill); s.sendMessage(Color.text("&e"+skill+" &7Level &f"+x.level()+" &8XP &f"+String.format("%.1f", x.xp()))); } return true; }
        if ((name.equals("backpack") || name.equals("bp")) && s instanceof Player p) { plugin.backpack().open(p); return true; }
        if (name.equals("quests")) { plugin.quests().list(s); return true; }
        if (name.equals("quest")) { if (a.length > 0) plugin.quests().show(s, a[0]); else plugin.quests().list(s); return true; }
        if (name.equals("dungeons")) { plugin.dungeons().list(s); return true; }
        if (name.equals("bosses")) { plugin.bosses().list(s); return true; }
        if (name.equals("boss") && a.length == 2 && a[0].equalsIgnoreCase("spawn")) { plugin.bosses().spawn(s, a[1]); return true; }
        if (name.equals("iteminfo") && s instanceof Player p) { String id = plugin.items().identify(p.getInventory().getItemInMainHand()); s.sendMessage(Color.text(id == null ? "&cThis is not a configured custom item." : "&6Custom item: &e"+id)); return true; }
        if (name.equals("customitem") && a.length == 3 && a[0].equalsIgnoreCase("give")) { Player t = Bukkit.getPlayerExact(a[1]); var item = plugin.items().create(a[2], 1); if (t == null || item == null) s.sendMessage(Color.text("&cPlayer or item not found.")); else { t.getInventory().addItem(item); s.sendMessage(Color.text("&aItem given.")); } return true; }
        return false;
    }
    private double parse(String s) { try { return Double.parseDouble(s); } catch (NumberFormatException e) { return Double.NaN; } }
    private String nameValue(String s) { return s.toLowerCase(Locale.ROOT); }
    @Override public java.util.List<String> onTabComplete(CommandSender s, Command c, String l, String[] a) { return java.util.Collections.emptyList(); }
}