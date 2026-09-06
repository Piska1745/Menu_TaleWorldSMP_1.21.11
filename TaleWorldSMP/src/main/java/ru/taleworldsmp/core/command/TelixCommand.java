package ru.taleworldsmp.core.command;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import ru.taleworldsmp.core.economy.CurrencyManager;

public class TelixCommand implements CommandExecutor {
    private final CurrencyManager manager;
    public TelixCommand(CurrencyManager manager) { this.manager = manager; }

    @Override public boolean onCommand(CommandSender s, Command c, String label, String[] a) {
        return handle(s, a, "telix", "Телики");
    }

    public boolean handle(CommandSender s, String[] a, String key, String name) {
        if (a.length != 3) {
            s.sendMessage(ChatColor.RED + "Использование: /" + key + " <add|remove|set> <игрок> <количество>");
            return true;
        }
        Player target = Bukkit.getPlayerExact(a[1]);
        if (target == null) { s.sendMessage(ChatColor.RED + "Игрок не найден."); return true; }
        long amount;
        try { amount = Long.parseLong(a[2]); } catch (NumberFormatException e) {
            s.sendMessage(ChatColor.RED + "Количество должно быть числом."); return true;
        }
        if (amount < 0) { s.sendMessage(ChatColor.RED + "Количество не может быть отрицательным."); return true; }

        switch (a[0].toLowerCase()) {
            case "add" -> manager.add(target, key, amount);
            case "remove" -> manager.remove(target, key, amount);
            case "set" -> manager.set(target, key, amount);
            default -> { s.sendMessage(ChatColor.RED + "Действие: add, remove или set."); return true; }
        }
        s.sendMessage(ChatColor.GREEN + name + " игрока " + target.getName() + ": " + manager.get(target, key));
        return true;
    }
}
