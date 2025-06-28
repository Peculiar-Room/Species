package com.ninni.species.client.renderer.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.client.model.mob.update_1.BirtModel;
import com.ninni.species.server.entity.mob.update_1.Birt;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class BirtFeatureRenderer<T extends Birt, M extends BirtModel<T>> extends RenderLayer<T, M> {
    private final ResourceLocation texture;
    private final AnimationAngleAdjuster<T> animationAngleAdjuster;
    private final ModelPartVisibility<T, M> modelPartVisibility;

    public BirtFeatureRenderer(RenderLayerParent<T, M> context, ResourceLocation texture, AnimationAngleAdjuster<T> animationAngleAdjuster, ModelPartVisibility<T, M> modelPartVisibility) {
        super(context);
        this.texture = texture;
        this.animationAngleAdjuster = animationAngleAdjuster;
        this.modelPartVisibility = modelPartVisibility;
    }

    private void updateModelPartVisibility() {
        List<ModelPart> list = this.modelPartVisibility.getPartsToDraw(this.getParentModel());
        this.getParentModel().root().getAllParts().forEach(part -> part.skipDraw = true);
        list.forEach(part -> part.skipDraw = false);
    }

    private void unhideAllModelParts() {
        this.getParentModel().root().getAllParts().forEach(part -> part.skipDraw = false);
    }

    @Override
    public void render(PoseStack matrixStack, MultiBufferSource multiBufferSource, int i, T birt, float f, float g, float h, float j, float k, float l) {
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.entityTranslucentEmissive(this.texture));
        if (birt.isInvisible() || birt.antennaTicks == 0) return;
        this.updateModelPartVisibility();
        this.getParentModel().renderToBuffer(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlayCoords(birt, 0.0f), 1.0f, 1.0f, 1.0f, this.animationAngleAdjuster.apply(birt, h, j));
        this.unhideAllModelParts();
    }

    @OnlyIn(Dist.CLIENT)
    public interface AnimationAngleAdjuster<T extends Birt> {
        float apply(T var1, float var2, float var3);
    }

    @OnlyIn(Dist.CLIENT)
    public interface ModelPartVisibility<T extends Birt, M extends EntityModel<T>> {
        List<ModelPart> getPartsToDraw(M var1);
    }
}
