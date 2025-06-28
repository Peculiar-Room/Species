package com.ninni.species.client.renderer.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.Species;
import com.ninni.species.client.model.mob.update_1.StackatickModel;
import com.ninni.species.server.entity.mob.update_1.Stackatick;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;

public class StackatickDyeLayer extends RenderLayer<Stackatick, StackatickModel<Stackatick>> {
    private final StackatickModel<Stackatick> model;

    public StackatickDyeLayer(RenderLayerParent<Stackatick, StackatickModel<Stackatick>> renderLayerParent, StackatickModel<Stackatick> entityModel) {
        super(renderLayerParent);
        this.model = entityModel;
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, Stackatick stackatick, float f, float g, float h, float j, float k, float l) {
        if (stackatick.isDyed() && !stackatick.isInvisible()) {
            this.getParentModel().copyPropertiesTo(this.model);
            this.model.prepareMobModel(stackatick, f, g, h);
            this.model.setupAnim(stackatick, f, g, j, k, l);
            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityTranslucent(this.getOverlayTextureLocation(stackatick)));
            this.model.renderToBuffer(poseStack, vertexConsumer, i, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
        }
    }


    public ResourceLocation getOverlayTextureLocation(Stackatick stackatick) {
        return new ResourceLocation(Species.MOD_ID, "textures/entity/stackatick/dyed/" + stackatick.getColor().getName() + ".png");
    }
}