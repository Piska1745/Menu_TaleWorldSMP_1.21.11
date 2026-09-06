```java
package ru.taleworldsmp.core.command;

import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;
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

        // /demote <игрок> <ранг> <причина>
        if (args.length < 3) {
            sender.sendMessage(
                    ChatColor.RED
                            + "Использование: /demote <игрок> <ранг> <причина>"
            );
            return true;
        }

        Player target = Bukkit.getPlayerExact(args[0]);

        if (target == null) {
            sender.sendMessage(ChatColor.RED + "Игрок не найден.");
            return true;
        }

        String newRank = args[1].toLowerCase();
        String reason = String.join(
                " ",
                java.util.Arrays.copyOfRange(args, 2, args.length)
        );

        try {
            LuckPerms luckPerms = LuckPermsProvider.get();

            // Проверяем, существует ли такой ранг в LuckPerms.
            if (luckPerms.getGroupManager().getGroup(newRank) == null) {
                sender.sendMessage(
                        ChatColor.RED
                                + "Ранг "
                                + ChatColor.WHITE
                                + newRank
                                + ChatColor.RED
                                + " не существует в LuckPerms."
                );
                return true;
            }

            /*
             * Используем официальную команду LuckPerms:
             *
             * /lp user <player> parent set <group>
             *
             * parent set заменяет текущие родительские группы
             * и обновляет primary group.
             */
            String commandToRun =
                    "lp user "
                            + target.getName()
                            + " parent set "
                            + newRank;

            boolean dispatched = Bukkit.dispatchCommand(
                    Bukkit.getConsoleSender(),
                    commandToRun
            );

            if (!dispatched) {
                sender.sendMessage(
                        ChatColor.RED
                                + "LuckPerms не смог выполнить смену ранга."
                );
                return true;
            }

            // Проверяем результат после выполнения команды.
            net.luckperms.api.model.user.User user =
                    luckPerms.getUserManager()
                            .getUser(target.getUniqueId());

            if (user == null) {
                sender.sendMessage(
                        ChatColor.RED
                                + "Не удалось проверить данные игрока в LuckPerms."
                );
                return true;
            }

            String actualRank = user.getPrimaryGroup();

            if (!actualRank.equalsIgnoreCase(newRank)) {
                sender.sendMessage(
                        ChatColor.RED
                                + "Ранг не был изменён. "
                                + "LuckPerms вернул: "
                                + actualRank
                );
                return true;
            }

            // Успешно.
            sender.sendMessage(
                    ChatColor.GREEN
                            + "Игрок "
                            + ChatColor.WHITE
                            + target.getName()
                            + ChatColor.GREEN
                            + " снят до ранга "
                            + ChatColor.WHITE
                            + newRank
                            + ChatColor.GREEN
                            + "."
            );

            String email = plugin.getConfig().getString(
                    "demote.support-email",
                    "taleworldsmp@gmail.com"
            );

            target.sendMessage("");
            target.sendMessage(
                    ChatColor.RED
                            + "Вы были сняты с должности."
            );

            target.sendMessage(
                    ChatColor.GRAY
                            + "Новый ранг: "
                            + ChatColor.WHITE
                            + newRank
            );

            target.sendMessage(
                    ChatColor.GRAY
                            + "Причина: "
                            + ChatColor.WHITE
                            + reason
            );

            target.sendMessage("");

            target.sendMessage(
                    ChatColor.GRAY
                            + "Если это ошибка, обратитесь в поддержку:"
            );

            target.sendMessage(
                    ChatColor.GREEN
                            + email
            );

            target.sendMessage("");

        } catch (IllegalStateException e) {
            sender.sendMessage(
                    ChatColor.RED
                            + "LuckPerms не найден или ещё не готов."
            );

            plugin.getLogger().severe(
                    "LuckPerms API недоступен: " + e.getMessage()
            );
        } catch (Exception e) {
            sender.sendMessage(
                    ChatColor.RED
                            + "Произошла ошибка при изменении ранга."
            );

            plugin.getLogger().severe(
                    "Ошибка /demote для игрока "
                            + target.getName()
                            + ": "
                            + e.getMessage()
            );
        }

        return true;
    }
}
```

### Что теперь делает `/demote`

Например:

```text
/demote Steve player нарушение правил
```

Плагин:

1. Проверяет, что `Steve` онлайн.
2. Проверяет, что группа `player` реально существует в LuckPerms.
3. Выполняет через LuckPerms:

   ```text
   lp user Steve parent set player
   ```
4. Проверяет, что `primaryGroup` действительно стал `player`.
5. Только после успешной проверки сообщает об успешном снятии.
6. Самому игроку отправляет причину и `taleworldsmp@gmail.com`.

Это опирается на документированное поведение LuckPerms `parent set`: команда очищает существующие группы в соответствующем контексте и обновляет primary group.

**И главное:** этот вариант не содержит ни одной из двух проблем, из-за которых у тебя сейчас падала компиляция: `targetUser` и `clear(InheritanceNode)`.

В Codespaces просто полностью замени файл `DemoteCommand.java` этим кодом и запускай:

```bash
cd /workspaces/Menu_TaleWorldSMP_1.21.11/TaleWorldSMP
mvn clean package
```

Если сборка после **именно этого файла** выдаст ошибку, пришли весь вывод `mvn clean package` — тогда будем исправлять уже конкретную ошибку, а не гадать.
