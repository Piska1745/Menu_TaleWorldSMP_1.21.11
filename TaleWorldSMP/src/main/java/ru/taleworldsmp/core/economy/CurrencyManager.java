package ru.taleworldsmp.core.economy;

import org.bukkit.entity.Player;
import ru.taleworldsmp.core.storage.PlayerDataManager;

public class CurrencyManager {
    private final PlayerDataManager data;

    public CurrencyManager(PlayerDataManager data) {
        this.data = data;
    }

    public long get(Player player, String currency) {
        return data.get(player, currency);
    }

    public void add(Player player, String currency, long amount) {
        if (amount > 0) data.add(player, currency, amount);
    }

    public void remove(Player player, String currency, long amount) {
        if (amount > 0) data.remove(player, currency, amount);
    }

    public void set(Player player, String currency, long amount) {
        data.set(player, currency, Math.max(0L, amount));
    }
}
