package com.ninni.species.client.renderer.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.client.model.entity.TreeperModel;
import com.ninni.species.entity.Treeper;
import net.minecraft.client.model.Model;
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
public class TreeperFeatureRenderer<T extends Treeper, M extends TreeperModel<T>> extends RenderLayer<T, M> {
    private static final RenderType TREEPER_EYES = RenderType.entityTranslucentEmissive(new ResourceLocation(MOD_ID, "textures/entity/treeper/treeper_eyes.png"));
    private static final RenderType TREEPER_EYES_BURNED = RenderType.entityTranslucentEmissive(new ResourceLocation(MOD_ID, "textures/entity/treeper/treeper_eyes_burned.png"));

    public TreeperFeatureRenderer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource multiBufferSource, int i, T entity, float f, float g, float h, float j, float k, float l) {
        VertexConsumer vertexConsumer = multiBufferSource.getBuffer(entity.isBurned() ? TREEPER_EYES_BURNED : TREEPER_EYES);
        ((Model)this.getParentModel()).renderToBuffer(poseStack, vertexConsumer, 0xF00000, OverlayTexture.NO_OVERLAY, 1.0f, 1.0f, 1.0f, 1.0f);
    }
}