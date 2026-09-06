package ru.taleworldsmp.core.command;
import org.bukkit.command.*;
import ru.taleworldsmp.core.economy.CurrencyManager;

public class TokensCommand extends TelixCommand {
    public TokensCommand(CurrencyManager manager) { super(manager); }
    @Override public boolean onCommand(CommandSender s, Command c, String label, String[] a) {
        return handle(s, a, "tokens", "Токены");
    }
}
