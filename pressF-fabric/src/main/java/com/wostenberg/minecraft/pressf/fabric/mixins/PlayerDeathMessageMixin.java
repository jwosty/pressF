package com.wostenberg.minecraft.pressf.fabric.mixins;

import com.wostenberg.minecraft.pressf.fabric.ServerPlayerDiedCallback;
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.damage.DamageTracker;
import net.minecraft.server.network.ServerPlayerEntity;
import org.slf4j.LoggerFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class PlayerDeathMessageMixin {
    private net.minecraft.entity.damage.DamageTracker DamageTracker;

    //@Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;getPrimeAdversary()Lnet/minecraft/entity/LivingEntity;"))
    @Inject(method = "onDeath", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/network/ServerPlayerEntity;dropShoulderEntities()V"))
    private void onDeath(CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        DamageTracker damageTracker = player.getDamageTracker();
        ServerPlayerDiedCallback.EVENT.invoker().onDeath(player, damageTracker);
//        String message = ((ServerPlayerEntity)(Object)this).getDamageTracker().getDeathMessage().getString();
//        LoggerFactory.getLogger("pressf").info("PLAYER DIED: {}", message);
    }
//    @Inject(method = "onDeath()V", at = @At("TAIL"))
//    private void onDeath(CallbackInfo ci) {
//        LoggerFactory.getLogger(("pressf")).info("PLAYER DIED!");
//    }
}
