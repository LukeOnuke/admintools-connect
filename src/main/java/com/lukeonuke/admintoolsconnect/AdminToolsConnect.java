package com.lukeonuke.admintoolsconnect;

import com.lukeonuke.admintoolsconnect.command.RegisterCommand;
import com.lukeonuke.admintoolsconnect.log.AdminToolsLogHandler;
import com.lukeonuke.admintoolsconnect.models.QueuedCommandModel;
import com.lukeonuke.admintoolsconnect.services.CommandValidationService;
import com.lukeonuke.admintoolsconnect.services.DataService;
import io.papermc.paper.threadedregions.scheduler.ScheduledTask;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;


public final class AdminToolsConnect extends JavaPlugin {
    public static final Logger atLogger = Logger.getLogger(AdminToolsConnect.class.getName());
    private ScheduledTask foliaTask;
    private AdminToolsLogHandler logHandler;
    private boolean isFolia;

    @Override
    public void onEnable() {
        // Save default config if not exists
        saveDefaultConfig();
        
        // Detect if running on Folia
        isFolia = detectFolia();
        atLogger.info("Server type detected: " + (isFolia ? "Folia" : "Paper/Spigot"));

        // Setup log handler (replaces Log4j filter)
        setupLogHandler();

        // Register commands
        this.getCommand("atcregister").setExecutor(new RegisterCommand());

        // Initialize command validation service
        CommandValidationService.getInstance(this);

        // Start command queue processor with Folia/Paper compatibility
        startCommandQueueProcessor();
        
        atLogger.info("AdminTools Connect enabled successfully!");
    }

    @Override
    public void onDisable() {
        // Stop scheduled task
        if (isFolia && foliaTask != null) {
            foliaTask.cancel();
        }
        
        // Remove log handler
        if (logHandler != null) {
            Logger rootLogger = Logger.getLogger("");
            rootLogger.removeHandler(logHandler);
        }
        
        atLogger.info("AdminTools Connect disabled!");
    }

    /**
     * Setup Java Util Logging handler to intercept all server logs
     */
    private void setupLogHandler() {
        DataService dataService = DataService.getInstance();
        logHandler = new AdminToolsLogHandler(dataService);
        
        // Add handler to root logger to catch all logs
        Logger rootLogger = Logger.getLogger("");
        rootLogger.addHandler(logHandler);
        
        atLogger.info("Log interceptor initialized (Java Util Logging)");
    }

    /**
     * Start command queue processor with Folia/Paper compatibility
     */
    private void startCommandQueueProcessor() {
        DataService dataService = DataService.getInstance();
        CommandValidationService validationService = CommandValidationService.getInstance();

        if (isFolia) {
            // Folia: Use AsyncScheduler for async tasks
            foliaTask = getServer().getAsyncScheduler().runAtFixedRate(this, (task) -> {
                processCommandQueue(dataService, validationService);
            }, 0, 5, TimeUnit.SECONDS);
            atLogger.info("Command queue processor started (Folia AsyncScheduler)");
        } else {
            // Paper/Spigot: Use traditional scheduler
            Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
                processCommandQueue(dataService, validationService);
            }, 0, 100); // 100 ticks = 5 seconds
            atLogger.info("Command queue processor started (Bukkit Scheduler)");
        }
    }

    /**
     * Process command queue - fetches and executes commands from AdminTools platform
     */
    private void processCommandQueue(DataService dataService, CommandValidationService validationService) {
        ArrayList<QueuedCommandModel> queuedCommands = dataService.getCommandQueue();

        if (Objects.isNull(queuedCommands)) return;

        queuedCommands.forEach(queuedCommandModel -> {
            String command = queuedCommandModel.getValue();
            
            // Validate command
            CommandValidationService.ValidationResult result = 
                validationService.validateCommand(Bukkit.getConsoleSender(), command);
            
            // Log the attempt
            validationService.logCommandExecution(command, result.isAllowed(), result.getReason());
            
            if (!result.isAllowed()) {
                atLogger.warning("Blocked command from AdminTools: " + command + " - " + result.getReason());
                return;
            }
            
            // Execute command on main thread
            if (isFolia) {
                // Folia: Use GlobalRegionScheduler for command execution
                getServer().getGlobalRegionScheduler().run(this, (scheduledTask) -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                });
            } else {
                // Paper/Spigot: Use traditional scheduler
                Bukkit.getScheduler().runTask(this, () -> {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
                });
            }
        });
    }

    /**
     * Detect if running on Folia
     * @return true if Folia is detected, false otherwise
     */
    private boolean detectFolia() {
        try {
            Class.forName("io.papermc.paper.threadedregions.RegionizedServer");
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }
}
