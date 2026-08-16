package me.nedayazady.modernplayerlimit;

import com.google.inject.Inject;
import com.velocitypowered.api.command.CommandManager;
import com.velocitypowered.api.event.Subscribe;
import com.velocitypowered.api.event.connection.LoginEvent;
import com.velocitypowered.api.event.proxy.ProxyInitializeEvent;
import com.velocitypowered.api.plugin.Plugin;
import com.velocitypowered.api.plugin.annotation.DataDirectory;
import com.velocitypowered.api.proxy.ProxyServer;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Properties;

@Plugin(
        id = "modernplayerlimit",
        name = "ModernPlayerLimit",
        version = "1.0",
        description = "A modern player limit plugin for Velocity.",
        authors = {"nedayazady"}
)
public class ModernPlayerLimit {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private int playerLimit;

    @Inject
    public ModernPlayerLimit(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        loadConfig();

        CommandManager commandManager = server.getCommandManager();
        commandManager.register(
                commandManager.metaBuilder("playerlimit").build(),
                new PlayerLimitCommand(this)
        );
        logger.info("ModernPlayerLimit has been initialized!");
    }

    @Subscribe
    public void onPlayerLogin(LoginEvent event) {
        if (playerLimit > 0) {
            if (server.getPlayerCount() >= playerLimit && !event.getPlayer().hasPermission("playerlimit.bypass")) {
                event.setResult(LoginEvent.ComponentResult.denied(Component.text("The server is currently full (Limit: " + playerLimit + ").", NamedTextColor.RED)));
            }
        }
    }

    private void loadConfig() {
        File configFile = new File(dataDirectory.toFile(), "config.properties");
        if (!dataDirectory.toFile().exists()) {
            dataDirectory.toFile().mkdirs();
        }

        Properties properties = new Properties();
        if (configFile.exists()) {
            try (FileInputStream in = new FileInputStream(configFile)) {
                properties.load(in);
                String limitStr = properties.getProperty("playerLimit", "0");
                try {
                    playerLimit = Integer.parseInt(limitStr);
                } catch (NumberFormatException e) {
                    logger.warn("Invalid playerLimit in config, setting to 0 (unlimited).");
                    playerLimit = 0;
                }
            } catch (IOException e) {
                logger.error("Could not load config file.", e);
            }
        } else {
            playerLimit = 0;
            saveConfig();
        }
    }

    public void saveConfig() {
        File configFile = new File(dataDirectory.toFile(), "config.properties");
        if (!dataDirectory.toFile().exists()) {
            dataDirectory.toFile().mkdirs();
        }
        Properties properties = new Properties();
        properties.setProperty("playerLimit", String.valueOf(playerLimit));
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            properties.store(out, "ModernPlayerLimit Configuration");
        } catch (IOException e) {
            logger.error("Could not save config file.", e);
        }
    }

    public int getPlayerLimit() {
        return playerLimit;
    }

    public void setPlayerLimit(int playerLimit) {
        this.playerLimit = playerLimit;
        saveConfig();
    }
}
