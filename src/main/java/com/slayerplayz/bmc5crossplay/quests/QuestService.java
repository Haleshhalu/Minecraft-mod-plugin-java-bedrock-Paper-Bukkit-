package com.slayerplayz.bmc5crossplay.quests;

import com.slayerplayz.bmc5crossplay.BMC5Crossplay;
import com.slayerplayz.bmc5crossplay.core.Color;
import org.bukkit.command.CommandSender;

public final class QuestService {
    private final BMC5Crossplay plugin;
    public QuestService(BMC5Crossplay plugin) { this.plugin = plugin; }
    public void list(CommandSender sender) {
        var s = plugin.getConfigFile("quests.yml").getConfigurationSection("quests");
        if (s == null) { sender.sendMessage(Color.text("&cNo quests configured.")); return; }
        sender.sendMessage(Color.text("&6Available quests:"));
        for (String id : s.getKeys(false)) sender.sendMessage(Color.text("&e" + id + " &7- " + s.getString(id + ".name", id)));
    }
    public void show(CommandSender sender, String id) {
        var c = plugin.getConfigFile("quests.yml").getConfigurationSection("quests." + id);
        if (c == null) { sender.sendMessage(Color.text("&cQuest not found.")); return; }
        sender.sendMessage(Color.text("&6" + c.getString("name", id)));
        sender.sendMessage(Color.text("&7" + c.getString("description", "")));
        sender.sendMessage(Color.text("&7Rewards: &e" + c.getDouble("rewards.xp") + " XP, " + c.getDouble("rewards.coins") + " coins"));
    }
}