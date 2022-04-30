package com.wostenberg.minecraft.pressf.bukkit;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import com.wostenberg.minecraft.pressf.PressFCore;

public class PressFPlugin extends JavaPlugin implements Listener {
    PressFCore corePlugin;

    @Override
    public void onEnable() {
        corePlugin = new PressFCore(getSLF4JLogger(), getDataFolder().toPath().resolve("config.yml"));

        corePlugin.loadConfig();
        corePlugin.saveConfig();

        this.getServer().getPluginManager().registerEvents(this, this);
        CommandReload ex = new CommandReload(getLogger(), () -> corePlugin.loadConfig());
        this.getCommand("reload").setExecutor(ex);
    }

    @Override
    public void saveConfig() {
        corePlugin.saveConfig();
    }

    public void loadConfig() {
        corePlugin.loadConfig();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) throws IOException {
        corePlugin.onDeath(event.getDeathMessage());
    }
}
