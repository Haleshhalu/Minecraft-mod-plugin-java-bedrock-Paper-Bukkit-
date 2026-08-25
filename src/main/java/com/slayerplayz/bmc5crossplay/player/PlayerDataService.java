package com.slayerplayz.bmc5crossplay.player;

import com.slayerplayz.bmc5crossplay.BMC5Crossplay;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import org.bukkit.entity.Player;

public final class PlayerDataService {
    private final BMC5Crossplay plugin;
    public PlayerDataService(BMC5Crossplay plugin) { this.plugin = plugin; }
    public void load(Player player) {
        async(() -> { plugin.database().ensurePlayer(player.getUniqueId(), player.getName(), plugin.getConfig().getDouble("economy.starting-balance", 100)); return null; });
    }
    public CompletableFuture<Double> balance(UUID uuid) { return async(() -> plugin.database().balance(uuid)); }
    public CompletableFuture<Boolean> transfer(UUID from, UUID to, double amount) { return async(() -> plugin.database().transfer(from, to, amount)); }
    public CompletableFuture<Void> setBalance(UUID uuid, double value) { return async(() -> { plugin.database().setBalance(uuid, value); return null; }); }
    private <T> CompletableFuture<T> async(SqlWork<T> work) {
        return CompletableFuture.supplyAsync(() -> { try { return work.run(); } catch (SQLException e) { throw new IllegalStateException("Database operation failed", e); } }, plugin.ioExecutor());
    }
    @FunctionalInterface private interface SqlWork<T> { T run() throws SQLException; }
}