package com.wostenberg.minecraft.pressf;

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

public class PressFPlugin extends JavaPlugin implements Listener {
    FileConfiguration config;
    
    static final String discordWebhookUrlPath = "discordWebhookUrl";
    static final String enabledPath = "enabled";

    @Override
    public void onEnable() {
        this.loadConfig();
        this.saveConfig();
        this.getServer().getPluginManager().registerEvents(this, this);
        CommandReload ex = new CommandReload(getLogger(), () -> loadConfig());
        this.getCommand("reload").setExecutor(ex);
    }

    public void loadConfig() {
        super.reloadConfig();
        config = this.getConfig();
        config.addDefault(discordWebhookUrlPath, "https://discordapp.com/api/webhooks/some/webhook");
        config.addDefault(enabledPath, true);
        config.options().copyDefaults(true);
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent event) throws IOException {
        boolean enabled = config.getBoolean(enabledPath);
        if (enabled) {
            String deathMsg = event.getDeathMessage();
            String strUrl = config.getString(discordWebhookUrlPath);
            if (strUrl != null) {
                URL url = new URL(strUrl);
                getLogger().log(Level.INFO, "Sending msg: " + deathMsg);
                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                Charset charset = StandardCharsets.UTF_8;
                // TODO: use a real json writer here
                String str = ("{\"content\":\"" + deathMsg + "\"}");
                getLogger().log(Level.INFO, str);
                byte[] out = str.getBytes(charset);
                http.setFixedLengthStreamingMode(out.length);
                http.setRequestProperty("Content-Type", "application/json; charset=" + charset.name());
                http.connect();
                // TODO: check status and log if not 200
                try(OutputStream os = http.getOutputStream()) {
                    os.write(out);
                }
            }
        }
    }
}
