package com.wostenberg.minecraft.pressf.fabric;

import net.fabricmc.api.ModInitializer;
import net.minecraft.entity.damage.DamageTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.damage.DamageSource;

public class FabricPressFMod implements ModInitializer {
    public static final Logger LOGGER = LoggerFactory.getLogger("pressf");

    @Override
    public void onInitialize() {
        ServerPlayerDiedCallback.EVENT.register((ServerPlayerEntity player, DamageTracker damageTracker) -> {
            LOGGER.info("PLAYER DIED: {}", damageTracker.getDeathMessage().getString());
        });
    }
}