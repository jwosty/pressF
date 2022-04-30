package com.wostenberg.minecraft.pressf.fabric;

import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.wostenberg.minecraft.pressf.PressFCore;
import me.lucko.fabric.api.permissions.v0.Permissions;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Path;

import me.lucko.fabric.api.permissions.v0.Permissions;

import static com.mojang.brigadier.arguments.BoolArgumentType.getBool;
import static com.mojang.brigadier.arguments.StringArgumentType.getString;
import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class FabricPressFMod implements ModInitializer {
    public static final String MOD_ID = "pressf";
    public static final String MOD_NAME = "pressF";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    PressFCore corePlugin;

    @Override
    public void onInitialize() {
        Path cfgDir = FabricLoader.getInstance().getConfigDir().resolve(MOD_ID);

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
                literal(MOD_ID)
                    .then(literal("config")
                        .requires(Permissions.require("pressf.config", PressFCore.COMMAND_PERMISSIONS_LEVEL))
                        .then(literal(PressFCore.discordWebhookUrlCfgKey)
                            .executes((CommandContext<ServerCommandSource> context) -> {
                                try {
                                    String value = corePlugin.getConfig().getString(PressFCore.discordWebhookUrlCfgKey);
                                    Text resultMsg =
                                        (new LiteralText(value))
                                            .setStyle(
                                                Style.EMPTY
                                                    .withColor(Formatting.GREEN)
                                                    .withClickEvent(new ClickEvent(ClickEvent.Action.COPY_TO_CLIPBOARD, value))
                                                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, new LiteralText("Click to copy to clipboard"))));
                                    context.getSource().sendFeedback(resultMsg, false);
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
                                    ctx.getSource().sendFeedback(new LiteralText(MOD_NAME + ": Changed config value '" + PressFCore.discordWebhookUrlCfgKey + "'"), true);
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
                                    ctx.getSource().sendFeedback(new LiteralText(MOD_NAME + ": Changed config value '" + PressFCore.enabledCfgKey + "'"), true);
                                    return 1;
                                } catch (Exception e) {
                                    LOGGER.error("Error executing command: {}", e);
                                    throw e;
                                }
                            }))
                        )
                    )
                    .then(literal("reload")
                        .requires(Permissions.require("pressf.reload", PressFCore.COMMAND_PERMISSIONS_LEVEL))
                        .executes((CommandContext<ServerCommandSource> ctx) -> {
                            corePlugin.loadConfig();
                            String str = MOD_NAME + ": Reloaded config";
                            ctx.getSource().sendFeedback(new LiteralText(str), true);
                            return 1;
                        }
                    ))
            );
        });
    }
}