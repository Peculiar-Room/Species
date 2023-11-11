package com.ninni.species.client.renderer.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.client.model.entity.SpringlingModel;
import com.ninni.species.entity.Springling;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;

import static com.ninni.species.Species.MOD_ID;

@Environment(value= EnvType.CLIENT)
public class SpringlingNeckFeatureRenderer<T extends Springling, M extends SpringlingModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation NECK = new ResourceLocation(MOD_ID, "textures/entity/springling/springling_neck.png");
    private static final ResourceLocation NECK_EXTENDING = new ResourceLocation(MOD_ID, "textures/entity/springling/springling_neck_extending.png");
    private static final ResourceLocation NECK_EXTENDED = new ResourceLocation(MOD_ID, "textures/entity/springling/springling_neck_extended.png");

    public SpringlingNeckFeatureRenderer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T entity, float f, float g, float h, float j, float k, float l) {
        SpringlingNeckFeatureRenderer.coloredCutoutModelCopyLayerRender(this.getParentModel(), this.getParentModel(), this.resourceLocation(entity), poseStack, multiBufferSource, i, entity, f, g, j, k, l, h, 1, 1, 1);
    }

    public ResourceLocation resourceLocation(T entity) {
        if (entity.getExtendedAmount() > entity.maxExtendedAmount/3f && entity.getExtendedAmount() < entity.maxExtendedAmount/1.5f) return NECK_EXTENDING;
        if (entity.getExtendedAmount() >= entity.maxExtendedAmount/1.5f) return NECK_EXTENDED;
        return NECK;
    }
}
