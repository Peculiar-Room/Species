package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.RoombugEntityModel;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.entity.RoombugEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import static com.ninni.species.Species.MOD_ID;

@Environment(EnvType.CLIENT)
public class RoombugEntityRenderer<T extends LivingEntity> extends MobRenderer<RoombugEntity, RoombugEntityModel<RoombugEntity>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/roombug/roombug.png");
    public static final ResourceLocation SITTING_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/roombug/roombug_sleeping.png");

    public RoombugEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RoombugEntityModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.ROOMBUG)), 0.8F);
    }

    @Override
    public ResourceLocation getTextureLocation(RoombugEntity entity) {
        return  entity.isInSittingPose() ? SITTING_TEXTURE : TEXTURE;
    }
}
