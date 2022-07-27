package com.ninni.species.client.renderer;

import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.client.model.entity.WraptorEntityModel;
import com.ninni.species.entity.WraptorEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.EntityRendererFactory;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.Identifier;

import static com.ninni.species.Species.*;

@Environment(EnvType.CLIENT)
public class WraptorEntityRenderer<T extends LivingEntity> extends MobEntityRenderer<WraptorEntity, WraptorEntityModel<WraptorEntity>> {
    public static final Identifier TEXTURE = new Identifier(MOD_ID, "textures/entity/wraptor.png");

    public WraptorEntityRenderer(EntityRendererFactory.Context ctx) { super(ctx, new WraptorEntityModel<>(ctx.getPart(SpeciesEntityModelLayers.WRAPTOR)), 0.6F); }

    @Override public Identifier getTexture(WraptorEntity entity) { return TEXTURE; }

    @Override
    protected boolean isShaking(WraptorEntity entity) {
        return super.isShaking(entity) || entity.getFeatherStage() == 1;
    }
}
