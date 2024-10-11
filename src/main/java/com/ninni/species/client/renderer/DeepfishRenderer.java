package com.ninni.species.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.ninni.species.client.model.entity.DeepfishModel;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.client.renderer.entity.feature.DeepfishFeatureRenderer;
import com.ninni.species.entity.Deepfish;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class DeepfishRenderer extends MobRenderer<Deepfish, DeepfishModel<Deepfish>> {
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/deepfish/deepfish.png");
    private static final ResourceLocation TEXTURE_GLOW = new ResourceLocation(MOD_ID, "textures/entity/deepfish/deepfish_glow.png");

    public DeepfishRenderer(EntityRendererProvider.Context context) {
        super(context, new DeepfishModel<>(context.bakeLayer(SpeciesEntityModelLayers.DEEPFISH)), 0.3f);
        this.addLayer(new DeepfishFeatureRenderer<>(this, TEXTURE_GLOW, (deepfish, tickDelta, animationProgress) -> (float)Math.max(0, Math.cos(animationProgress * 0.15F) * 3F * 0.25F), DeepfishModel::getAllParts));
    }

    @Override
    protected void setupRotations(Deepfish fish, PoseStack matrixStack, float f, float g, float h) {
        super.setupRotations(fish, matrixStack, f, g, h);
        matrixStack.scale(0.75F, 0.75F, 0.75F);

        if (!fish.isInWater()) {
            matrixStack.translate(0.2, 0.2, 0);
            matrixStack.mulPose(Axis.ZP.rotationDegrees(90.0f));
        }
    }

    @Override
    public ResourceLocation getTextureLocation(Deepfish fish) {
        return TEXTURE;
    }
}
