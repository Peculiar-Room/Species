package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.LimpetEntityModel;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.client.renderer.entity.feature.LimpetBreakingLayer;
import com.ninni.species.entity.Limpet;
import net.minecraft.ChatFormatting;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class LimpetRenderer extends MobRenderer<Limpet, LimpetEntityModel<Limpet>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/limpet/limpet.png");
    public static final ResourceLocation TEXTURE_COAL = new ResourceLocation(MOD_ID, "textures/entity/limpet/minerals/coal.png");
    public static final ResourceLocation TEXTURE_AMETHYST = new ResourceLocation(MOD_ID, "textures/entity/limpet/minerals/amethyst.png");
    public static final ResourceLocation TEXTURE_LAPIS = new ResourceLocation(MOD_ID, "textures/entity/limpet/minerals/lapis.png");
    public static final ResourceLocation TEXTURE_EMERALD = new ResourceLocation(MOD_ID, "textures/entity/limpet/minerals/emerald.png");
    public static final ResourceLocation TEXTURE_DIAMOND = new ResourceLocation(MOD_ID, "textures/entity/limpet/minerals/diamond.png");
    public static final ResourceLocation GARY_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/limpet/limpet_gary.png");
    public static final ResourceLocation GARY_TEXTURE_COAL = new ResourceLocation(MOD_ID, "textures/entity/limpet/minerals/coal_gary.png");
    public static final ResourceLocation GARY_TEXTURE_AMETHYST = new ResourceLocation(MOD_ID, "textures/entity/limpet/minerals/amethyst_gary.png");
    public static final ResourceLocation GARY_TEXTURE_LAPIS = new ResourceLocation(MOD_ID, "textures/entity/limpet/minerals/lapis_gary.png");
    public static final ResourceLocation GARY_TEXTURE_EMERALD = new ResourceLocation(MOD_ID, "textures/entity/limpet/minerals/emerald_gary.png");
    public static final ResourceLocation GARY_TEXTURE_DIAMOND = new ResourceLocation(MOD_ID, "textures/entity/limpet/minerals/diamond_gary.png");

    public LimpetRenderer(EntityRendererProvider.Context context) {
        super(context, new LimpetEntityModel<>(context.bakeLayer(SpeciesEntityModelLayers.LIMPET)), 0.5f);
        this.addLayer(new LimpetBreakingLayer(this, new LimpetEntityModel<>(context.bakeLayer(SpeciesEntityModelLayers.LIMPET))));
    }

    @Override
    protected boolean isShaking(Limpet entity) {
        if (!entity.level().getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(4D), entity::isValidEntityHoldingPickaxe).isEmpty()) return true;
        return super.isShaking(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(Limpet entity) {
        if (entity.getName().getString().equalsIgnoreCase("gary")) {
            return switch (entity.getLimpetType()) {
                case COAL -> GARY_TEXTURE_COAL;
                case AMETHYST -> GARY_TEXTURE_AMETHYST;
                case LAPIS -> GARY_TEXTURE_LAPIS;
                case EMERALD -> GARY_TEXTURE_EMERALD;
                case DIAMOND -> GARY_TEXTURE_DIAMOND;
                case SHELL, NO_SHELL -> GARY_TEXTURE;
            };
        } else
            return switch (entity.getLimpetType()) {
                case COAL -> TEXTURE_COAL;
                case AMETHYST -> TEXTURE_AMETHYST;
                case LAPIS -> TEXTURE_LAPIS;
                case EMERALD -> TEXTURE_EMERALD;
                case DIAMOND -> TEXTURE_DIAMOND;
                case SHELL, NO_SHELL -> TEXTURE;
            };
    }
}
