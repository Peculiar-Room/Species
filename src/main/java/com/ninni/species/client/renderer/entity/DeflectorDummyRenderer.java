package com.ninni.species.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ninni.species.client.model.mob.update_3.DeflectorDummyModel;
import com.ninni.species.client.renderer.entity.feature.DeflectorDummyChargingFeatureRenderer;
import com.ninni.species.server.entity.mob.update_3.DeflectorDummy;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;

import static com.ninni.species.Species.MOD_ID;

public class DeflectorDummyRenderer extends LivingEntityRenderer<DeflectorDummy, DeflectorDummyModel<DeflectorDummy>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/quake/deflector_dummy/deflector_dummy.png");

    public DeflectorDummyRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new DeflectorDummyModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.DEFLECTOR_DUMMY)), 0F);
        this.addLayer(new DeflectorDummyChargingFeatureRenderer<>(this, new DeflectorDummyModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.DEFLECTOR_DUMMY))));
    }

    @Override
    protected boolean shouldShowName(DeflectorDummy dummy) {
        return false;
    }

    public void setupRotations(DeflectorDummy dummy, PoseStack poseStack, float f, float g, float h) {
        if (dummy.getStoredDamage() == 80) {
            g += (float)(Math.cos((double)dummy.tickCount * 3.25D) * Math.PI * (double)0.4F);
        }
        poseStack.mulPose(Axis.YP.rotationDegrees(180.0F - g));

        float l = (float)(dummy.level().getGameTime() - dummy.lastHit) + h;
        if (l < 5.0F) {
            poseStack.mulPose(Axis.YP.rotationDegrees(Mth.sin(l / 1.5F * (float)Math.PI) * 3.0F));
        }

    }

    @Override
    public ResourceLocation getTextureLocation(DeflectorDummy entity) {
        return TEXTURE;
    }
}