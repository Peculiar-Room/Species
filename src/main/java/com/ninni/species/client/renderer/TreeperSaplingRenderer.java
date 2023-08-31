package com.ninni.species.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.client.model.entity.TreeperSaplingModel;
import com.ninni.species.entity.TreeperSapling;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

import static com.ninni.species.Species.MOD_ID;

@Environment(EnvType.CLIENT)
public class TreeperSaplingRenderer<T extends LivingEntity> extends MobRenderer<TreeperSapling, TreeperSaplingModel<TreeperSapling>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/treeper/treeper_sapling.png");

    public TreeperSaplingRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TreeperSaplingModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.TREEPER_SAPLING)), 0.3F);
    }

    @Override
    protected void scale(TreeperSapling creeper, PoseStack poseStack, float f) {
        float g = creeper.getSwelling(f);
        float h = 1.0f + Mth.sin(g * 100.0f) * g * 0.01f;
        g = Mth.clamp(g, 0.0f, 1.0f);
        g *= g;
        g *= g;
        float i = (1.0f + g * 0.4f) * h;
        float j = (1.0f + g * 0.1f) / h;
        poseStack.scale(i, j, i);
    }

    @Override
    protected float getWhiteOverlayProgress(TreeperSapling creeper, float f) {
        float g = creeper.getSwelling(f);
        if ((int)(g * 10.0f) % 2 == 0) {
            return 0.0f;
        }
        return Mth.clamp(g, 0.5f, 1.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(TreeperSapling entity) {
        return  TEXTURE;
    }
}
