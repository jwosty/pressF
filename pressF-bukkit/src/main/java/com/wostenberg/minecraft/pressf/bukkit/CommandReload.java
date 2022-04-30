package com.wostenberg.minecraft.pressf.bukkit;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandReload implements CommandExecutor {
    Runnable reloadConfig;
    Logger logger;

    public CommandReload(Logger logger, Runnable reloadConfig) {
        this.reloadConfig = reloadConfig;
        this.logger = logger;
    }

    private void log(CommandSender sender, String msg) {
        logger.log(Level.INFO, msg);
        sender.sendMessage(msg);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        reloadConfig.run();
        this.log(sender, "Successfully reloaded pressF config");
        return false;
    }}