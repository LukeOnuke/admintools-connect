package com.lukeonuke.admintoolsconnect.command;

import com.lukeonuke.admintoolsconnect.services.ServerUUIDService;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class RegisterCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] arguments) {
        if(arguments.length != 1) {
            commandSender.sendMessage(Component.text("ServerUUID can not be blank!", NamedTextColor.RED));
            commandSender.sendMessage(
                Component.text("Usage: ", NamedTextColor.RED)
                    .append(Component.text("/atcregister <UUID>", NamedTextColor.RED, TextDecoration.BOLD))
            );
            return true;
        }

        ServerUUIDService uuidService = ServerUUIDService.getInstance();

        uuidService.setServerUUID(arguments[0]);
        commandSender.sendMessage(
            Component.text("ServerUUID set to ")
                .append(Component.text(arguments[0], NamedTextColor.GREEN))
        );
        return true;
    }
}
