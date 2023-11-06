package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.SpringlingModel;
import com.ninni.species.entity.Springling;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import static com.ninni.species.Species.MOD_ID;

@Environment(EnvType.CLIENT)
public class SpringlingRenderer<T extends LivingEntity> extends MobRenderer<Springling, SpringlingModel<Springling>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/springling/springling.png");
    public static final ResourceLocation TEXTURE_1 = new ResourceLocation(MOD_ID, "textures/entity/springling/springling1.png");
    public static final ResourceLocation TEXTURE_2 = new ResourceLocation(MOD_ID, "textures/entity/springling/springling2.png");

    public SpringlingRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SpringlingModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.SPRINGLING)), 0F);
    }

    @Override
    public ResourceLocation getTextureLocation(Springling entity) {
        if (entity.getExtendedAmount() > entity.maxExtendedAmount/3f && entity.getExtendedAmount() < entity.maxExtendedAmount/1.5f) return TEXTURE_1;
        if (entity.getExtendedAmount() >= entity.maxExtendedAmount/1.5f) return TEXTURE_2;
        return TEXTURE;
    }
}
