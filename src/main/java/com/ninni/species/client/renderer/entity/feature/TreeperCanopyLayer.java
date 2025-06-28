package com.ninni.species.client.renderer.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.client.model.mob.update_2.TreeperModel;
import com.ninni.species.server.entity.mob.update_2.Treeper;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.model.data.ModelData;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.RenderLayerParent;

public class TreeperCanopyLayer extends RenderLayer<Treeper, TreeperModel<Treeper>> {
    String[][] canopyLayers = {
            // Layer 0 (bottom)
            {
                    "00000000",
                    "00000000",
                    "00000000",
                    "000LL000",
                    "000LL000",
                    "00000000",
                    "00000000",
                    "00000000"
            },
            // Layer 1
            {
                    "00000000",
                    "00000000",
                    "000LL000",
                    "00LLLL00",
                    "00LLLL00",
                    "000LL000",
                    "00000000",
                    "00000000"
            },
            // Layer 2
            {
                    "00000000",
                    "00LLLL00",
                    "0LLLLLL0",
                    "0LLLLLL0",
                    "0LLLLLL0",
                    "0LLLLLL0",
                    "00LLLL00",
                    "00000000"
            },
            // Layer 3
            {
                    "00LLLL00",
                    "0LLLLLL0",
                    "LLLLLLLL",
                    "LLLLLLLL",
                    "LLLLLLLL",
                    "LLLLLLLL",
                    "0LLLLLL0",
                    "00LLLL00"
            },
            // Layer 4
            {
                    "00000000",
                    "00LLLL00",
                    "0LLLLLL0",
                    "0LLLLLL0",
                    "0LLLLLL0",
                    "0LLLLLL0",
                    "00LLLL00",
                    "00000000"
            },
            // Layer 5 (top)
            {
                    "0LLLLLL0",
                    "LLLLLLLL",
                    "LLLLLLLL",
                    "LLLLLLLL",
                    "LLLLLLLL",
                    "LLLLLLLL",
                    "LLLLLLLL",
                    "0LLLLLL0"
            }
    };

    public TreeperCanopyLayer(RenderLayerParent<Treeper, TreeperModel<Treeper>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack stack, MultiBufferSource buffer, int packedLight, Treeper entity, float limbSwing, float limbSwingAmount, float partialTick, float ageInTicks, float netHeadYaw, float headPitch) {
        stack.pushPose();
        this.getParentModel().root().translateAndRotate(stack);
        this.getParentModel().getTrunk().translateAndRotate(stack);
        this.getParentModel().getCanopy().translateAndRotate(stack);

        stack.translate(0,-5.5,0);

        BlockState state = Blocks.SPRUCE_LEAVES.defaultBlockState();
        renderCanopy(entity, stack, buffer, state, partialTick);

        stack.popPose();
    }


    private void renderCanopy(Treeper entity, PoseStack poseStack, MultiBufferSource buffer, BlockState state, float partialTick) {
        for (int y = 0; y < canopyLayers.length; y++) {
            String[] layer = canopyLayers[y];
            for (int z = 0; z < layer.length; z++) {
                char[] row = layer[z].toCharArray();
                for (int x = 0; x < row.length; x++) {
                    if (row[x] == 'L') {
                        poseStack.pushPose();

                        int dx = x - 4;
                        int dz = z - 4;

                        poseStack.translate(dx, y, dz);
                        poseStack.scale(-1.0F, -1.01F, 1.0F);
                        poseStack.translate(-1.0F, -0.999F, 0);

                        float bodyRotLerp = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);
                        float yawRad = (bodyRotLerp) * Mth.DEG_TO_RAD;
                        Vec3 rotatedOffset = new Vec3(dx, 0, dz).yRot(yawRad);

                        double worldX = entity.getX() + rotatedOffset.x + 0.5F;
                        double worldY = entity.getY() + (canopyLayers.length - 1 - y) + 7.5 + (entity.isPlanted() ? 0 : 0.95F);
                        double worldZ = entity.getZ() - rotatedOffset.z - 0.5F;

                        BlockPos lightPos = BlockPos.containing(worldX, worldY, worldZ);
                        int light = LevelRenderer.getLightColor(entity.level(), lightPos);

                        // Render block
                        RenderType renderType = Minecraft.getInstance().options.graphicsMode().get() == GraphicsStatus.FAST ? RenderType.solid() : RenderType.cutoutMipped();
                        BakedModel model = Minecraft.getInstance().getBlockRenderer().getBlockModel(state);
                        ModelBlockRenderer renderer = Minecraft.getInstance().getBlockRenderer().getModelRenderer();


                        renderer.tesselateBlock(
                                entity.level(),
                                model,
                                state,
                                lightPos,
                                poseStack,
                                buffer.getBuffer(renderType),
                                false,
                                RandomSource.create(),
                                42L,
                                light,
                                ModelData.EMPTY,
                                renderType
                        );

                        poseStack.popPose();
                    }
                }
            }
        }

    }
}
