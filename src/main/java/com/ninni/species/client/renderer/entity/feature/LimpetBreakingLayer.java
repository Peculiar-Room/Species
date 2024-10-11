package com.ninni.species.client.renderer.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.client.model.entity.LimpetModel;
import com.ninni.species.entity.Limpet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

import static com.ninni.species.Species.MOD_ID;

public class LimpetBreakingLayer extends RenderLayer<Limpet, LimpetModel<Limpet>> {
    private final LimpetModel<Limpet> model;
    public static final ResourceLocation TEXTURE_0 = new ResourceLocation(MOD_ID, "textures/entity/limpet/breaking_overlay/0.png");
    public static final ResourceLocation TEXTURE_1 = new ResourceLocation(MOD_ID, "textures/entity/limpet/breaking_overlay/1.png");
    public static final ResourceLocation TEXTURE_2 = new ResourceLocation(MOD_ID, "textures/entity/limpet/breaking_overlay/2.png");

    public LimpetBreakingLayer(RenderLayerParent<Limpet, LimpetModel<Limpet>> renderLayerParent, LimpetModel<Limpet> entityModel) {
        super(renderLayerParent);
        this.model = entityModel;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, Limpet limpet, float f, float g, float h, float j, float k, float l) {
        if (limpet.getCrackedStage() == 0) return;
        else {
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(limpet, f, g, h);
            this.model.setupAnim(limpet, f, g, j, k, l);
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(this.getOverlayTextureLocation(limpet)));
            this.model.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 0.75f);
        }
    }


    public ResourceLocation getOverlayTextureLocation(Limpet limpet) {
        return switch (limpet.getCrackedStage()) {
            case 2 -> TEXTURE_1;
            case 3 -> TEXTURE_2;
            default -> TEXTURE_0;
        };
    }
}