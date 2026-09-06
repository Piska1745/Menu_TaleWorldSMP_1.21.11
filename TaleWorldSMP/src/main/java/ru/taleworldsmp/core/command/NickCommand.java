package ru.taleworldsmp.core.command;

import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;

public class NickCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player player)) {
            sender.sendMessage("Только игрок может использовать эту команду.");
            return true;
        }

        if (args.length != 1) {
            player.sendMessage(ChatColor.RED + "Использование: /nick <ник|reset>");
            return true;
        }

        if (args[0].equalsIgnoreCase("reset")) {
            player.setDisplayName(player.getName());
            player.setPlayerListName(player.getName());
            player.sendMessage(ChatColor.GREEN + "Ник сброшен.");
            return true;
        }

        String nick = ChatColor.translateAlternateColorCodes('&', args[0]);
        player.setDisplayName(nick);
        player.setPlayerListName(nick);
        player.sendMessage(ChatColor.GREEN + "Ник изменён.");
        return true;
    }
}
