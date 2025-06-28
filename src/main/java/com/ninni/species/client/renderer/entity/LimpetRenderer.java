package com.ninni.species.client.renderer.entity;

import com.ninni.species.client.model.mob.update_1.LimpetModel;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.client.renderer.entity.feature.LimpetBreakingLayer;
import com.ninni.species.server.entity.mob.update_1.Limpet;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class LimpetRenderer extends MobRenderer<Limpet, LimpetModel<Limpet>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/limpet/limpet.png");
    public static final ResourceLocation GARY_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/limpet/limpet_gary.png");

    public LimpetRenderer(EntityRendererProvider.Context context) {
        super(context, new LimpetModel<>(context.bakeLayer(SpeciesEntityModelLayers.LIMPET)), 0.5f);
        this.addLayer(new LimpetBreakingLayer(this, new LimpetModel<>(context.bakeLayer(SpeciesEntityModelLayers.LIMPET))));
    }

    @Override
    protected boolean isShaking(Limpet entity) {
        if (!entity.level().getEntitiesOfClass(Player.class, entity.getBoundingBox().inflate(4D), entity::isValidEntityHoldingPickaxe).isEmpty()) return true;
        return super.isShaking(entity);
    }

    @Override
    public ResourceLocation getTextureLocation(Limpet entity) {
        ResourceLocation ore = ResourceLocation.tryParse(entity.getOre());

        if (entity.getName().getString().equalsIgnoreCase("gary")) {
            if (entity.hasShell() && ore != null) {
                return new ResourceLocation(ore.getNamespace(), "textures/entity/limpet/ores/" + ore.getPath() + "_gary.png");
            }
            return GARY_TEXTURE;
        } else {
            if (entity.hasShell() && ore != null) {
                return new ResourceLocation(ore.getNamespace(), "textures/entity/limpet/ores/" + ore.getPath() + ".png");
            }
            return TEXTURE;
        }
    }
}
