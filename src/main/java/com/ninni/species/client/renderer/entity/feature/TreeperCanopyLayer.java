package com.ninni.species.client.renderer.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.client.model.mob.update_3.BewereagerModel;
import com.ninni.species.server.entity.mob.update_3.Bewereager;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class TreeperCanopyLayer extends RenderLayer<Bewereager, BewereagerModel<Bewereager>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/bewereager/bewereager_fur.png");
    public static final ResourceLocation TEXTURE_TAME = new ResourceLocation(MOD_ID, "textures/entity/bewereager/bewereager_tame_fur.png");

    public TreeperCanopyLayer(RenderLayerParent<Bewereager, BewereagerModel<Bewereager>> p_117707_) {
        super(p_117707_);
    }

    public void render(PoseStack poseStack, MultiBufferSource bufferSource, int i, Bewereager bewereager, float v, float v1, float v2, float v3, float v4, float v5) {
        if (bewereager.isWet) {
            float color;
            if (bewereager.shakeTime != 0) color =  -(float)bewereager.shakeTime/25 * 0.5F;
            else color = 0.5F;

            renderColoredCutoutModel(this.getParentModel(), getTextureLocation(bewereager), poseStack, bufferSource, i, bewereager, color, color, color);
        }
    }

    public ResourceLocation getTextureLocation(Bewereager entity) {
        return entity.getFromWolf() ? TEXTURE_TAME : TEXTURE;
    }
}
