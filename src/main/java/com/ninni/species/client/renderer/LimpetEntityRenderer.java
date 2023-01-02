package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.LimpetEntityModel;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.entity.LimpetEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.util.Identifier;

import static com.ninni.species.Species.MOD_ID;

@Environment(value= EnvType.CLIENT)
public class LimpetEntityRenderer extends MobEntityRenderer<LimpetEntity, LimpetEntityModel<LimpetEntity>> {
    public static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/limpet/limpet.png");

    public LimpetEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new LimpetEntityModel<>(context.getPart(SpeciesEntityModelLayers.LIMPET)), 0.5f);
    }

    @Override
    public Identifier getTexture(LimpetEntity limpet) {
        return TEXTURE;
    }
}
