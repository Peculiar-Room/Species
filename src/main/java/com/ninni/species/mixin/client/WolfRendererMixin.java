package com.ninni.species.mixin.client;

import com.ninni.species.mixin_util.WolfAccess;
import net.minecraft.client.renderer.entity.WolfRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Wolf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.ninni.species.Species.MOD_ID;

@Mixin(WolfRenderer.class)
@OnlyIn(Dist.CLIENT)
public class WolfRendererMixin {
    @Unique
    private static final ResourceLocation BEWEREAGER_LOCATION = new ResourceLocation(MOD_ID, "textures/entity/bewereager/bewereager_wolf.png");

    @Inject(at = @At("HEAD"), method = "getTextureLocation(Lnet/minecraft/world/entity/animal/Wolf;)Lnet/minecraft/resources/ResourceLocation;", cancellable = true)
    private void S$getTextureLocation(Wolf wolf, CallbackInfoReturnable<ResourceLocation> cir) {
        if (wolf instanceof WolfAccess wolfAccess && wolfAccess.getIsCuredBewereager()) cir.setReturnValue(BEWEREAGER_LOCATION);
    }

}
