# ModernPlayerLimit

A modern and simple player limit plugin for Velocity proxy servers.

## Features

- Set a global player limit across your Velocity proxy.
- Allow specific users or groups to bypass the limit.
- Manage the limit dynamically in-game or from the console using commands.
- Configuration is saved persistently.

## Commands

- `/playerlimit` - View the current player limit.
- `/playerlimit <amount>` - Set a new player limit. (Setting to `0` removes the limit).

## Permissions

- `playerlimit.admin` - Required to use the `/playerlimit` command to view or set the limit.
- `playerlimit.bypass` - Allows a player to join the proxy even if the limit has been reached.

## Installation

1. Download the latest `.jar` file from the [Releases](https://github.com/nedayazady/ModernPlayerLimit/releases) page.
2. Place the `.jar` file into the `plugins` folder of your Velocity proxy server.
3. Restart the proxy.
4. (Optional) Configure the default limit in `plugins/modernplayerlimit/config.properties`.

## Building from Source

This project uses Maven. To compile the plugin yourself:

```bash
git clone https://github.com/nedayazady/ModernPlayerLimit.git
cd ModernPlayerLimit
mvn clean package
```

The compiled `.jar` will be available in the `target` directory.

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
