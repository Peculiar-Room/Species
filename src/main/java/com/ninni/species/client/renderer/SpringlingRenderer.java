package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.client.model.entity.SpringlingModel;
import com.ninni.species.client.renderer.entity.feature.SpringlingNeckFeatureRenderer;
import com.ninni.species.entity.Springling;
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
    public static final ResourceLocation TEXTURE_PISTON = new ResourceLocation(MOD_ID, "textures/entity/springling/springling_piston.png");

    public SpringlingRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SpringlingModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.SPRINGLING)), 0F);
        this.addLayer(new SpringlingNeckFeatureRenderer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Springling entity) {
        return entity.getName().getString().equalsIgnoreCase("piston") ? TEXTURE_PISTON : TEXTURE;
    }
}