package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.LimpetEntityModel;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.entity.LimpetEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import static com.ninni.species.Species.MOD_ID;

@Environment(value= EnvType.CLIENT)
public class LimpetEntityRenderer extends MobRenderer<LimpetEntity, LimpetEntityModel<LimpetEntity>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/limpet/limpet.png");
    public static final ResourceLocation TEXTURE_AMETHYST = new ResourceLocation(MOD_ID, "textures/entity/limpet/limpet_amethyst.png");
    public static final ResourceLocation TEXTURE_LAPIS = new ResourceLocation(MOD_ID, "textures/entity/limpet/limpet_lapis.png");
    public static final ResourceLocation TEXTURE_EMERALD = new ResourceLocation(MOD_ID, "textures/entity/limpet/limpet_emerald.png");
    public static final ResourceLocation TEXTURE_DIAMOND = new ResourceLocation(MOD_ID, "textures/entity/limpet/limpet_diamond.png");

    public LimpetEntityRenderer(EntityRendererProvider.Context context) {
        super(context, new LimpetEntityModel<>(context.bakeLayer(SpeciesEntityModelLayers.LIMPET)), 0.5f);
    }

    @Override
    protected boolean isShaking(LimpetEntity entity) {
        if (!entity.level.getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(4D), entity::isValidEntityHoldingPickaxe).isEmpty()) return true;
        return super.isShaking(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(LimpetEntity limpet) {
        return switch (limpet.getLimpetType()) {
            case AMETHYST -> TEXTURE_AMETHYST;
            case LAPIS -> TEXTURE_LAPIS;
            case EMERALD -> TEXTURE_EMERALD;
            case DIAMOND -> TEXTURE_DIAMOND;
            case SHELL, NO_SHELL -> TEXTURE;
        };
    }
}
