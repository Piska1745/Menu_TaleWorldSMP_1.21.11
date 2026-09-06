package ru.taleworldsmp.core.command;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
import net.luckperms.api.model.user.User;
import net.luckperms.api.node.types.InheritanceNode;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DemoteCommand implements CommandExecutor {

    private final String defaultRank;
    private final String supportEmail;

    public DemoteCommand(String defaultRank, String supportEmail) {
        this.defaultRank = defaultRank;
        this.supportEmail = supportEmail;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 3) {
            sender.sendMessage(ChatColor.RED +
                    "Использование: /demote <игрок> <ранг> <причина>");
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED +
                    "Игрок должен находиться онлайн.");
            return true;
        }

        String newRank = args[1];

        StringBuilder reasonBuilder = new StringBuilder();

        for (int i = 2; i < args.length; i++) {
            if (i > 2) {
                reasonBuilder.append(" ");
            }
            reasonBuilder.append(args[i]);
        }

        String reason = reasonBuilder.toString();

        LuckPerms luckPerms;

        try {
            luckPerms = LuckPermsProvider.get();
        } catch (IllegalStateException e) {
            sender.sendMessage(ChatColor.RED +
                    "LuckPerms не найден.");
            return true;
        }

        User targetUser = luckPerms.getUserManager().getUser(target.getUniqueId());

        if (targetUser == null) {
            sender.sendMessage(ChatColor.RED +
                    "Не удалось получить данные LuckPerms игрока.");
            return true;
        }

        String oldRank = targetUser.getPrimaryGroup();

        // Удаляем старую основную группу.
        if (oldRank != null && !oldRank.equalsIgnoreCase("default")) {
            targetUser.data().remove(
                    InheritanceNode.builder(oldRank).build()
            );
        }

        // Устанавливаем новый ранг.
        targetUser.data().add(
                InheritanceNode.builder(newRank).build()
        );

        luckPerms.getUserManager().saveUser(targetUser);

        sender.sendMessage(ChatColor.GREEN +
                "Игрок " + target.getName() +
                " переведён на ранг " + newRank + ".");

        target.sendMessage("");
        target.sendMessage(ChatColor.RED + "Вы были сняты с должности.");
        target.sendMessage(ChatColor.GRAY + "Новый ранг: " + ChatColor.WHITE + newRank);
        target.sendMessage(ChatColor.GRAY + "Причина: " + ChatColor.WHITE + reason);
        target.sendMessage("");
        target.sendMessage(ChatColor.GRAY +
                "Если это ошибка, обратитесь в поддержку:");
        target.sendMessage(ChatColor.GREEN + supportEmail);
        target.sendMessage("");

        return true;
    }
}
