# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [2.0.0-SNAPSHOT]

### Added
- **Folia Support**: Full compatibility with Folia's regionalized threading system
  - Automatic detection of Folia vs Paper/Spigot runtime
  - Uses `AsyncScheduler` and `GlobalRegionScheduler` when running on Folia
  - Graceful fallback to traditional Bukkit Scheduler on Paper/Spigot
  - Added `folia-supported: true` flag in plugin.yml
  
- **Command Validation System**: Comprehensive security layer for remote command execution
  - Permission-based command filtering with three priority levels:
    1. Player-specific rules (highest priority)
    2. Group-based rules (medium priority)
    3. Global rules (lowest priority)
  - Support for both whitelist and blacklist modes
  - Configurable via `config.yml`
  - Automatic logging of all command execution attempts for audit trails
  - Default blacklist includes critical commands: `stop`, `restart`, `reload`, `op`, `deop`, `whitelist`, `ban-ip`
  
- **Configuration System**: New `config.yml` for security settings
  - Flexible command validation rules
  - Customizable permission groups
  - Player-specific command restrictions
  
- **Enhanced Logging**: Replaced Log4j with Java Util Logging
  - New `AdminToolsLogHandler` class for intercepting server logs
  - Eliminates ClassLoader conflicts with Paper/Spigot
  - Better compatibility with modern Minecraft server implementations
  - Maintains full log forwarding to AdminTools platform

- **Permissions**: New permission nodes in plugin.yml
  - `atc.register` - Allows server registration (OP by default)
  - `atc.commands.*` - Wildcard for all command groups
  - `atc.commands.global` - Global command access

### Changed
- **BREAKING**: Minimum Java version increased from 11 to 21
- **BREAKING**: API version updated from 1.20 to 1.21
- **BREAKING**: Migrated from Spigot API to Paper API 1.21.4
- **BREAKING**: Removed Log4j Core dependency (now uses Java Util Logging)

- Updated `RegisterCommand` to use Adventure Component API
  - Replaced deprecated `ChatColor` with `Component` and `NamedTextColor`
  - Modern text formatting with proper component builders
  
- Improved logger implementation
  - Changed logger from instance field to `static final`
  - Better logging practices to avoid string concatenation

### Fixed
- Fixed incorrect use of `Color.RED` instead of `ChatColor.RED` in RegisterCommand
- Fixed inconsistent Maven configuration (java.version property vs compiler plugin)
- Removed unused `Setter` import in ServerUUIDService
- Removed unused `Timestamp` imports in model classes
- Fixed inefficient string concatenation in logging statements
- Fixed potential NullPointerException in CommandValidationService

### Security
- **Command Validation**: Remote commands now require proper permissions
- **Audit Logging**: All command execution attempts are logged
- **Default Blacklist**: Critical server commands are blocked by default
- Reduced attack surface by removing external Log4j dependency

### Dependencies
- Updated Paper API from `1.20.1-R0.1-SNAPSHOT` to `1.21.4-R0.1-SNAPSHOT`
- Updated Jackson Core from `2.14.2` to `2.18.2`
- Updated Jackson Databind from `2.14.2` to `2.18.2`
- Updated Jackson JSR310 from `2.14.2` to `2.18.2`
- Updated Lombok from `RELEASE` (floating) to `1.18.36` (fixed version)
- Updated Maven Compiler Plugin from `3.8.1` to `3.13.0`
- **Removed** Log4j Core `2.20.0` (replaced with Java Util Logging)
- Changed Lombok scope from `compile` to `provided`

### Removed
- Removed `AdminToolsFilter.java` (replaced by `AdminToolsLogHandler.java`)
- Removed Log4j Core dependency
- Removed Spigot API dependency (replaced by Paper API)

### Technical Details
- Java version: 21 (source and target)
- Minecraft API: 1.21
- Server compatibility: Paper 1.21.4+, Folia 1.21+
- Build system: Maven
- Minimum Paper build: 1.21.4-R0.1-SNAPSHOT

### Migration Guide
For users updating from 0.0.1-alpha:

1. **Java Requirement**: Ensure your server runs Java 21 or higher
2. **Server Software**: Update to Paper 1.21.4+ or Folia 1.21+
3. **Configuration**: A new `config.yml` will be generated on first startup
   - Review and customize the command validation settings
   - Configure permission groups as needed
4. **Permissions**: Grant new permissions to users/groups:
   - `atc.commands.global` for basic command access
   - Custom group permissions as defined in config.yml
5. **Testing**: Test remote command execution to ensure proper validation

---

## [1.0.0] - 2023

### Added
- Initial alpha release
- Basic server registration via `/atcregister` command
- Log forwarding to AdminTools platform
- Remote command execution from AdminTools
- Server UUID management
- HTTP client for AdminTools API communication
