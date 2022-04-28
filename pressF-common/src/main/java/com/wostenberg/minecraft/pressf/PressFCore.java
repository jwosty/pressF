package com.wostenberg.minecraft.pressf;

import org.simpleyaml.configuration.file.YamlFile;
import org.slf4j.Logger;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.logging.Level;

public class PressFCore {
    Logger logger;
    Path cfgFilePath;
    YamlFile config;

    static final String discordWebhookUrlPath = "discordWebhookUrl";
    static final String enabledPath = "enabled";

    public PressFCore(Logger logger, Path cfgFilePath) {
        this.logger = logger;
        this.cfgFilePath = cfgFilePath;
    }

    public void loadConfig() {
        config = new YamlFile(cfgFilePath.toAbsolutePath().toString());
        try {
            if (!config.exists()) {
                config.createNewFile(true);
            }
            config.loadWithComments();
        } catch (IOException e) {
            logger.error("Error loading config: {}", e);
        }

        config.options().copyDefaults(true);
        config.addDefault(discordWebhookUrlPath, "https://discordapp.com/api/webhooks/some/webhook");
        config.addDefault(enabledPath, true);
        config.options().copyDefaults(true);

        // SimpleYAML is *supposed* to write defaults back into the file if copyDefaults is set to true (which it is),
        // but it seems to have a bug or something where it only does if you also manually set something. So just work
        // around it by setting a value to itself to force it to write out the file.
        config.set(discordWebhookUrlPath, config.getString(discordWebhookUrlPath));

    }

    public void saveConfig() {
        try {
            config.save();
        } catch (IOException e) {
            logger.error("Error saving config: {}", e);
        }
    }

    public void onDeath(String deathMsg) throws IOException {
        boolean enabled = config.getBoolean(enabledPath);
        if (enabled) {
            String webhookUrl = config.getString(discordWebhookUrlPath);

            if (webhookUrl != null) {
                URL url = new URL(webhookUrl);
                logger.info("Sending msg: {}", deathMsg);
                HttpURLConnection http = (HttpURLConnection)url.openConnection();
                http.setRequestMethod("POST");
                http.setDoOutput(true);
                Charset charset = StandardCharsets.UTF_8;
                // TODO: use a real json writer here
                String str = ("{\"content\":\"" + deathMsg + "\"}");
                logger.info(str);
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
