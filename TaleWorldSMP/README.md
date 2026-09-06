# TaleWorldSMP

Core-плагин для Spigot 1.21.11.

## Команды

- `/telix add <игрок> <количество>`
- `/telix remove <игрок> <количество>`
- `/telix set <игрок> <количество>`
- `/coins add <игрок> <количество>`
- `/coins remove <игрок> <количество>`
- `/coins set <игрок> <количество>`
- `/tokens add <игрок> <количество>`
- `/tokens remove <игрок> <количество>`
- `/tokens set <игрок> <количество>`
- `/demote <игрок> <ранг> <причина>`
- `/nick <ник|reset>`

## LuckPerms

Выдай нужным группам:

`taleworldsmp.currency.manage`

`taleworldsmp.demote`

`taleworldsmp.nick`

Например, для группы `dadmin`:

`/lp group dadmin permission set taleworldsmp.currency.manage true`

`/lp group admin permission set taleworldsmp.demote true`

Настрой наследование групп в LuckPerms, чтобы старшие ранги получали permissions автоматически.

## Сборка

Требуется Java 21 и Maven:

`mvn clean package`

Готовый файл появится в:

`target/TaleWorldSMP.jar`

После этого загрузите JAR в папку plugins вашего сервера.
