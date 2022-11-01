package com.ninni.species.client.renderer.entity.feature;

import com.ninni.species.client.model.entity.BirtEntityModel;
import com.ninni.species.entity.BirtEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.client.render.entity.feature.FeatureRenderer;
import net.minecraft.client.render.entity.feature.FeatureRendererContext;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;

import java.util.List;

@Environment(value=EnvType.CLIENT)
public class BirtFeatureRenderer<T extends BirtEntity, M extends BirtEntityModel<T>> extends FeatureRenderer<T, M> {
    private final Identifier texture;
    private final AnimationAngleAdjuster<T> animationAngleAdjuster;
    private final ModelPartVisibility<T, M> modelPartVisibility;

    public BirtFeatureRenderer(FeatureRendererContext<T, M> context, Identifier texture, AnimationAngleAdjuster<T> animationAngleAdjuster, ModelPartVisibility<T, M> modelPartVisibility) {
        super(context);
        this.texture = texture;
        this.animationAngleAdjuster = animationAngleAdjuster;
        this.modelPartVisibility = modelPartVisibility;
    }

    @Override
    public void render(MatrixStack matrixStack, VertexConsumerProvider vertexConsumerProvider, int i, T birt, float f, float g, float h, float j, float k, float l) {
        VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(RenderLayer.getEntityTranslucentEmissive(this.texture));

        if (birt.isInvisible() || birt.antennaTicks == 0) {
            return;
        }
        this.updateModelPartVisibility();
        this.getContextModel().render(matrixStack, vertexConsumer, i, LivingEntityRenderer.getOverlay(birt, 0.0f), 1.0f, 1.0f, 1.0f, this.animationAngleAdjuster.apply(birt, h, j));
        this.unhideAllModelParts();
    }

    private void updateModelPartVisibility() {
        List<ModelPart> list = this.modelPartVisibility.getPartsToDraw(this.getContextModel());
        this.getContextModel().getPart().traverse().forEach(part -> {
            part.hidden = true;
        });
        list.forEach(part -> {
            part.hidden = false;
        });
    }

    private void unhideAllModelParts() {
        this.getContextModel().getPart().traverse().forEach(part -> {
            part.hidden = false;
        });
    }

    @Environment(value= EnvType.CLIENT)
    public interface AnimationAngleAdjuster<T extends BirtEntity> {
        float apply(T var1, float var2, float var3);
    }

    @Environment(value=EnvType.CLIENT)
    public interface ModelPartVisibility<T extends BirtEntity, M extends EntityModel<T>> {
        List<ModelPart> getPartsToDraw(M var1);
    }
}
