# ModernPlayerLimit

A modern and highly customizable player limit plugin for Velocity proxy servers.

## Features

- **Global Limit:** Set a global player limit across your Velocity proxy.
- **Bypass:** Allow specific users or groups to bypass the limit using permissions.
- **Dynamic Updates:** Manage the limit dynamically in-game or from the console using commands.
- **Customizable Messages:** Fully customizable messages with legacy color support (`&e`) and HEX color support (`&#FFFFFF`).
- **Configuration:** Persistent saving/loading of the player limit and settings.

## Commands

- `/playerlimit <amount>` - Set a new player limit. (Setting to `0` removes the limit).
- `/playerlimit reload` - Reloads the configuration and messages files.

## Permissions

- `playerlimit.admin` - Required to use the `/playerlimit <amount>` command.
- `playerlimit.reload` - Required to use the `/playerlimit reload` command.
- `playerlimit.bypass` - Allows a player to join the proxy even if the limit has been reached.

## Configuration

### `messages.yml`
This file is generated automatically and allows you to translate or colorize all messages from the plugin.

```yaml
prefix: "&e&lPlayer Limit &8>> "
server_full: "&cThe server is currently full (Limit: {limit})."
limit_set: "&aPlayer limit has been set to: {limit}"
current_limit: "&eCurrent player limit is: {limit}"
negative_limit: "&cPlayer limit cannot be negative."
invalid_number: "&cInvalid number provided."
usage: "&cUsage: /playerlimit [amount | reload]"
no_permission: "&cYou do not have permission to execute this command!"
reload_success: "&aSuccessfully reloaded configuration and messages."
```

### `config.properties`
- `playerLimit`: The current limit (0 = disabled).
- `plugin-update`: A toggleable boolean (true/false) setting reserved for future use.

## Installation

1. Download the latest `.jar` file from the [Releases](https://github.com/nedayazady/ModernPlayerLimit/releases) page.
2. Place the `.jar` file into the `plugins` folder of your Velocity proxy server.
3. Restart the proxy.
4. Configure your messages in `plugins/modernplayerlimit/messages.yml`.

## Building from Source

This project uses Maven and requires **Java 17**. To compile the plugin yourself:

```bash
git clone https://github.com/nedayazady/ModernPlayerLimit.git
cd ModernPlayerLimit
mvn clean package
```

The compiled `.jar` will be available in the `target` directory.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
