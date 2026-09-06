package ru.taleworldsmp.core.hud;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.*;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scoreboard.*;
import ru.taleworldsmp.core.storage.PlayerDataManager;

import java.util.HashMap;
import java.util.Map;

public class SidebarManager implements Listener {
    private final JavaPlugin plugin;
    private final PlayerDataManager data;
    private final Map<Player, Scoreboard> boards = new HashMap<>();

    public SidebarManager(JavaPlugin plugin, PlayerDataManager data) {
        this.plugin = plugin;
        this.data = data;

        Bukkit.getScheduler().runTaskTimer(plugin, () -> {
            for (Player player : Bukkit.getOnlinePlayers()) update(player);
        }, 1L, 20L);
    }

    @EventHandler
    public void join(PlayerJoinEvent event) {
        Bukkit.getScheduler().runTaskLater(plugin, () -> update(event.getPlayer()), 10L);
    }

    @EventHandler
    public void quit(PlayerQuitEvent event) {
        boards.remove(event.getPlayer());
    }

    public void update(Player player) {
        if (!plugin.getConfig().getBoolean("sidebar.enabled", true)) return;

        Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
        Objective objective = board.registerNewObjective("tw", Criteria.DUMMY,
                color(plugin.getConfig().getString("sidebar.title", "§aTALE WORLD SMP")));
        objective.setDisplaySlot(DisplaySlot.SIDEBAR);

        String rank = getRank(player);
        String clan = "Нет";

        add(objective, "§fНик: §a" + player.getName(), 10);
        add(objective, "§fРанг: §a" + rank, 9);
        add(objective, "§fКлан: §a" + clan, 8);
        add(objective, color(plugin.getConfig().getString("sidebar.separator")), 7);
        add(objective, "§f◈ Телики: §a" + format(data.get(player, "telix")), 6);
        add(objective, "§f◈ Монеты: §a" + format(data.get(player, "coins")), 5);
        add(objective, "§f◈ Токены: §a" + format(data.get(player, "tokens")), 4);
        add(objective, color(plugin.getConfig().getString("sidebar.separator")), 3);
        add(objective, "§aTALE WORLD SMP", 2);

        player.setScoreboard(board);
        boards.put(player, board);
    }

    private void add(Objective o, String text, int score) {
        String unique = text + "§" + score;
        o.getScore(unique).setScore(score);
    }

    private String getRank(Player player) {
        if (Bukkit.getPluginManager().getPlugin("LuckPerms") != null) {
            try {
                var lp = net.luckperms.api.LuckPermsProvider.get();
                var user = lp.getUserManager().getUser(player.getUniqueId());
                if (user != null) return user.getPrimaryGroup();
            } catch (IllegalStateException ignored) {}
        }
        return "player";
    }

    private String format(long value) {
        return String.format("%,d", value).replace(',', ' ');
    }

    private String color(String text) {
        return ChatColor.translateAlternateColorCodes('&', text == null ? "" : text);
    }
}
