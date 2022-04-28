package com.wostenberg.minecraft.pressf.fabric;

import com.wostenberg.minecraft.pressf.PressFCore;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.damage.DamageTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.damage.DamageSource;

import java.io.IOException;
import java.nio.file.Path;

public class FabricPressFMod implements ModInitializer {
    public static final String MODID = "pressf";

    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    PressFCore corePlugin;

    @Override
    public void onInitialize() {
        Path cfgDir = FabricLoader.getInstance().getConfigDir().resolve(MODID);

        corePlugin = new PressFCore(LOGGER, cfgDir.resolve("config.yml"));
        corePlugin.loadConfig();
        corePlugin.saveConfig();

        registerEvents();
    }

    public void registerEvents() {
        ServerPlayerDiedCallback.EVENT.register((ServerPlayerEntity player, DamageTracker damageTracker) -> {
            try {
                corePlugin.onDeath(damageTracker.getDeathMessage().getString());
            } catch (IOException e) {
                LOGGER.error("Error sending discord death message: {}", e);
            }
        });
    }
}