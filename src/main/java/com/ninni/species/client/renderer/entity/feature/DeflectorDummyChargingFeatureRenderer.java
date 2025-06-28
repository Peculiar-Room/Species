package com.ninni.species.client.renderer.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.client.model.mob.update_3.DeflectorDummyModel;
import com.ninni.species.server.entity.mob.update_3.DeflectorDummy;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class DeflectorDummyChargingFeatureRenderer<T extends DeflectorDummy, M extends DeflectorDummyModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation CHARGING = new ResourceLocation(MOD_ID, "textures/entity/quake/deflector_dummy/deflector_dummy_charging.png");
    private static final ResourceLocation CHARGED = new ResourceLocation(MOD_ID, "textures/entity/quake/deflector_dummy/deflector_dummy_charged.png");
    private final DeflectorDummyModel<T> model;

    public DeflectorDummyChargingFeatureRenderer(RenderLayerParent<T, M> renderLayerParent, DeflectorDummyModel<T> entityModel) {
        super(renderLayerParent);
        this.model = entityModel;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T quake, float f, float g, float h, float j, float k, float l) {
        if (quake.getStoredDamage() == 0) return;

        float opacityCharging = Math.min(quake.getStoredDamage() * 0.025F, 1);
        float opacityCharged = Math.max(0, Math.min((quake.getStoredDamage() - 40) * 0.025F, 1));

        this.getParentModel().copyPropertiesTo(this.model);
        this.model.prepareMobModel(quake, f, g, h);
        this.model.setupAnim(quake, f, g, j, k, l);

        VertexConsumer vertexConsumerCharging = multiBufferSource.getBuffer(RenderType.entityTranslucent(CHARGING));
        this.model.renderToBuffer(poseStack, vertexConsumerCharging, i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, opacityCharging);

        VertexConsumer vertexConsumerCharged = multiBufferSource.getBuffer(RenderType.entityTranslucentEmissive(CHARGED));
        this.model.renderToBuffer(poseStack, vertexConsumerCharged, i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, opacityCharged);
    }
}