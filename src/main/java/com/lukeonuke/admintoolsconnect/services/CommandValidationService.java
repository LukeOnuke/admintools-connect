package com.lukeonuke.admintoolsconnect.services;

import com.lukeonuke.admintoolsconnect.AdminToolsConnect;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.logging.Level;

/**
 * Service for validating commands based on permission groups and player-specific rules
 * Supports whitelist and blacklist modes with hierarchical permission checking
 */
public class CommandValidationService {
    private static CommandValidationService instance;
    private final AdminToolsConnect plugin;
    
    private boolean commandValidationEnabled;
    private boolean whitelistMode;
    
    // Global settings
    private String globalPermission;
    private Set<String> globalAllowedCommands;
    private Set<String> globalBlockedCommands;
    
    // Group-based settings
    private Map<String, GroupConfig> groups;
    
    // Player-specific settings
    private Map<String, PlayerConfig> players;

    private CommandValidationService(AdminToolsConnect plugin) {
        this.plugin = plugin;
        this.groups = new HashMap<>();
        this.players = new HashMap<>();
        this.globalAllowedCommands = new HashSet<>();
        this.globalBlockedCommands = new HashSet<>();
        loadConfig();
    }

    public static CommandValidationService getInstance(AdminToolsConnect plugin) {
        if (instance == null) {
            instance = new CommandValidationService(plugin);
        }
        return instance;
    }

    public static CommandValidationService getInstance() {
        if (instance == null) {
            throw new IllegalStateException("CommandValidationService not initialized! Call getInstance(plugin) first.");
        }
        return instance;
    }

    /**
     * Load configuration from config.yml
     */
    public void loadConfig() {
        plugin.reloadConfig();
        FileConfiguration config = plugin.getConfig();
        
        ConfigurationSection securitySection = config.getConfigurationSection("security");
        if (securitySection == null) {
            plugin.getLogger().warning("No security section found in config.yml - using defaults");
            commandValidationEnabled = true;
            whitelistMode = false;
            loadDefaults();
            return;
        }
        
        commandValidationEnabled = securitySection.getBoolean("command-validation", true);
        whitelistMode = securitySection.getBoolean("whitelist-mode", false);
        
        // Load global settings
        ConfigurationSection globalSection = securitySection.getConfigurationSection("global");
        if (globalSection != null) {
            globalPermission = globalSection.getString("permission", "atc.commands.global");
            globalAllowedCommands = new HashSet<>(globalSection.getStringList("allowed-commands"));
            globalBlockedCommands = new HashSet<>(globalSection.getStringList("blocked-commands"));
        } else {
            loadDefaults();
        }
        
        // Load groups
        ConfigurationSection groupsSection = securitySection.getConfigurationSection("groups");
        if (groupsSection != null) {
            for (String groupName : groupsSection.getKeys(false)) {
                ConfigurationSection groupSection = groupsSection.getConfigurationSection(groupName);
                if (groupSection != null) {
                    GroupConfig groupConfig = new GroupConfig(
                        groupName,
                        groupSection.getString("permission", "atc.commands." + groupName),
                        new HashSet<>(groupSection.getStringList("allowed-commands")),
                        new HashSet<>(groupSection.getStringList("blocked-commands"))
                    );
                    groups.put(groupName.toLowerCase(), groupConfig);
                }
            }
        }
        
        // Load player-specific settings
        ConfigurationSection playersSection = securitySection.getConfigurationSection("players");
        if (playersSection != null) {
            for (String playerName : playersSection.getKeys(false)) {
                ConfigurationSection playerSection = playersSection.getConfigurationSection(playerName);
                if (playerSection != null) {
                    PlayerConfig playerConfig = new PlayerConfig(
                        playerName,
                        playerSection.getString("permission", "atc.commands." + playerName.toLowerCase()),
                        new HashSet<>(playerSection.getStringList("allowed-commands")),
                        new HashSet<>(playerSection.getStringList("blocked-commands"))
                    );
                    players.put(playerName.toLowerCase(), playerConfig);
                }
            }
        }
        
        plugin.getLogger().info("Command validation loaded: " + 
            (commandValidationEnabled ? "ENABLED" : "DISABLED") + 
            " (Mode: " + (whitelistMode ? "WHITELIST" : "BLACKLIST") + ")");
    }

    /**
     * Load default security settings
     */
    private void loadDefaults() {
        globalPermission = "atc.commands.global";
        globalBlockedCommands = new HashSet<>(Arrays.asList(
            "stop", "restart", "reload", "op", "deop", "whitelist", "ban-ip"
        ));
        globalAllowedCommands = new HashSet<>();
    }

    /**
     * Validate if a command can be executed by the given sender
     * 
     * @param sender The command sender (console or player)
     * @param command The command to validate (without leading slash)
     * @return ValidationResult containing whether the command is allowed and the reason
     */
    public ValidationResult validateCommand(CommandSender sender, String command) {
        if (!commandValidationEnabled) {
            return ValidationResult.allowed("Command validation disabled");
        }
        
        // Extract base command (first word)
        String baseCommand = command.split(" ")[0].toLowerCase();
        
        // Check player-specific settings first (highest priority)
        if (sender instanceof Player player) {
            String playerName = player.getName().toLowerCase();
            if (players.containsKey(playerName)) {
                PlayerConfig playerConfig = players.get(playerName);
                if (player.hasPermission(playerConfig.permission)) {
                    return checkCommandAgainstConfig(baseCommand, playerConfig.allowedCommands, 
                        playerConfig.blockedCommands, "player-specific rule");
                }
            }
            
            // Check group permissions (medium priority)
            for (GroupConfig groupConfig : groups.values()) {
                if (player.hasPermission(groupConfig.permission)) {
                    return checkCommandAgainstConfig(baseCommand, groupConfig.allowedCommands, 
                        groupConfig.blockedCommands, "group: " + groupConfig.name);
                }
            }
        }
        
        // Check global settings (lowest priority)
        if (sender.hasPermission(globalPermission)) {
            return checkCommandAgainstConfig(baseCommand, globalAllowedCommands, 
                globalBlockedCommands, "global settings");
        }
        
        // No permission found
        return ValidationResult.denied("No permission to execute remote commands");
    }

    /**
     * Check command against allowed/blocked lists
     */
    private ValidationResult checkCommandAgainstConfig(String command, Set<String> allowed, 
                                                       Set<String> blocked, String source) {
        if (whitelistMode) {
            // Whitelist mode: only allowed commands can be executed
            if (allowed.contains(command)) {
                return ValidationResult.allowed("Allowed by " + source);
            }
            return ValidationResult.denied("Not in whitelist (" + source + ")");
        } else {
            // Blacklist mode: all commands except blocked ones
            if (blocked.contains(command)) {
                return ValidationResult.denied("Blocked by " + source);
            }
            return ValidationResult.allowed("Not blocked (" + source + ")");
        }
    }

    /**
     * Log a command execution attempt
     */
    public void logCommandExecution(String command, boolean allowed, String reason) {
        String status = allowed ? "EXECUTED" : "BLOCKED";
        plugin.getLogger().log(Level.INFO, 
            String.format("[ATC Command] %s: %s (Reason: %s)", status, command, reason));
    }

    // Configuration classes
    private static class GroupConfig {
        final String name;
        final String permission;
        final Set<String> allowedCommands;
        final Set<String> blockedCommands;

        GroupConfig(String name, String permission, Set<String> allowedCommands, Set<String> blockedCommands) {
            this.name = name;
            this.permission = permission;
            this.allowedCommands = allowedCommands;
            this.blockedCommands = blockedCommands;
        }
    }

    private static class PlayerConfig {
        final String playerName;
        final String permission;
        final Set<String> allowedCommands;
        final Set<String> blockedCommands;

        PlayerConfig(String playerName, String permission, Set<String> allowedCommands, Set<String> blockedCommands) {
            this.playerName = playerName; // Store for future use
            this.permission = permission;
            this.allowedCommands = allowedCommands;
            this.blockedCommands = blockedCommands;
        }

        String getPlayerName() {
            return playerName;
        }
    }

    /**
     * Result of command validation
     */
    public static class ValidationResult {
        private final boolean allowed;
        private final String reason;

        private ValidationResult(boolean allowed, String reason) {
            this.allowed = allowed;
            this.reason = reason;
        }

        public static ValidationResult allowed(String reason) {
            return new ValidationResult(true, reason);
        }

        public static ValidationResult denied(String reason) {
            return new ValidationResult(false, reason);
        }

        public boolean isAllowed() {
            return allowed;
        }

        public String getReason() {
            return reason;
        }
    }
}
