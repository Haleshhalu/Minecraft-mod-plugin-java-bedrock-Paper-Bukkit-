package com.slayerplayz.bmc5crossplay.listeners;
import com.slayerplayz.bmc5crossplay.BMC5Crossplay;
import com.slayerplayz.bmc5crossplay.backpack.BackpackService;
import org.bukkit.event.*;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerJoinEvent;
public final class GameplayListener implements Listener {
 private final BMC5Crossplay plugin; public GameplayListener(BMC5Crossplay plugin){this.plugin=plugin;}
 @EventHandler public void join(PlayerJoinEvent e){plugin.players().load(e.getPlayer());}
 @EventHandler public void click(InventoryClickEvent e){plugin.shop().click(e); plugin.backpack().click(e);}
 @EventHandler public void drag(InventoryDragEvent e){if(e.getInventory().getHolder() instanceof BackpackService.BackpackHolder || e.getInventory().getHolder() instanceof com.slayerplayz.bmc5crossplay.shop.ShopService.ShopHolder)e.setCancelled(true);}
 @EventHandler public void close(InventoryCloseEvent e){}
}