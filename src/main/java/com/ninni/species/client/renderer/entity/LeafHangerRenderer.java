package com.ninni.species.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.client.model.mob.update_3.LeafHangerModel;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.server.entity.mob.update_3.LeafHanger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import org.jetbrains.annotations.Nullable;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class LeafHangerRenderer extends HangerRenderer<LeafHanger, LeafHangerModel<LeafHanger>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/hanger/leaf_hanger.png");
    public static final ResourceLocation TEXTURE_RARE = new ResourceLocation(MOD_ID, "textures/entity/hanger/fang_hanger.png");

    public LeafHangerRenderer(EntityRendererProvider.Context context) {
        super(context, new LeafHangerModel<>(context.bakeLayer(SpeciesEntityModelLayers.LEAF_HANGER)), 0.5f);
    }

    @Override
    public ResourceLocation getTextureLocation(LeafHanger entity) {
        return entity.isRare() ? TEXTURE_RARE : TEXTURE;
    }

    @Override
    public @Nullable RenderType getRenderType(LeafHanger entity, boolean b, boolean b1, boolean b2) {
        return entity.isRare() ? RenderType.entityTranslucent(TEXTURE_RARE) : RenderType.entityTranslucent(TEXTURE);
    }

    @Override
    public void render(LeafHanger entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);
        VertexConsumer builder = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE_COIL));

        Vec3 base = new Vec3(0, entity.getTongueOffset(), 0);
        Vec3 tip = new Vec3(entity.getCurrentTonguePos());
        Vec3 direction = tip.subtract(base);
        double length = direction.length();

        if (!entity.isAlive()) return;

        Vec3 dirNorm = direction.normalize();
        Vec3 up = Math.abs(dirNorm.y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
        Vec3 right = dirNorm.cross(up).normalize().scale(0.3);
        Vec3 forward = dirNorm.cross(right).normalize().scale(0.3);

        poseStack.pushPose();
        float vMax = (float) length;
        float baseOpacity = entity.isTongueOut() && !entity.isPullingTarget() ? 0.4F : 1;
        float tipOpacity = entity.isTongueOut() && !entity.isPullingTarget() ? 0F : 1;
        tip = tip.add(0,-0.135,0);

        drawTongueQuad(poseStack, builder, base, tip, right, vMax, packedLight, baseOpacity, tipOpacity);
        drawTongueQuad(poseStack, builder, base, tip, forward, vMax, packedLight, baseOpacity, tipOpacity);
        poseStack.popPose();

        if (length > 0.2 && !entity.isPullingTarget()) {
            BlockState state = entity.getBaitBlockState();
            BlockPos pos = BlockPos.containing(entity.position().add(tip));
            int light = LevelRenderer.getLightColor(entity.level(), pos);
            tip = tip.add(0,+0.135,0);

            poseStack.pushPose();
            poseStack.translate(tip.x - 0.5, tip.y, tip.z - 0.5);

            BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
            ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();

            renderer.tesselateBlock(
                    entity.level(),
                    model,
                    state,
                    pos,
                    poseStack,
                    buffer.getBuffer(RenderType.cutout()),
                    false,
                    RandomSource.create(),
                    42L,
                    light,
                    ModelData.EMPTY,
                    RenderType.cutout()
            );

            poseStack.popPose();
        }

    }

}
