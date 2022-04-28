package com.wostenberg.minecraft.pressf.fabric;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.wostenberg.minecraft.pressf.PressFCore;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.LiteralText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.entity.damage.DamageSource;

import java.io.IOException;
import java.nio.file.Path;

import static net.minecraft.server.command.CommandManager.literal;

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
        registerCommands();
    }

    private void registerEvents() {
        ServerPlayerDiedCallback.EVENT.register((ServerPlayerEntity player, DamageTracker damageTracker) -> {
            try {
                corePlugin.onDeath(damageTracker.getDeathMessage().getString());
            } catch (IOException e) {
                LOGGER.error("Error sending discord death message: {}", e);
            }
        });
    }

    private void registerCommands() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(
                CommandManager.literal(MODID)
                    .then(
                        CommandManager.literal("reload").executes((CommandContext<ServerCommandSource> context) -> {
                            corePlugin.loadConfig();
                            String str = "pressF: Successfully reloaded config.yml";
                            context.getSource().sendFeedback(new LiteralText(str), true);
                            return 1;
                        })
                    )
            );
//            dispatcher.register(CommandManager.literal(MODID + ":reload").executes((CommandContext<ServerCommandSource> context) -> {
//                corePlugin.loadConfig();
//                String str = "pressF: Successfully reloaded config.yml";
//                context.getSource().sendFeedback(new LiteralText(str), true);
//                return 1;
//            }));
        });
    }
}