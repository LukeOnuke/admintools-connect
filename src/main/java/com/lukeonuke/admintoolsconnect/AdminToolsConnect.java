package com.lukeonuke.admintoolsconnect;

import com.lukeonuke.admintoolsconnect.command.RegisterCommand;
import com.lukeonuke.admintoolsconnect.log.AdminToolsFilter;
import com.lukeonuke.admintoolsconnect.models.QueuedCommandModel;
import com.lukeonuke.admintoolsconnect.services.DataService;
import org.apache.logging.log4j.LogManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Objects;
import java.util.logging.Logger;


public final class AdminToolsConnect extends JavaPlugin {
    public static Logger atLogger;

    @Override
    public void onEnable() {
        atLogger = this.getLogger();

        ((org.apache.logging.log4j.core.Logger) LogManager.getRootLogger()).addFilter(new AdminToolsFilter());
        atLogger.info("Applied log interceptor.");

        this.getCommand("atcregister").setExecutor(new RegisterCommand());

        DataService dataService = DataService.getInstance();

        Bukkit.getScheduler().runTaskTimerAsynchronously(this, () -> {
            ArrayList<QueuedCommandModel> queuedCommands =
                    dataService.getCommandQueue();

            if (Objects.isNull(queuedCommands)) return;

            queuedCommands.forEach(queuedCommandModel -> {
                Bukkit.getScheduler().runTask(this, () -> {
                    Bukkit.dispatchCommand(
                            Bukkit.getConsoleSender(),
                            queuedCommandModel.getValue()
                    );
                });
            });
        }, 0, 100);
    }

    @Override
    public void onDisable() {
    }
}
