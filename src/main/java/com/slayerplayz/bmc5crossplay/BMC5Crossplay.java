package com.slayerplayz.bmc5crossplay;

import com.slayerplayz.bmc5crossplay.backpack.BackpackService;
import com.slayerplayz.bmc5crossplay.bosses.BossService;
import com.slayerplayz.bmc5crossplay.commands.BmcCommand;
import com.slayerplayz.bmc5crossplay.core.Color;
import com.slayerplayz.bmc5crossplay.database.DatabaseManager;
import com.slayerplayz.bmc5crossplay.dungeons.DungeonService;
import com.slayerplayz.bmc5crossplay.economy.EconomyService;
import com.slayerplayz.bmc5crossplay.items.CustomItemRegistry;
import com.slayerplayz.bmc5crossplay.listeners.GameplayListener;
import com.slayerplayz.bmc5crossplay.player.PlayerDataService;
import com.slayerplayz.bmc5crossplay.quests.QuestService;
import com.slayerplayz.bmc5crossplay.rpg.RpgService;
import com.slayerplayz.bmc5crossplay.shop.ShopService;
import com.slayerplayz.bmc5crossplay.skills.SkillService;
import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class BMC5Crossplay extends JavaPlugin {
    private ExecutorService io; private DatabaseManager database; private PlayerDataService players; private EconomyService economy; private CustomItemRegistry items; private ShopService shop; private RpgService rpg; private SkillService skills; private BackpackService backpack; private QuestService quests; private DungeonService dungeons; private BossService bosses; private NamespacedKey bossKey;
    @Override public void onEnable() {
        saveDefaultConfig(); for (String file : new String[]{"messages.yml","items.yml","shops.yml","skills.yml","quests.yml","dungeons.yml","bosses.yml"}) saveResource(file, false);
        io = Executors.newFixedThreadPool(2, r -> { Thread t = new Thread(r, "BMC5Crossplay-IO"); t.setDaemon(true); return t; });
        database = new DatabaseManager(this); try { database.open(); } catch (Exception e) { getLogger().log(Level.SEVERE, "Database initialization failed", e); getServer().getPluginManager().disablePlugin(this); return; }
        players = new PlayerDataService(this); economy = new EconomyService(this); items = new CustomItemRegistry(this); shop = new ShopService(this); rpg = new RpgService(this); skills = new SkillService(this); backpack = new BackpackService(this); quests = new QuestService(this); dungeons = new DungeonService(this); bosses = new BossService(this); bossKey = new NamespacedKey(this, "boss_id");
        BmcCommand command = new BmcCommand(this); for (String n : new String[]{"balance","bal","money","pay","adminmoney","shop","shopadmin","stats","skills","iteminfo","customitem","backpack","bp","quests","quest","dungeons","bosses","boss"}) { var c = getCommand(n); if (c != null) { c.setExecutor(command); c.setTabCompleter(command); } }
        getServer().getPluginManager().registerEvents(new GameplayListener(this), this); getLogger().info("BMC5Crossplay 0.1.0 enabled.");
    }
    @Override public void onDisable() { if (database != null) database.close(); if (io != null) { io.shutdown(); try { io.awaitTermination(5, TimeUnit.SECONDS); } catch (InterruptedException e) { Thread.currentThread().interrupt(); } } }
    public FileConfiguration getConfigFile(String name) { return YamlConfiguration.loadConfiguration(new File(getDataFolder(), name)); }
    public void reloadPluginConfigs() { reloadConfig(); items.reload(); }
    public String message(String key) { return Color.text(getConfigFile("messages.yml").getString(key, key)).replace("{prefix}", Color.text(getConfigFile("messages.yml").getString("prefix", ""))); }
    public String currencySymbol(){return getConfig().getString("economy.currency-symbol","⛃");} public String currencyName(){return getConfig().getString("economy.currency-name","Coins");}
    public ExecutorService ioExecutor(){return io;} public DatabaseManager database(){return database;} public PlayerDataService players(){return players;} public EconomyService economy(){return economy;} public CustomItemRegistry items(){return items;} public ShopService shop(){return shop;} public RpgService rpg(){return rpg;} public SkillService skills(){return skills;} public BackpackService backpack(){return backpack;} public QuestService quests(){return quests;} public DungeonService dungeons(){return dungeons;} public BossService bosses(){return bosses;} public NamespacedKey bossKey(){return bossKey;}
}