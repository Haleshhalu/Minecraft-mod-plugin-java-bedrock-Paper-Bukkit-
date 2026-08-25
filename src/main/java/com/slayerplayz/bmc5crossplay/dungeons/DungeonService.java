package com.slayerplayz.bmc5crossplay.dungeons;
import com.slayerplayz.bmc5crossplay.BMC5Crossplay;
import com.slayerplayz.bmc5crossplay.core.Color;
import org.bukkit.command.CommandSender;
public final class DungeonService {
 private final BMC5Crossplay plugin; public DungeonService(BMC5Crossplay plugin){this.plugin=plugin;}
 public void list(CommandSender s){var d=plugin.getConfigFile("dungeons.yml").getConfigurationSection("dungeons"); if(d==null)return; s.sendMessage(Color.text("&5Available dungeons:")); for(String id:d.getKeys(false)) s.sendMessage(Color.text("&d"+id+" &7"+d.getString(id+".name",id)+" &8["+d.getString(id+".difficulty","NORMAL")+"]")); }
}