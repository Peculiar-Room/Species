package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.TreeperSaplingModel;
import com.ninni.species.entity.TreeperSapling;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

import static com.ninni.species.Species.MOD_ID;

@Environment(EnvType.CLIENT)
public class TreeperSaplingRenderer<T extends LivingEntity> extends MobRenderer<TreeperSapling, TreeperSaplingModel<TreeperSapling>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/treeper/treeper_sapling.png");

    public TreeperSaplingRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TreeperSaplingModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.TREEPER_SAPLING)), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(TreeperSapling entity) {
        return  TEXTURE;
    }
}
