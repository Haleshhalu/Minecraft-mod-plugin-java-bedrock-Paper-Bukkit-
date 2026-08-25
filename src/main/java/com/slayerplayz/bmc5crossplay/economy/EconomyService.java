package com.slayerplayz.bmc5crossplay.economy;

import com.slayerplayz.bmc5crossplay.BMC5Crossplay;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public final class EconomyService {
    private final BMC5Crossplay plugin;
    public EconomyService(BMC5Crossplay plugin) { this.plugin = plugin; }
    public CompletableFuture<Double> getBalance(UUID uuid) { return plugin.players().balance(uuid); }
    public CompletableFuture<Boolean> deposit(UUID uuid, double amount) { return change(uuid, amount); }
    public CompletableFuture<Boolean> withdraw(UUID uuid, double amount) { return change(uuid, -amount); }
    private CompletableFuture<Boolean> change(UUID uuid, double delta) {
        if (!Double.isFinite(delta)) return CompletableFuture.completedFuture(false);
        return getBalance(uuid).thenCompose(current -> {
            double next = current + delta;
            return !Double.isFinite(next) || next < 0 ? CompletableFuture.completedFuture(false) : plugin.players().setBalance(uuid, next).thenApply(v -> true);
        });
    }
    public CompletableFuture<Boolean> pay(UUID from, UUID to, double amount) {
        if (!Double.isFinite(amount) || amount <= 0 || from.equals(to)) return CompletableFuture.completedFuture(false);
        return plugin.players().transfer(from, to, amount);
    }
}