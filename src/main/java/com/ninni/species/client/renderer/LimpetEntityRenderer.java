package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.LimpetEntityModel;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.entity.LimpetEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;

import static com.ninni.species.Species.MOD_ID;

@Environment(value= EnvType.CLIENT)
public class LimpetEntityRenderer extends MobEntityRenderer<LimpetEntity, LimpetEntityModel<LimpetEntity>> {
    public static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/limpet/limpet.png");
    public static final Identifier TEXTURE_AMETHYST = new Identifier(MOD_ID, "textures/entity/limpet/limpet_amethyst.png");
    public static final Identifier TEXTURE_LAPIS = new Identifier(MOD_ID, "textures/entity/limpet/limpet_lapis.png");
    public static final Identifier TEXTURE_EMERALD = new Identifier(MOD_ID, "textures/entity/limpet/limpet_emerald.png");
    public static final Identifier TEXTURE_DIAMOND = new Identifier(MOD_ID, "textures/entity/limpet/limpet_diamond.png");

    public LimpetEntityRenderer(EntityRendererFactory.Context context) {
        super(context, new LimpetEntityModel<>(context.getPart(SpeciesEntityModelLayers.LIMPET)), 0.5f);
    }

    @Override
    protected boolean isShaking(LimpetEntity entity) {
        if (!entity.world.getEntitiesByClass(PlayerEntity.class, entity.getBoundingBox().expand(4D), entity::isValidEntityHoldingPickaxe).isEmpty()) return true;
        return super.isShaking(entity);
    }

    @Override
    public Identifier getTexture(LimpetEntity limpet) {
        return switch (limpet.getLimpetType()) {
            case AMETHYST -> TEXTURE_AMETHYST;
            case LAPIS -> TEXTURE_LAPIS;
            case EMERALD -> TEXTURE_EMERALD;
            case DIAMOND -> TEXTURE_DIAMOND;
            case SHELL, NO_SHELL -> TEXTURE;
        };
    }
}
