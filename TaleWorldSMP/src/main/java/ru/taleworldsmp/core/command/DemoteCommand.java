package ru.taleworldsmp.core.command;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DemoteCommand implements CommandExecutor {
    private final JavaPlugin plugin;

    public DemoteCommand(JavaPlugin plugin) { this.plugin = plugin; }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED + "Использование: /demote <игрок> <ранг> <причина>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);
        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок не найден.");
            return true;
        }

        String rank = args[1].toLowerCase();
        String reason = String.join(" ", java.util.Arrays.copyOfRange(args, 2, args.length));

        try {
            LuckPerms lp = LuckPermsProvider.get();
            User user = lp.getUserManager().getUser(target.getUniqueId());
            if (user == null) {
                sender.sendMessage(ChatColor.RED + "Данные LuckPerms ещё не загружены.");
                return true;
            }

            String oldPrimary = user.getPrimaryGroup();

            user.data().clear(InheritanceNode.builder(oldPrimary).build());
            user.data().add(InheritanceNode.builder(rank).build());

            lp.getUserManager().saveUser(user);

            sender.sendMessage(ChatColor.GREEN + "Игрок " + target.getName()
                    + " снят до ранга " + rank + ".");

            String email = plugin.getConfig().getString("demote.support-email", "taleworldsmp@gmail.com");
            target.sendMessage("");
            target.sendMessage(ChatColor.RED + "Вы были сняты с должности.");
            target.sendMessage(ChatColor.GRAY + "Новый ранг: " + ChatColor.WHITE + rank);
            target.sendMessage(ChatColor.GRAY + "Причина: " + ChatColor.WHITE + reason);
            target.sendMessage("");
            target.sendMessage(ChatColor.GRAY + "Если это ошибка, обратитесь в поддержку:");
            target.sendMessage(ChatColor.GREEN + email);
            target.sendMessage("");
        } catch (IllegalStateException e) {
            sender.sendMessage(ChatColor.RED + "LuckPerms не найден или ещё не готов.");
        }

        return true;
    }
}
