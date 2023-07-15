package com.ninni.species.client.renderer;

import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.client.model.entity.WraptorEntityModel;
import com.ninni.species.entity.WraptorEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import static com.ninni.species.Species.MOD_ID;

@Environment(EnvType.CLIENT)
public class WraptorEntityRenderer<T extends LivingEntity> extends MobRenderer<WraptorEntity, WraptorEntityModel<WraptorEntity>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/wraptor/wraptor.png");
    public static final ResourceLocation TEXTURE_GOTH = new ResourceLocation(MOD_ID, "textures/entity/wraptor/wraptor_goth.png");

    public WraptorEntityRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WraptorEntityModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.WRAPTOR)), 0.6F);
    }

    @Override public ResourceLocation getTextureLocation(WraptorEntity entity) {
        if ("Goth".equals(ChatFormatting.stripFormatting(entity.getName().getString()))) {
            return TEXTURE_GOTH;
        } else return TEXTURE;
    }

    @Override
    protected boolean isShaking(WraptorEntity entity) {
        if (entity.getFeatherStage() == 1) return true;
        if (!entity.level().dimensionType().piglinSafe()) return true;
        return super.isShaking(entity);
    }
}
