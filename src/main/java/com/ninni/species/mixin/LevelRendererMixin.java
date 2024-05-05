package com.ninni.species.mixin;

import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


@Mixin(LevelRenderer.class)
public class LevelRendererMixin {

    @Shadow private @Nullable ClientLevel level;

    @Shadow @Final private Minecraft minecraft;

    @Inject(method = "globalLevelEvent", at = @At("HEAD"))
    public void applyBirtdParticles(int i, BlockPos blockPos, int j, CallbackInfo ci) {
        Camera camera = this.minecraft.gameRenderer.getMainCamera();
        double h = camera.getPosition().x;
        double k = camera.getPosition().y;
        double l = camera.getPosition().z;
        if (i == 1246 && camera.isInitialized() && this.level != null) {
            this.level.playLocalSound(h, k, l, SpeciesSoundEvents.GUT_FEELING_APPLIED, SoundSource.HOSTILE, 2.0f, 1.0f, false);
        }
    }
}
