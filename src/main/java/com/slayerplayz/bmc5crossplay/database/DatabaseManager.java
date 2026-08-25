package com.slayerplayz.bmc5crossplay.database;

import com.slayerplayz.bmc5crossplay.BMC5Crossplay;
import java.io.File;
import java.sql.*;
import java.util.UUID;
import java.util.logging.Level;

public final class DatabaseManager implements AutoCloseable {
    private final BMC5Crossplay plugin;
    private Connection connection;
    public DatabaseManager(BMC5Crossplay plugin) { this.plugin = plugin; }
    public void open() throws SQLException {
        plugin.getDataFolder().mkdirs();
        connection = DriverManager.getConnection("jdbc:sqlite:" + new File(plugin.getDataFolder(), plugin.getConfig().getString("database.file", "data.db")));
        try (Statement s = connection.createStatement()) {
            s.executeUpdate("PRAGMA foreign_keys=ON");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS schema_version (version INTEGER NOT NULL)");
            s.executeUpdate("INSERT INTO schema_version SELECT 1 WHERE NOT EXISTS (SELECT 1 FROM schema_version)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS players (uuid TEXT PRIMARY KEY, name TEXT NOT NULL, rpg_level INTEGER NOT NULL DEFAULT 1, rpg_xp REAL NOT NULL DEFAULT 0)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS balances (uuid TEXT PRIMARY KEY REFERENCES players(uuid) ON DELETE CASCADE, coins REAL NOT NULL CHECK(coins >= 0))");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS skills (uuid TEXT NOT NULL, skill TEXT NOT NULL, level INTEGER NOT NULL DEFAULT 1, xp REAL NOT NULL DEFAULT 0, PRIMARY KEY(uuid, skill), FOREIGN KEY(uuid) REFERENCES players(uuid) ON DELETE CASCADE)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS quests (uuid TEXT NOT NULL, quest_id TEXT NOT NULL, progress TEXT NOT NULL DEFAULT '{}', completed INTEGER NOT NULL DEFAULT 0, PRIMARY KEY(uuid, quest_id), FOREIGN KEY(uuid) REFERENCES players(uuid) ON DELETE CASCADE)");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS backpacks (uuid TEXT PRIMARY KEY REFERENCES players(uuid) ON DELETE CASCADE, level TEXT NOT NULL, contents TEXT NOT NULL DEFAULT '[]')");
            s.executeUpdate("CREATE TABLE IF NOT EXISTS statistics (uuid TEXT NOT NULL, stat TEXT NOT NULL, value REAL NOT NULL DEFAULT 0, PRIMARY KEY(uuid, stat), FOREIGN KEY(uuid) REFERENCES players(uuid) ON DELETE CASCADE)");
        }
    }
    public synchronized Connection connection() { return connection; }
    public synchronized void ensurePlayer(UUID uuid, String name, double starting) throws SQLException {
        try (PreparedStatement p = connection.prepareStatement("INSERT OR IGNORE INTO players(uuid,name) VALUES(?,?)");
             PreparedStatement b = connection.prepareStatement("INSERT OR IGNORE INTO balances(uuid,coins) VALUES(?,?)")) {
            p.setString(1, uuid.toString()); p.setString(2, name); p.executeUpdate();
            b.setString(1, uuid.toString()); b.setDouble(2, starting); b.executeUpdate();
        }
    }
    public synchronized double balance(UUID uuid) throws SQLException {
        try (PreparedStatement p = connection.prepareStatement("SELECT coins FROM balances WHERE uuid=?")) {
            p.setString(1, uuid.toString());
            try (ResultSet r = p.executeQuery()) { return r.next() ? r.getDouble(1) : 0; }
        }
    }
    public synchronized boolean transfer(UUID from, UUID to, double amount) throws SQLException {
        if (!Double.isFinite(amount) || amount <= 0) return false;
        connection.setAutoCommit(false);
        try {
            double available = balance(from);
            if (available < amount) { connection.rollback(); return false; }
            try (PreparedStatement p = connection.prepareStatement("UPDATE balances SET coins=coins+? WHERE uuid=?")) {
                p.setDouble(1, -amount); p.setString(2, from.toString()); p.executeUpdate();
                p.setDouble(1, amount); p.setString(2, to.toString()); p.executeUpdate();
            }
            connection.commit(); return true;
        } catch (SQLException e) { connection.rollback(); throw e; } finally { connection.setAutoCommit(true); }
    }
    public synchronized void setBalance(UUID uuid, double value) throws SQLException {
        if (!Double.isFinite(value) || value < 0) throw new IllegalArgumentException("Invalid balance");
        try (PreparedStatement p = connection.prepareStatement("UPDATE balances SET coins=? WHERE uuid=?")) {
            p.setDouble(1, value); p.setString(2, uuid.toString()); p.executeUpdate();
        }
    }
    @Override public synchronized void close() {
        if (connection != null) try { connection.close(); } catch (SQLException e) { plugin.getLogger().log(Level.WARNING, "Could not close database", e); }
    }
}