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
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.slf4j.Logger;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Plugin(
        id = "modernplayerlimit",
        name = "ModernPlayerLimit",
        version = "1.0.0",
        description = "A modern player limit plugin for Velocity.",
        authors = {"nedayazady"}
)
public class ModernPlayerLimit {

    private final ProxyServer server;
    private final Logger logger;
    private final Path dataDirectory;
    private int playerLimit;
    private boolean pluginUpdate;
    private Map<String, Object> messages;
    private final Pattern hexPattern = Pattern.compile("&#([a-fA-F0-9]{6})");
    private final LegacyComponentSerializer serializer = LegacyComponentSerializer.builder()
            .character('&')
            .hexCharacter('#')
            .hexColors()
            .useUnusualXRepeatedCharacterHexFormat()
            .build();

    @Inject
    public ModernPlayerLimit(ProxyServer server, Logger logger, @DataDirectory Path dataDirectory) {
        this.server = server;
        this.logger = logger;
        this.dataDirectory = dataDirectory;
    }

    @Subscribe
    public void onProxyInitialization(ProxyInitializeEvent event) {
        reload();
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
                String kickMessage = getMessage("server_full").replace("{limit}", String.valueOf(playerLimit));
                event.setResult(LoginEvent.ComponentResult.denied(color(kickMessage)));
            }
        }
    }

    public void reload() {
        loadConfig();
        loadMessages();
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
                playerLimit = Integer.parseInt(properties.getProperty("playerLimit", "0"));
                pluginUpdate = Boolean.parseBoolean(properties.getProperty("plugin-update", "true"));
            } catch (IOException | NumberFormatException e) {
                logger.error("Could not load config file, setting defaults.", e);
                playerLimit = 0;
                pluginUpdate = true;
                saveConfig();
            }
        } else {
            playerLimit = 0;
            pluginUpdate = true;
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
        properties.setProperty("plugin-update", String.valueOf(pluginUpdate));
        try (FileOutputStream out = new FileOutputStream(configFile)) {
            properties.store(out, "ModernPlayerLimit Configuration");
        } catch (IOException e) {
            logger.error("Could not save config file.", e);
        }
    }

    private void loadMessages() {
        File messagesFile = new File(dataDirectory.toFile(), "messages.yml");
        if (!messagesFile.exists()) {
            try (InputStream in = getClass().getResourceAsStream("/messages.yml")) {
                if (in != null) {
                    Files.copy(in, messagesFile.toPath());
                } else {
                    logger.error("messages.yml not found in resources!");
                }
            } catch (IOException e) {
                logger.error("Could not copy messages.yml to plugin folder.", e);
            }
        }

        try (FileInputStream in = new FileInputStream(messagesFile)) {
            Yaml yaml = new Yaml();
            messages = yaml.load(in);
        } catch (Exception e) {
            logger.error("Could not load messages.yml.", e);
        }
    }

    public String getMessage(String key) {
        if (messages == null || !messages.containsKey(key)) {
            return key;
        }
        String prefix = (String) messages.getOrDefault("prefix", "");
        return prefix + messages.get(key);
    }
    
    public String getRawMessage(String key) {
         if (messages == null || !messages.containsKey(key)) {
            return key;
        }
        return (String) messages.get(key);
    }

    public Component color(String message) {
        Matcher matcher = hexPattern.matcher(message);
        StringBuilder builder = new StringBuilder(message.length() + 4 * 8);
        while (matcher.find()) {
            String group = matcher.group(1);
            matcher.appendReplacement(builder, "&#" + group);
        }
        matcher.appendTail(builder);
        return serializer.deserialize(builder.toString());
    }

    public int getPlayerLimit() {
        return playerLimit;
    }

    public void setPlayerLimit(int playerLimit) {
        this.playerLimit = playerLimit;
        saveConfig();
    }
}
