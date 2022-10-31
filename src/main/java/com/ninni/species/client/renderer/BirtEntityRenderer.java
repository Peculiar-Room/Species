package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.BirtEntityModel;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.entity.BirtEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

import static com.ninni.species.Species.MOD_ID;

@Environment(value= EnvType.CLIENT)
public class BirtEntityRenderer extends MobEntityRenderer<BirtEntity, BirtEntityModel<BirtEntity>> {
    private static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/birt/birt.png");

    public BirtEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new BirtEntityModel<>(context.getPart(SpeciesEntityModelLayers.BIRT)), 0.3f);
    }

    @Override
    public Identifier getTexture(BirtEntity fish) {
        return TEXTURE;
    }
}
