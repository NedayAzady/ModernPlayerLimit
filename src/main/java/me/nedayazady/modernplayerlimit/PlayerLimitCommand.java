package me.nedayazady.modernplayerlimit;

import com.velocitypowered.api.command.CommandSource;
import com.velocitypowered.api.command.SimpleCommand;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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
            source.sendMessage(plugin.color(plugin.getMessage("usage")));
            return;
        }

        if (args.length == 1) {
            if (args[0].equalsIgnoreCase("reload")) {
                if (source.hasPermission("playerlimit.reload")) {
                    plugin.reload();
                    source.sendMessage(plugin.color(plugin.getMessage("reload_success")));
                } else {
                    source.sendMessage(plugin.color(plugin.getMessage("no_permission")));
                }
                return;
            }

            if (args[0].equalsIgnoreCase("info")) {
                if (source.hasPermission("playerlimit.admin")) {
                    int currentLimit = plugin.getPlayerLimit();
                    String limitDisplay = currentLimit > 0 ? String.valueOf(currentLimit) : "Unlimited";
                    String msg = plugin.getMessage("current_limit").replace("{limit}", limitDisplay);
                    source.sendMessage(plugin.color(msg));
                } else {
                    source.sendMessage(plugin.color(plugin.getMessage("no_permission")));
                }
                return;
            }

            try {
                int newLimit = Integer.parseInt(args[0]);
                if (newLimit < 0) {
                    source.sendMessage(plugin.color(plugin.getMessage("negative_limit")));
                    return;
                }
                plugin.setPlayerLimit(newLimit);
                String limitDisplay = newLimit > 0 ? String.valueOf(newLimit) : "Unlimited";
                String msg = plugin.getMessage("limit_set").replace("{limit}", limitDisplay);
                source.sendMessage(plugin.color(msg));
            } catch (NumberFormatException e) {
                source.sendMessage(plugin.color(plugin.getMessage("invalid_number")));
            }
        } else {
            source.sendMessage(plugin.color(plugin.getMessage("usage")));
        }
    }

    @Override
    public boolean hasPermission(Invocation invocation) {
        return invocation.source().hasPermission("playerlimit.admin") || invocation.source().hasPermission("playerlimit.reload");
    }

    @Override
    public CompletableFuture<List<String>> suggestAsync(Invocation invocation) {
        String[] args = invocation.arguments();
        List<String> suggestions = new ArrayList<>();
        if (args.length == 0 || args.length == 1) {
             if (invocation.source().hasPermission("playerlimit.reload")) {
                suggestions.add("reload");
             }
             if (invocation.source().hasPermission("playerlimit.admin")) {
                suggestions.add("info");
             }
        }
        return CompletableFuture.completedFuture(suggestions);
    }
}
