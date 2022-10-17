package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.DeepfishEntityModel;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.client.renderer.entity.feature.DeepfishFeatureRenderer;
import com.ninni.species.entity.DeepfishEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import org.jetbrains.annotations.Nullable;

import static com.ninni.species.Species.MOD_ID;

@Environment(value= EnvType.CLIENT)
public class DeepfishEntityRenderer extends MobEntityRenderer<DeepfishEntity, DeepfishEntityModel<DeepfishEntity>> {
    private static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/deepfish/deepfish.png");
    private static final Identifier TEXTURE_GLOW = new Identifier(MOD_ID, "textures/entity/deepfish/deepfish_glow.png");

    public DeepfishEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new DeepfishEntityModel<>(context.getPart(SpeciesEntityModelLayers.DEEPFISH)), 0.3f);
        this.addFeature(new DeepfishFeatureRenderer<>(this, TEXTURE_GLOW, (deepfish, tickDelta, animationProgress) -> (float)Math.max(0, Math.cos(animationProgress * 0.15F) * 3F * 0.25F), DeepfishEntityModel::getAllParts));
    }

    @Nullable
    @Override
    protected RenderLayer getRenderLayer(DeepfishEntity entity, boolean showBody, boolean translucent, boolean showOutline) {
        return super.getRenderLayer(entity, showBody, true, showOutline);
    }

    @Override
    protected void setupTransforms(DeepfishEntity fish, MatrixStack matrixStack, float f, float g, float h) {
        super.setupTransforms(fish, matrixStack, f, g, h);
        matrixStack.scale(0.75F, 0.75F, 0.75F);

        if (!fish.isTouchingWater()) {
            matrixStack.translate(0.2, 0.2, 0);
            matrixStack.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(90.0f));
        }
    }

    @Override
    public Identifier getTexture(DeepfishEntity fish) {
        return TEXTURE;
    }
}
