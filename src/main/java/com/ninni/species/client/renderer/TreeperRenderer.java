package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.TreeperModel;
import com.ninni.species.client.renderer.entity.feature.TreeperFeatureRenderer;
import com.ninni.species.entity.Treeper;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import static com.ninni.species.Species.MOD_ID;

@Environment(EnvType.CLIENT)
public class TreeperRenderer<T extends LivingEntity> extends MobRenderer<Treeper, TreeperModel<Treeper>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/treeper/treeper.png");

    public TreeperRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TreeperModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.TREEPER)), 0F);
        this.addLayer(new TreeperFeatureRenderer<>(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Treeper entity) {
        return TEXTURE;
    }
}
