package com.ninni.species.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.client.model.entity.TrooperModel;
import com.ninni.species.entity.Trooper;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class TrooperRenderer<T extends LivingEntity> extends MobRenderer<Trooper, TrooperModel<Trooper>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/treeper/trooper.png");
    public static final ResourceLocation TAMED_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/treeper/trooper_tame.png");

    public TrooperRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TrooperModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.TROOPER)), 0.3F);
    }

    @Override
    protected void scale(Trooper creeper, PoseStack poseStack, float f) {
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
    protected float getWhiteOverlayProgress(Trooper creeper, float f) {
        float g = creeper.getSwelling(f);
        if ((int)(g * 10.0f) % 2 == 0) {
            return 0.0f;
        }
        return Mth.clamp(g, 0.5f, 1.0f);
    }

    @Override
    public ResourceLocation getTextureLocation(Trooper entity) {
        return entity.isTame() ? TAMED_TEXTURE : TEXTURE;
    }
}