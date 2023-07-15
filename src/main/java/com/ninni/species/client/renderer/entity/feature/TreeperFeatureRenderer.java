package com.ninni.species.client.renderer.entity.feature;

import com.ninni.species.client.model.entity.TreeperModel;
import com.ninni.species.entity.Treeper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.EyesLayer;
import net.minecraft.resources.ResourceLocation;

import static com.ninni.species.Species.MOD_ID;

@Environment(value=EnvType.CLIENT)
public class TreeperFeatureRenderer<T extends Treeper, M extends TreeperModel<T>> extends EyesLayer<T, M> {
    private static final RenderType TREEPER_EYES = RenderType.entityTranslucentEmissive(new ResourceLocation(MOD_ID, "textures/entity/treeper/treeper_eyes.png"));

    public TreeperFeatureRenderer(RenderLayerParent<T, M> renderLayerParent) {
        super(renderLayerParent);
    }

    @Override
    public RenderType renderType() {
        return TREEPER_EYES;
    }
}
