package ru.taleworldsmp.core.command;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class DemoteCommand implements CommandExecutor {

    private final JavaPlugin plugin;

    public DemoteCommand(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(
            CommandSender sender,
            Command command,
            String label,
            String[] args
    ) {
        if (args.length < 3) {
            sender.sendMessage(
                    ChatColor.RED +
                    "Использование: /demote <игрок> <ранг> <причина>"
            );
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            sender.sendMessage(
                    ChatColor.RED +
                    "Игрок не найден или не в сети."
            );
            return true;
        }

        String rank = args[1].toLowerCase();

        if (!rank.matches("[a-z0-9_-]+")) {
            sender.sendMessage(
                    ChatColor.RED +
                    "Ранг может содержать только латинские буквы, цифры, '_' и '-'."
            );
            return true;
        }

        String reason = String.join(
                " ",
                java.util.Arrays.copyOfRange(args, 2, args.length)
        );

        try {
            LuckPerms luckPerms = LuckPermsProvider.get();

            User user = luckPerms
                    .getUserManager()
                    .getUser(target.getUniqueId());

            if (user == null) {
                sender.sendMessage(
                        ChatColor.RED +
                        "Данные LuckPerms ещё не загружены."
                );
                return true;
            }

            String commandLine =
                    "lp user " +
                    target.getName() +
                    " parent set " +
                    rank;

            boolean success = Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    commandLine
            );

            if (!success) {
                sender.sendMessage(
                        ChatColor.RED +
                        "Не удалось выполнить команду LuckPerms."
                );
                return true;
            }

            sender.sendMessage(
                    ChatColor.GREEN +
                    "Игрок " +
                    target.getName() +
                    " снят до ранга " +
                    rank +
                    "."
            );

            String email = plugin.getConfig().getString(
                    "demote.support-email",
                    "taleworldsmp@gmail.com"
            );

            target.sendMessage("");
            target.sendMessage(
                    ChatColor.RED +
                    "Вы были сняты с должности."
            );
            target.sendMessage(
                    ChatColor.GRAY +
                    "Новый ранг: " +
                    ChatColor.WHITE +
                    rank
            );
            target.sendMessage(
                    ChatColor.GRAY +
                    "Причина: " +
                    ChatColor.WHITE +
                    reason
            );
            target.sendMessage("");
            target.sendMessage(
                    ChatColor.GRAY +
                    "Если это ошибка, обратитесь в поддержку:"
            );
            target.sendMessage(
                    ChatColor.GREEN +
                    email
            );
            target.sendMessage("");

        } catch (IllegalStateException e) {
            sender.sendMessage(
                    ChatColor.RED +
                    "LuckPerms не найден или ещё не готов."
            );

            plugin.getLogger().warning(
                    "Не удалось выполнить /demote: " +
                    e.getMessage()
            );
        }

        return true;
    }
}
