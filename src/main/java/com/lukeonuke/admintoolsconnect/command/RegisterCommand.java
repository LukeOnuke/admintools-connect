package com.lukeonuke.admintoolsconnect.command;

import com.lukeonuke.admintoolsconnect.services.ServerUUIDService;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.UUID;

public class RegisterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] arguments) {
        if(arguments.length != 1) {
            commandSender.sendMessage(Color.RED + "ServerUUID can not be blank!");
            commandSender.sendMessage( Color.RED + "Usage: " + ChatColor.BOLD + " /atcregister <UUID>");
            return true;
        }

        ServerUUIDService uuidService = ServerUUIDService.getInstance();

        uuidService.setServerUUID(arguments[0]);
        commandSender.sendMessage("ServerUUID set to " + ChatColor.GREEN + arguments[0]);
        return true;
    }
}
