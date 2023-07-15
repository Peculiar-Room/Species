package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.RoombugModel;
import com.ninni.species.entity.Roombug;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import static com.ninni.species.Species.MOD_ID;

@Environment(EnvType.CLIENT)
public class RoombugRenderer<T extends LivingEntity> extends MobRenderer<Roombug, RoombugModel<Roombug>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/roombug/roombug.png");
    public static final ResourceLocation SITTING_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/roombug/roombug_sleeping.png");

    public RoombugRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new RoombugModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.ROOMBUG)), 0.8F);
    }

    @Override
    public ResourceLocation getTextureLocation(Roombug entity) {
        return  entity.isInSittingPose() ? SITTING_TEXTURE : TEXTURE;
    }
}
