package ru.taleworldsmp.core.command;
import org.bukkit.command.*;
import ru.taleworldsmp.core.economy.CurrencyManager;

public class CoinsCommand extends TelixCommand {
    public CoinsCommand(CurrencyManager manager) { super(manager); }
    @Override public boolean onCommand(CommandSender s, Command c, String label, String[] a) {
        return handle(s, a, "coins", "Монеты");
    }
}
