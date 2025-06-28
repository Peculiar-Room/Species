package com.ninni.species.mixin.client;

import com.mojang.authlib.GameProfile;
import com.ninni.species.SpeciesDevelopers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@OnlyIn(Dist.CLIENT)
@Mixin(AbstractClientPlayer.class)
public abstract class AbstractClientPlayerMixin extends Player {

    public AbstractClientPlayerMixin(Level p_250508_, BlockPos p_250289_, float p_251702_, GameProfile p_252153_) {
        super(p_250508_, p_250289_, p_251702_, p_252153_);
    }

    @Inject(at = @At("HEAD"), method = "getCloakTextureLocation()Lnet/minecraft/resources/ResourceLocation;", cancellable = true)
    private void getCapeLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        if (SpeciesDevelopers.developerUUIDS.containsKey(this.getUUID())) {
            cir.setReturnValue(SpeciesDevelopers.developerUUIDS.get(this.getUUID()).getCapeTexture());
        }
    }

    @Inject(at = @At("HEAD"), method = "getElytraTextureLocation()Lnet/minecraft/resources/ResourceLocation;", cancellable = true)
    private void getElytraLocation(CallbackInfoReturnable<ResourceLocation> cir) {
        if (SpeciesDevelopers.developerUUIDS.containsKey(this.getUUID())) {
            cir.setReturnValue(SpeciesDevelopers.developerUUIDS.get(this.getUUID()).getCapeTexture());
        }
    }
}
