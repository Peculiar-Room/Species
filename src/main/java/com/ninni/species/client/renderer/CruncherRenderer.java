package com.ninni.species.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.client.model.entity.CruncherModel;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.entity.Cruncher;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class CruncherRenderer<T extends LivingEntity> extends MobRenderer<Cruncher, CruncherModel<Cruncher>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/cruncher/cruncher.png");

    public CruncherRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new CruncherModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.CRUNCHER)), 2.5F);
    }

    @Override
    protected void scale(Cruncher livingEntity, PoseStack poseStack, float f) {
        poseStack.scale(1.2f,1.2f,1.2f);
        super.scale(livingEntity, poseStack, f);
    }

    @Override
    public ResourceLocation getTextureLocation(Cruncher entity) {
        return TEXTURE;
    }
}