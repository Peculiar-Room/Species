package com.ninni.species.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.client.model.entity.BirtModel;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.client.renderer.entity.feature.BirtFeatureRenderer;
import com.ninni.species.entity.Birt;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class BirtRenderer extends MobRenderer<Birt, BirtModel<Birt>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/birt/birt.png");
    public static final ResourceLocation TEXTURE_COMMUNICATING = new ResourceLocation(MOD_ID, "textures/entity/birt/birt_communicating.png");

    public BirtRenderer(EntityRendererProvider.Context context) {
        super(context, new BirtModel<>(context.bakeLayer(SpeciesEntityModelLayers.BIRT)), 0.3f);
        this.addLayer(new BirtFeatureRenderer<>(this, TEXTURE_COMMUNICATING, (birt, tickDelta, animationProgress) -> Math.max(0, Mth.cos(animationProgress * 0.5f) * 0.75F), BirtModel::getAllParts));
    }

    @Override
    protected void scale(Birt entity, PoseStack matrices, float amount) {
        if (entity.isBaby()) matrices.scale(0.5F, 0.5F, 0.5F);
        else super.scale(entity, matrices, amount);
    }

    @Override
    public ResourceLocation getTextureLocation(Birt fish) {
        return TEXTURE;
    }
}
