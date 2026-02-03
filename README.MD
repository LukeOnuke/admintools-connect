![AdminTools.app logo](https://github.com/LukeOnuke/admintools-connect/blob/master/banner.png?raw=true)

***AdminTools Connect*** is a plugin that allows connections with the ATC protocol.

The ATC protocol is a peer-to-peer remote console over HTTPS. That means it does not require any open ports to function.

It is developed for [admintools.app](https://admintools.app).

For more technical info consult the [admintools documentation on ATC](https://docs.admintools.app).

## 📋 Requirements

- **Minecraft**: 1.21+
- **Server Software**: Paper/Spigot/Purpur/Bukkit 1.21.4+ or Folia 1.21+
- **Java**: 21 or higher

## ✨ Features

- 🔐 **Secure Remote Console**: Peer-to-peer HTTPS connection without port forwarding
- 🌐 **Web-Based Management**: Control your server from anywhere via [admintools.app](https://admintools.app)
- 📊 **Real-Time Log Streaming**: All server logs are forwarded to the AdminTools platform
- 🎮 **Remote Command Execution**: Execute commands from the web interface
- 🔒 **Advanced Security System**: Permission-based command validation
- 🧵 **Folia Compatible**: Full support for Folia's regionalized threading
- ⚡ **Modern APIs**: Uses Adventure Components and Paper API

## 🚀 Quick Start

### Installation

1. Download the latest **Admin Tools Connect** plugin from the [release](https://github.com/X00LA/admintools-connect/releases/latest) section.
2. Place the `.jar` file in your server's `plugins` folder.
3. Start or restart your server.
4. A `config.yml` will be generated in `plugins/AdminToolsConnect/`.

### Registration

1. Open [admintools.app](https://admintools.app) and navigate to "Add Server".
2. Select `ATC` as the connection type.
3. Enter a name for your server.
4. Click `Generate Server ID and Copy`.
5. Paste the command (e.g., `/atcregister <uuid>`) into your Minecraft server console.
6. Your server is now connected! Click on the server card to access the remote console.

## ⚙️ Configuration

The plugin creates a `config.yml` file in `plugins/AdminToolsConnect/` with comprehensive security settings.

### Basic Structure

```yaml
security:
  command-validation: true      # Enable/disable command validation
  whitelist-mode: false          # false = blacklist mode, true = whitelist mode
  
  global:                        # Settings for all users with global permission
    permission: atc.commands.global
    allowed-commands: [...]      # Commands allowed in whitelist mode
    blocked-commands: [...]      # Commands blocked in blacklist mode
  
  groups:                        # Permission group-based rules
    admin:
      permission: atc.commands.admin
      allowed-commands: [...]
      blocked-commands: [...]
  
  players:                       # Player-specific rules (highest priority)
    playername:
      permission: atc.commands.playername
      allowed-commands: [...]
      blocked-commands: [...]
```

### Security Modes

#### Blacklist Mode (Default - Recommended)
```yaml
whitelist-mode: false
```
- Allows **all commands except** those in `blocked-commands`
- More flexible for day-to-day administration
- Default blocked commands: `stop`, `restart`, `reload`, `op`, `deop`, `whitelist`, `ban-ip`

#### Whitelist Mode (Maximum Security)
```yaml
whitelist-mode: true
```
- Allows **only** commands in `allowed-commands`
- More restrictive, requires explicit permission for each command
- Better for untrusted environments

### Priority System

The plugin evaluates permissions in this order (highest to lowest priority):

1. **Player-Specific Rules** → Defined in `players` section
2. **Group Rules** → Defined in `groups` section
3. **Global Rules** → Defined in `global` section

If a player has multiple matching permissions, the highest priority rule applies.

### Example Configurations

#### Admin Group Example
```yaml
groups:
  admin:
    permission: atc.commands.admin
    allowed-commands:
      - kick
      - ban
      - pardon
      - gamemode
      - teleport
      - give
      - weather
      - time
    blocked-commands:
      - stop
      - restart
      - op
      - deop
```

#### Moderator Group Example
```yaml
groups:
  moderator:
    permission: atc.commands.moderator
    allowed-commands:
      - kick
      - mute
      - warn
      - teleport
    blocked-commands:
      - ban
      - pardon
      - gamemode
      - give
      - op
      - deop
      - stop
      - restart
```

#### Player-Specific Override
```yaml
players:
  trusted_admin:
    permission: atc.commands.trusted_admin
    allowed-commands:
      - kick
      - ban
      - gamemode
      - tp
      - give
    blocked-commands:
      - stop      # Still can't stop the server
      - restart
```

### Disabling Command Validation

If you want to allow all commands without restrictions (not recommended):

```yaml
security:
  command-validation: false
```

**⚠️ Warning**: Disabling validation allows any command to be executed remotely, including server shutdown and OP granting!

## 🔑 Permissions

### Plugin Permissions

| Permission | Description | Default |
|------------|-------------|---------|
| `atc.register` | Allows registering the server to AdminTools | OP |
| `atc.commands.*` | Wildcard for all command groups | False |
| `atc.commands.global` | Allows global command execution | False |

### Custom Group Permissions

You can define custom permissions in `config.yml`:

```yaml
groups:
  yourgroup:
    permission: atc.commands.yourgroup  # Define this permission
    allowed-commands: [...]
    blocked-commands: [...]
```

Then grant `atc.commands.yourgroup` using your permission plugin (LuckPerms, etc.).

## 📝 Audit Logging

All remote command execution attempts are logged to the server console:

```
[ATC Command] EXECUTED: kick PlayerName (Reason: Allowed by group: admin)
[ATC Command] BLOCKED: stop (Reason: Blocked by global settings)
```

This provides a complete audit trail for security and compliance.

## 🧵 Folia Support

The plugin automatically detects if it's running on Folia or Paper/Spigot/Purpur:

- **On Folia**: Uses `AsyncScheduler` and `GlobalRegionScheduler`
- **On Paper/Spigot/Purpur**: Uses traditional `BukkitScheduler`

No configuration needed - it just works! 😏

## 🔧 Commands

| Command | Description | Permission |
|---------|-------------|------------|
| `/atcregister <uuid>` | Register server to AdminTools Connect | `atc.register` |

## 🐛 Troubleshooting

### Server won't connect
- Ensure you're using Java 21+
- Check that you're running Paper/Spigot/Purpur/Bukkit 1.21.4+ or Folia 1.21+
- Verify the server UUID was correctly set with `/atcregister`

### Commands are blocked
- Check your `config.yml` security settings
- Verify the user/group has the correct permission (`atc.commands.*`)
- Review the server logs for detailed blocking reasons
- Check if `command-validation` is enabled

### Logs not showing up
- Ensure the server is properly registered
- Check network connectivity to `connect.admintools.app`
- Review server logs for connection errors

## 📚 Support

- **Documentation**: [docs.admintools.app](https://docs.admintools.app/)
- **Issues**: [GitHub Issues](https://github.com/LukeOnuke/admintools-connect/issues)
- **Website**: [admintools.app](https://admintools.app)

## 📄 License

See [LICENSE](LICENSE) file for details.

## 🔄 Changelog

See [CHANGELOG.md](CHANGELOG.md) for version history and updates.