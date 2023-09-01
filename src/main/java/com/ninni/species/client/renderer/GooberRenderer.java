package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.GooberModel;
import com.ninni.species.entity.Goober;
import com.ninni.species.entity.pose.SpeciesPose;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import static com.ninni.species.Species.MOD_ID;

@Environment(EnvType.CLIENT)
public class GooberRenderer<T extends LivingEntity> extends MobRenderer<Goober, GooberModel<Goober>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/goober/goober.png");
    public static final ResourceLocation TEXTURE_TIRED = new ResourceLocation(MOD_ID, "textures/entity/goober/goober_tired.png");

    public GooberRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GooberModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.GOOBER)), 1F);
    }

    @Override
    public ResourceLocation getTextureLocation(Goober entity) {
        return (entity.getPose() == SpeciesPose.YAWNING.get() || entity.getPose() == SpeciesPose.YAWNING_LAYING_DOWN.get()) ? TEXTURE_TIRED : TEXTURE;
    }
}
