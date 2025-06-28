package com.ninni.species.client.renderer.entity.rendertypes;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesRenderTypes;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public abstract class OffsetRenderLayer<T extends Entity, M extends EntityModel<T>> extends RenderLayer<T, M> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(Species.MOD_ID, "textures/misc/empty.png");
    public OffsetRenderLayer(RenderLayerParent<T, M> p_116967_) {
        super(p_116967_);
    }

    public void render(PoseStack p_116970_, MultiBufferSource p_116971_, int p_116972_, T p_116973_, float p_116974_, float p_116975_, float p_116976_, float p_116977_, float p_116978_, float p_116979_) {
        if (!p_116973_.isInvisible()) {
            float f = (float) p_116973_.tickCount + p_116976_;
            EntityModel<T> entitymodel = this.model(p_116973_);
            entitymodel.prepareMobModel(p_116973_, p_116974_, p_116975_, p_116976_);
            this.getParentModel().copyPropertiesTo(entitymodel);
            VertexConsumer vertexconsumer = p_116971_.getBuffer(SpeciesRenderTypes.ScrollingTex(this.getTextureLocation(p_116973_), this.xOffset(f) % 1.0F, 0));
            entitymodel.setupAnim(p_116973_, p_116974_, p_116975_, p_116977_, p_116978_, p_116979_);
            entitymodel.renderToBuffer(p_116970_, vertexconsumer, p_116972_, OverlayTexture.RED_OVERLAY_V, 1.0F, 1.0F, 1.0F, 1.0F);
        }
    }

    protected ResourceLocation getTextureLocation(T Entity) {
        return TEXTURE;
    }

    protected abstract float xOffset(float p_116968_);

    protected abstract EntityModel<T> model(T entity);

}
