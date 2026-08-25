package com.slayerplayz.bmc5crossplay.bosses;
import com.slayerplayz.bmc5crossplay.BMC5Crossplay;
import com.slayerplayz.bmc5crossplay.core.Color;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
public final class BossService {
 private final BMC5Crossplay plugin; public BossService(BMC5Crossplay plugin){this.plugin=plugin;}
 public void list(CommandSender s){var b=plugin.getConfigFile("bosses.yml").getConfigurationSection("bosses"); if(b==null)return; s.sendMessage(Color.text("&5Configured bosses:")); for(String id:b.getKeys(false))s.sendMessage(Color.text("&d"+id+" &7"+b.getString(id+".name",id))); }
 public void spawn(CommandSender s,String id){if(!(s instanceof Player p))return; var c=plugin.getConfigFile("bosses.yml").getConfigurationSection("bosses."+id); if(c==null){s.sendMessage(Color.text("&cBoss not found."));return;} Entity e=p.getWorld().spawnEntity(p.getLocation(), EntityType.ZOMBIE); if(e instanceof LivingEntity l){l.customName(Color.component(c.getString("name",id)));l.setCustomNameVisible(true);l.setMaxHealth(c.getDouble("health",250));l.setHealth(l.getMaxHealth());l.getPersistentDataContainer().set(plugin.bossKey(),org.bukkit.persistence.PersistentDataType.STRING,id);} s.sendMessage(Color.text("&aSpawned "+id+"."));}
}