package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.MammutilationModel;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.entity.Mammutilation;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class MammutilationRenderer<T extends LivingEntity> extends MobRenderer<Mammutilation, MammutilationModel<Mammutilation>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/mammutilation/mammutilation.png");

    public MammutilationRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new MammutilationModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.MAMMUTILATION)), 2.5F);
    }

    @Override
    public ResourceLocation getTextureLocation(Mammutilation entity) {
        return TEXTURE;
    }
}