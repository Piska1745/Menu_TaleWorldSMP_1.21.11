package ru.taleworldsmp.core.storage;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

public class PlayerDataManager {
    private final JavaPlugin plugin;
    private final File file;
    private final YamlConfiguration data;

    public PlayerDataManager(JavaPlugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "players.yml");
        if (!file.exists()) {
            try {
                plugin.getDataFolder().mkdirs();
                file.createNewFile();
            } catch (IOException e) {
                plugin.getLogger().severe("Не удалось создать players.yml: " + e.getMessage());
            }
        }
        this.data = YamlConfiguration.loadConfiguration(file);
    }

    private String path(Player player, String currency) {
        return player.getUniqueId() + "." + currency;
    }

    public long get(Player player, String currency) {
        return data.getLong(path(player, currency), 0L);
    }

    public void set(Player player, String currency, long amount) {
        data.set(path(player, currency), Math.max(0L, amount));
        save();
    }

    public void add(Player player, String currency, long amount) {
        set(player, currency, get(player, currency) + amount);
    }

    public void remove(Player player, String currency, long amount) {
        set(player, currency, Math.max(0L, get(player, currency) - amount));
    }

    public void save() {
        try {
            data.save(file);
        } catch (IOException e) {
            plugin.getLogger().severe("Не удалось сохранить players.yml: " + e.getMessage());
        }
    }
}
