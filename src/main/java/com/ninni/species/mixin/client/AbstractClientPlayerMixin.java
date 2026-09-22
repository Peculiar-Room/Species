package com.ninni.species.mixin.client;

import com.mojang.authlib.GameProfile;
import com.ninni.species.SpeciesDevelopers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(PlayerSkin.class)
public abstract class AbstractClientPlayerMixin {

    @Shadow
    @Final
    private PlayerSkin.Model model;

    //??????? Maybe???

    @Inject(at = @At("HEAD"), method = "capeTexture", cancellable = true)
    private void getCapeLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        if (SpeciesDevelopers.developerUUIDS.containsKey(model.id())) {
            cir.setReturnValue(SpeciesDevelopers.developerUUIDS.get(model.id()).getCapeTexture());
        }
    }

    @Inject(at = @At("HEAD"), method = "elytraTexture", cancellable = true)
    private void getElytraLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        if (SpeciesDevelopers.developerUUIDS.containsKey(model.id())) {
            cir.setReturnValue(SpeciesDevelopers.developerUUIDS.get(model.id()).getCapeTexture());
        }
    }
}
