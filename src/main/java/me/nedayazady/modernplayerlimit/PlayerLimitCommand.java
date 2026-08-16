package me.nedayazady.modernplayerlimit;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

public class PlayerLimitCommand implements SimpleCommand {

    private final ModernPlayerLimit plugin;

    public PlayerLimitCommand(ModernPlayerLimit plugin) {
        this.plugin = plugin;
    }

    @Override
    public void execute(Invocation invocation) {
        CommandSource source = invocation.source();
        String[] args = invocation.arguments();

        if (args.length == 0) {
            int currentLimit = plugin.getPlayerLimit();
            String limitDisplay = currentLimit > 0 ? String.valueOf(currentLimit) : "Unlimited";
            source.sendMessage(Component.text("Current player limit is: " + limitDisplay, NamedTextColor.YELLOW));
            return;
        }

        if (args.length == 1) {
            try {
                int newLimit = Integer.parseInt(args[0]);
                if (newLimit < 0) {
                    source.sendMessage(Component.text("Player limit cannot be negative.", NamedTextColor.RED));
                    return;
                }
                plugin.setPlayerLimit(newLimit);
                String limitDisplay = newLimit > 0 ? String.valueOf(newLimit) : "Unlimited";
                source.sendMessage(Component.text("Player limit has been set to: " + limitDisplay, NamedTextColor.GREEN));
            } catch (NumberFormatException e) {
                source.sendMessage(Component.text("Invalid number provided.", NamedTextColor.RED));
            }
        } else {
            source.sendMessage(Component.text("Usage: /playerlimit [amount]", NamedTextColor.RED));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("playerlimit.admin");
    }
}
