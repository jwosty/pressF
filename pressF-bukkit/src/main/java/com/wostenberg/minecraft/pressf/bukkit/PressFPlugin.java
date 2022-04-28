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
    // FileConfiguration config;
    
//    static final String discordWebhookUrlPath = "discordWebhookUrl";
//    static final String enabledPath = "enabled";

    PressFCore corePlugin;

    @Override
    public void onEnable() {
        corePlugin = new PressFCore(getSLF4JLogger(), getDataFolder().toPath().resolve("config.yml"));

        this.loadConfig();
        this.saveConfig();

        this.getServer().getPluginManager().registerEvents(this, this);
        CommandReload ex = new CommandReload(getLogger(), () -> loadConfig());
        this.getCommand("reload").setExecutor(ex);
    }

    public void loadConfig() {
//        super.reloadConfig();
//        config = this.getConfig();
//        config.addDefault(discordWebhookUrlPath, "https://discordapp.com/api/webhooks/some/webhook");
//        config.addDefault(enabledPath, true);
//        config.options().copyDefaults(true);
        corePlugin.loadConfig();
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) throws IOException {
//        boolean enabled = config.getBoolean(enabledPath);
//        if (enabled) {
//            String deathMsg = event.getDeathMessage();
//            String strUrl = config.getString(discordWebhookUrlPath);
//            corePlugin.onDeath(deathMsg);
//        }
        corePlugin.onDeath(event.getDeathMessage());
    }
}
