package ru.taleworldsmp.core;

import org.bukkit.plugin.java.JavaPlugin;
import ru.taleworldsmp.core.command.CoinsCommand;
import ru.taleworldsmp.core.command.DemoteCommand;
import ru.taleworldsmp.core.command.NickCommand;
import ru.taleworldsmp.core.command.TelixCommand;
import ru.taleworldsmp.core.command.TokensCommand;
import ru.taleworldsmp.core.economy.CurrencyManager;
import ru.taleworldsmp.core.hud.SidebarManager;
import ru.taleworldsmp.core.storage.PlayerDataManager;

public final class TaleWorldSMP extends JavaPlugin {
    private PlayerDataManager dataManager;
    private CurrencyManager currencyManager;
    private SidebarManager sidebarManager;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        dataManager = new PlayerDataManager(this);
        currencyManager = new CurrencyManager(dataManager);
        sidebarManager = new SidebarManager(this, dataManager);

        getCommand("telix").setExecutor(new TelixCommand(currencyManager));
        getCommand("coins").setExecutor(new CoinsCommand(currencyManager));
        getCommand("tokens").setExecutor(new TokensCommand(currencyManager));
        getCommand("demote").setExecutor(new DemoteCommand(this));
        getCommand("nick").setExecutor(new NickCommand());

        getServer().getPluginManager().registerEvents(sidebarManager, this);

        getLogger().info("TaleWorldSMP enabled.");
    }

    @Override
    public void onDisable() {
        if (dataManager != null) {
            dataManager.save();
        }
    }

    public CurrencyManager getCurrencyManager() {
        return currencyManager;
    }
}
