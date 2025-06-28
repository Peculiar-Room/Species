package com.ninni.species.client.renderer.entity;

import com.ninni.species.client.model.mob.update_3.GhoulModel;
import com.ninni.species.server.entity.mob.update_3.Ghoul;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class GhoulRenderer<T extends LivingEntity> extends MobRenderer<Ghoul, GhoulModel<Ghoul>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/ghoul/ghoul.png");

    public GhoulRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new GhoulModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.GHOUL)), 1F);
    }

    @Override
    public ResourceLocation getTextureLocation(Ghoul entity) {
        return TEXTURE;
    }
}