package com.wostenberg.minecraft.pressf.fabric;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.wostenberg.minecraft.pressf.PressFCore;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.LiteralText;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.minecraft.server.command.CommandManager.argument;
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
                literal(MODID)
                    .then(literal("config")
                        .then(literal(PressFCore.discordWebhookUrlCfgKey)
                            .executes((CommandContext<ServerCommandSource> context) -> {
                                try {
                                    String value = corePlugin.getConfig().getString(PressFCore.discordWebhookUrlCfgKey);
                                    context.getSource().sendFeedback(new LiteralText(value), false);
                                    return 1;
                                } catch (Exception e) {
                                    LOGGER.error("Error executing command: {}", e);
                                    throw e;
                                }
                            })
                            .then(argument("value", StringArgumentType.string()).executes((CommandContext<ServerCommandSource> ctx) -> {
                                try {
                                    String value = getString(ctx, "value");
                                    corePlugin.getConfig().set(PressFCore.discordWebhookUrlCfgKey, value);
                                    return 1;
                                } catch (Exception e) {
                                    LOGGER.error("Error executing command: {}", e);
                                    throw e;
                                }
                            }))
                        )
                        .then(literal(PressFCore.enabledCfgKey)
                            .executes((CommandContext<ServerCommandSource> ctx) -> {
                                try {
                                    Boolean value = corePlugin.getConfig().getBoolean(PressFCore.enabledCfgKey);
                                    ctx.getSource().sendFeedback(new LiteralText(value.toString()), false);
                                    return 1;
                                } catch (Exception e) {
                                    LOGGER.error("Error executing command: {}", e);
                                    throw e;
                                }
                            })
                            .then(argument("value", BoolArgumentType.bool()).executes((CommandContext<ServerCommandSource> ctx) -> {
                                try {
                                    boolean value = getBool(ctx, "value");
                                    corePlugin.getConfig().set(PressFCore.enabledCfgKey, value);
                                    return 1;
                                } catch (Exception e) {
                                    LOGGER.error("Error executing command: {}", e);
                                    throw e;
                                }
                            }))
                        )
                    )
                    .then(literal("reload").executes((CommandContext<ServerCommandSource> ctx) -> {
                        corePlugin.loadConfig();
                        String str = "pressF: Successfully reloaded config.yml";
                        ctx.getSource().sendFeedback(new LiteralText(str), true);
                        return 1;
                    }))
            );
        });
    }
}