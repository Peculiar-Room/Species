package com.ninni.species.client.renderer.block;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.client.model.mob_heads.*;
import com.ninni.species.server.block.MobHeadBlock;
import com.ninni.species.server.block.WallMobHeadBlock;
import com.ninni.species.server.block.entity.MobHeadBlockEntity;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import net.minecraft.Util;
import net.minecraft.client.model.geom.EntityModelSet;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.RotationSegment;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.Map;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class MobHeadBlockEntityRenderer implements BlockEntityRenderer<MobHeadBlockEntity> {
    public final Map<MobHeadBlock.Type, MobHeadModelBase> modelByType;
    public static final Map<MobHeadBlock.Type, ResourceLocation> SKIN_BY_TYPE = Util.make(Maps.newHashMap(), (p_261388_) -> {
        p_261388_.put(MobHeadBlock.Types.GHOUL, new ResourceLocation(MOD_ID, "textures/entity/ghoul/ghoul.png"));
        p_261388_.put(MobHeadBlock.Types.WICKED, new ResourceLocation(MOD_ID, "textures/entity/wicked/wicked.png"));
        p_261388_.put(MobHeadBlock.Types.QUAKE, new ResourceLocation(MOD_ID, "textures/entity/quake/quake.png"));
        p_261388_.put(MobHeadBlock.Types.BEWEREAGER, new ResourceLocation(MOD_ID, "textures/entity/bewereager/bewereager.png"));
    });

    public static Map<MobHeadBlock.Type, MobHeadModelBase> createMobHeadRenderers(EntityModelSet root) {
        ImmutableMap.Builder<MobHeadBlock.Type, MobHeadModelBase> builder = ImmutableMap.builder();
        builder.put(MobHeadBlock.Types.GHOUL, new GhoulHeadModel(root.bakeLayer(SpeciesEntityModelLayers.GHOUL_HEAD)));
        builder.put(MobHeadBlock.Types.WICKED, new WickedHeadModel(root.bakeLayer(SpeciesEntityModelLayers.WICKED_CANDLE)));
        builder.put(MobHeadBlock.Types.QUAKE, new QuakeHeadModel(root.bakeLayer(SpeciesEntityModelLayers.QUAKE_HEAD)));
        builder.put(MobHeadBlock.Types.BEWEREAGER, new BewereagerHeadModel(root.bakeLayer(SpeciesEntityModelLayers.BEWEREAGER_HEAD)));
        return builder.build();
    }

    public MobHeadBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
        this.modelByType = createMobHeadRenderers(context.getModelSet());
    }

    public void render(MobHeadBlockEntity blockEntity, float v, PoseStack poseStack, MultiBufferSource bufferSource, int i1, int i2) {
        float f = blockEntity.getAnimation(v);
        BlockState blockstate = blockEntity.getBlockState();
        boolean flag = blockstate.getBlock() instanceof WallMobHeadBlock;
        Direction direction = flag ? blockstate.getValue(WallMobHeadBlock.FACING) : null;
        int i = flag ? RotationSegment.convertToSegment(direction.getOpposite()) : blockstate.getValue(MobHeadBlock.ROTATION);
        float f1 = RotationSegment.convertToDegrees(i);
        MobHeadBlock.Type type = blockstate.getBlock() instanceof WallMobHeadBlock ? ((WallMobHeadBlock)blockstate.getBlock()).getType() : ((MobHeadBlock)blockstate.getBlock()).getType();
        MobHeadModelBase modelBase = this.modelByType.get(type);
        RenderType rendertype = getRenderType(type);
        renderMobHead(direction, f1, f, poseStack, bufferSource, i1, modelBase, rendertype, null, type, false);
    }

    public static void renderMobHead(@Nullable Direction direction, float v, float v1, PoseStack poseStack, MultiBufferSource multiBufferSource, int i, MobHeadModelBase modelBase, RenderType renderType, @Nullable ItemDisplayContext context, MobHeadBlock.Type type, boolean isLayer) {
        poseStack.pushPose();

        if (direction == null) {
            poseStack.translate(0.5F, 0.0F, 0.5F);
        } else {
            if (type == MobHeadBlock.Types.QUAKE) poseStack.translate(0.5F - (float)direction.getStepX() * 0.18625F, 0.25F, 0.5F - (float)direction.getStepZ() * 0.18625F);
            else if (type == MobHeadBlock.Types.BEWEREAGER) poseStack.translate(0.5F - (float)direction.getStepX() * 0.15F, 0.1925F, 0.5F - (float)direction.getStepZ() * 0.15F);
            else poseStack.translate(0.5F - (float)direction.getStepX() * 0.25F, 0.25F, 0.5F - (float)direction.getStepZ() * 0.25F);
        }
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        if (context != null) {
            if (type == MobHeadBlock.Types.GHOUL) {
                if (context == ItemDisplayContext.GUI) {
                    poseStack.scale(0.85F, 0.85F, 0.85F);
                    poseStack.translate(0.0F, 0.0F, 0.125F);
                }
            }
            if (type == MobHeadBlock.Types.QUAKE) {
                if (context == ItemDisplayContext.GUI) {
                    poseStack.scale(0.9F, 0.9F, 0.9F);
                }
            }
            if (type == MobHeadBlock.Types.WICKED) {
                if (context == ItemDisplayContext.FIRST_PERSON_LEFT_HAND || context == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND) {
                    poseStack.translate(0.0F, -0.2F, 0.0F);
                }
            }
            if (type == MobHeadBlock.Types.BEWEREAGER) {
                if (context == ItemDisplayContext.GUI) {
                    poseStack.scale(0.9F, 0.9F, 0.9F);
                }
            }
        }

        if (isLayer) {
            if (type == MobHeadBlock.Types.WICKED) {
                poseStack.scale(1.15F, 1.3F,1.15F);
            }
            if (type == MobHeadBlock.Types.GHOUL) {
                poseStack.scale(1F, 1.15F,1F);
            }
            if (type == MobHeadBlock.Types.QUAKE) {
                poseStack.scale(1F, 1.15F,1F);
            }
        }

        VertexConsumer vertexconsumer = multiBufferSource.getBuffer(renderType);
        modelBase.setupAnim(v1, v, 0.0F);
        modelBase.renderToBuffer(poseStack, vertexconsumer, i, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();
    }

    public static RenderType getRenderType(MobHeadBlock.Type type) {
        return RenderType.entityCutoutNoCullZOffset(SKIN_BY_TYPE.get(type));
    }
}