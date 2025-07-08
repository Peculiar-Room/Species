package com.ninni.species.client.renderer.entity;

import com.ninni.species.client.renderer.entity.feature.SpringlingNeckLayer;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.client.model.mob.update_2.SpringlingModel;
import com.ninni.species.server.entity.mob.update_2.Springling;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class SpringlingRenderer<T extends LivingEntity> extends MobRenderer<Springling, SpringlingModel<Springling>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/springling/springling.png");
    public static final ResourceLocation TEXTURE_NECK = new ResourceLocation(MOD_ID, "textures/entity/springling/springling_neck.png");
    public static final ResourceLocation TEXTURE_PISTON = new ResourceLocation(MOD_ID, "textures/entity/springling/springling_piston.png");
    public static final ResourceLocation TEXTURE_PISTON_NECK = new ResourceLocation(MOD_ID, "textures/entity/springling/springling_neck_piston.png");

    public SpringlingRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SpringlingModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.SPRINGLING)), 0F);
        this.addLayer(new SpringlingNeckLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Springling entity) {
        return entity.getName().getString().equalsIgnoreCase("piston") ? TEXTURE_PISTON : TEXTURE;
    }
}