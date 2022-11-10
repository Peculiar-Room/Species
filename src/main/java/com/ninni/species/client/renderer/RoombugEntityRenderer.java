package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.RoombugEntityModel;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.entity.RoombugEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import static com.ninni.species.Species.MOD_ID;

@Environment(EnvType.CLIENT)
public class RoombugEntityRenderer<T extends LivingEntity> extends MobEntityRenderer<RoombugEntity, RoombugEntityModel<RoombugEntity>> {
    public static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/roombug/roombug.png");
    public static final Identifier SITTING_TEXTURE = new Identifier(MOD_ID, "textures/entity/roombug/roombug_sleeping.png");

    public RoombugEntityRenderer(EntityRendererFactory.Context ctx) {
        super(ctx, new RoombugEntityModel<>(ctx.getPart(SpeciesEntityModelLayers.ROOMBUG)), 0.8F);
    }

    @Override public Identifier getTexture(RoombugEntity entity) {
        return  entity.isInSittingPose() ? SITTING_TEXTURE : TEXTURE;
    }
}
