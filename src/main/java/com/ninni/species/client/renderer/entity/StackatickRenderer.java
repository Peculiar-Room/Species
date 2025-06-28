package com.ninni.species.client.renderer.entity;

import com.ninni.species.client.model.mob.update_1.StackatickModel;
import com.ninni.species.client.renderer.entity.feature.StackatickDyeLayer;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.server.entity.mob.update_1.Stackatick;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class StackatickRenderer<T extends LivingEntity> extends MobRenderer<Stackatick, StackatickModel<Stackatick>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/stackatick/stackatick.png");
    public static final ResourceLocation SITTING_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/stackatick/stackatick_sleeping.png");

    public StackatickRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new StackatickModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.STACKATICK)), 0.5F);
        this.addLayer(new StackatickDyeLayer(this, new StackatickModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.STACKATICK))));
    }

    @Override
    public ResourceLocation getTextureLocation(Stackatick entity) {
        return  entity.isInSittingPose() ? SITTING_TEXTURE : TEXTURE;
    }
}
