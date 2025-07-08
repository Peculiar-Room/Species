package com.ninni.species.client.renderer.entity;

import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.client.model.mob.update_1.WraptorModel;
import com.ninni.species.server.entity.mob.update_1.Wraptor;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class WraptorRenderer<T extends LivingEntity> extends MobRenderer<Wraptor, WraptorModel<Wraptor>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/wraptor/wraptor.png");
    public static final ResourceLocation TEXTURE_GOTH = new ResourceLocation(MOD_ID, "textures/entity/wraptor/wraptor_goth.png");
    public static final ResourceLocation TEXTURE_TRANS = new ResourceLocation(MOD_ID, "textures/entity/wraptor/wraptor_trans.png");

    public WraptorRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new WraptorModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.WRAPTOR)), 0.5F);
    }

    @Override public ResourceLocation getTextureLocation(Wraptor entity) {
        if (entity.getName().getString().equalsIgnoreCase("goth") || entity.getName().getString().equalsIgnoreCase("susie")) {
            return TEXTURE_GOTH;
        }else if (entity.getName().getString().equalsIgnoreCase("trans")) {
            return TEXTURE_TRANS;
        } else return TEXTURE;
    }

    @Override
    protected boolean isShaking(Wraptor entity) {
        if (entity.getFeatherStage() == 1) return true;
        if (!entity.level().dimensionType().piglinSafe()) return true;
        return super.isShaking(entity);
    }
}
