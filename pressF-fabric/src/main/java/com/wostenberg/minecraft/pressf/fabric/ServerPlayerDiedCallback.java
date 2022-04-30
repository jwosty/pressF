package com.wostenberg.minecraft.pressf.fabric;

import net.fabricmc.fabric.api.event.Event;
import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.ActionResult;

import java.io.IOException;

public interface ServerPlayerDiedCallback {
    Event<ServerPlayerDiedCallback> EVENT = EventFactory.createArrayBacked(ServerPlayerDiedCallback.class,
            (listeners) -> (player, damageTracker) -> {
                for (ServerPlayerDiedCallback listener : listeners) {
                    listener.onDeath(player, damageTracker);
                }
            });

    void onDeath(ServerPlayerEntity player, DamageTracker damageTracker);
}
