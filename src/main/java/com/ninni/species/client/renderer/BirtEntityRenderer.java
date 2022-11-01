package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.BirtEntityModel;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.client.renderer.entity.feature.BirtFeatureRenderer;
import com.ninni.species.entity.BirtEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

import static com.ninni.species.Species.MOD_ID;

@Environment(value= EnvType.CLIENT)
public class BirtEntityRenderer extends MobEntityRenderer<BirtEntity, BirtEntityModel<BirtEntity>> {
    private static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/birt/birt.png");
    private static final Identifier TEXTURE_COMMUNICATING = new Identifier(MOD_ID, "textures/entity/birt/birt_communicating.png");

    public BirtEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BirtEntityModel<>(context.getPart(SpeciesEntityModelLayers.BIRT)), 0.3f);
        this.addFeature(new BirtFeatureRenderer<>(this, TEXTURE_COMMUNICATING, (birt, tickDelta, animationProgress) -> Math.max(0, MathHelper.cos(animationProgress * 0.5f) * 0.75F), BirtEntityModel::getAllParts));
    }

    @Override
    protected void scale(BirtEntity entity, MatrixStack matrices, float amount) {
        if (entity.isBaby()) matrices.scale(0.5F, 0.5F, 0.5F);
        else super.scale(entity, matrices, amount);
    }

    @Override
    public Identifier getTexture(BirtEntity fish) {
        return TEXTURE;
    }
}
