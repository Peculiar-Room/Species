package com.ninni.species.client.renderer.item;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class WickedFireballRenderer<T extends Entity> extends EntityRenderer<T> {
    private final ModelPart all;
    private final float scale;
    private final boolean fullBright;
    public static final ResourceLocation LOCATION = new ResourceLocation(Species.MOD_ID,"textures/entity/wicked/wicked_fireball.png");

    public WickedFireballRenderer(EntityRendererProvider.Context p_174416_, float p_174417_, boolean p_174418_) {
        super(p_174416_);
        this.scale = p_174417_;
        this.fullBright = p_174418_;
        ModelPart modelPart = p_174416_.bakeLayer(SpeciesEntityModelLayers.WICKED_FIREBALL);
        this.all = modelPart.getChild("all");
    }


    public WickedFireballRenderer(EntityRendererProvider.Context p_174414_) {
        this(p_174414_, 1.0F, false);
    }



    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create().texOffs(0, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(16, 0).addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.25F))
                .texOffs(0, 8).addBox(-2.0F, -2.0F, 2.0F, 4.0F, 4.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }


    public void render(T entity, float v, float v1, PoseStack poseStack, MultiBufferSource multiBufferSource, int i) {
        if (entity.tickCount >= 2 || !(this.entityRenderDispatcher.camera.getEntity().distanceToSqr(entity) < 12.25D)) {
            poseStack.pushPose();

            poseStack.mulPose(Axis.YP.rotationDegrees(-entity.getYRot()));
            poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(v1, entity.xRotO, entity.getXRot())));
            poseStack.translate(0, -1.35, 0);
            poseStack.scale(this.scale, this.scale, this.scale);

            VertexConsumer vertexConsumer = multiBufferSource.getBuffer(RenderType.dragonExplosionAlpha(this.getTextureLocation(entity)));

            this.all.render(poseStack, vertexConsumer, i, 1);

            poseStack.popPose();
            super.render(entity, v, v1, poseStack, multiBufferSource, i);
        }
    }

    protected int getBlockLightLevel(T p_116092_, BlockPos p_116093_) {
        return this.fullBright ? 15 : super.getBlockLightLevel(p_116092_, p_116093_);
    }

    public ResourceLocation getTextureLocation(Entity p_116083_) {
        return LOCATION;
    }
}