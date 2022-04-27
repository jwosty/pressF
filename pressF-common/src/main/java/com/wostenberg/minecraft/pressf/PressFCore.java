package com.wostenberg.minecraft.pressf;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;
import java.util.logging.Level;

public class PressFCore {
    public static void onDeath(Logger logger, String deathMsg, String webhookUrl) throws IOException {
        if (webhookUrl != null) {
            URL url = new URL(webhookUrl);
            logger.log(Level.INFO, "Sending msg: " + deathMsg);
            HttpURLConnection http = (HttpURLConnection)url.openConnection();
            http.setRequestMethod("POST");
            http.setDoOutput(true);
            Charset charset = StandardCharsets.UTF_8;
            // TODO: use a real json writer here
            String str = ("{\"content\":\"" + deathMsg + "\"}");
            logger.log(Level.INFO, str);
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
