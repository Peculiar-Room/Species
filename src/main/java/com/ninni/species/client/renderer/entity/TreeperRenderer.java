package com.ninni.species.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.client.renderer.entity.feature.TreeperCanopyLayer;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.client.model.mob.update_2.TreeperModel;
import com.ninni.species.client.renderer.entity.feature.TreeperFeatureRenderer;
import com.ninni.species.server.entity.mob.update_2.Springling;
import com.ninni.species.server.entity.mob.update_2.Treeper;
import net.minecraft.client.GraphicsStatus;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.ModelBlockRenderer;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import org.joml.Matrix4f;
import org.joml.Quaternionf;
import org.joml.Vector4f;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class TreeperRenderer extends MobRenderer<Treeper, TreeperModel<Treeper>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/treeper/treeper.png");
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


    public TreeperRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new TreeperModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.TREEPER)), 0F);
        this.addLayer(new TreeperFeatureRenderer<>(this));
        this.addLayer(new TreeperCanopyLayer(this));
    }

    @Override
    public ResourceLocation getTextureLocation(Treeper entity) {
        return TEXTURE;
    }

    @Override
    public void render(Treeper entity, float entityYaw, float partialTick, PoseStack stack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTick, stack, buffer, packedLight);
        stack.pushPose();
        float bodyRotLerp = Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);

        float animationProgress = this.getBob(entity, partialTick);
        this.setupRotations(entity, stack, animationProgress, bodyRotLerp, partialTick);
        stack.scale(-1.0F, -1.0F, 1.0F);
        this.scale(entity, stack, partialTick);

        this.model.root().translateAndRotate(stack);
        this.model.getTrunk().translateAndRotate(stack);
        this.model.getCanopy().translateAndRotate(stack);


        stack.translate(0,-7,0);

        BlockState state = Blocks.SPRUCE_LEAVES.defaultBlockState();
        renderCanopy(entity, stack, buffer, state, partialTick);

        stack.popPose();
    }

    private void renderCanopy(Treeper entity, PoseStack poseStack, MultiBufferSource buffer, BlockState state, float partialTick) {for (int y = 0; y < canopyLayers.length; y++) {
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


    @Override
    public boolean shouldRender(Treeper treeper, Frustum frustum, double v, double v1, double v2) {
        if (this.shouldRenderAll(treeper, frustum, v, v1, v2)) {
            return true;
        } else {
            Entity entity = treeper.getLeashHolder();
            return entity != null && frustum.isVisible(entity.getBoundingBoxForCulling());
        }
    }

    public boolean shouldRenderAll(Treeper treeper, Frustum frustum, double v, double v1, double v2) {
        if (!treeper.shouldRender(v, v1, v2)) {
            return false;
        } else if (treeper.noCulling) {
            return true;
        } else {
            AABB aabb = treeper.getBoundingBoxForCulling().inflate(3D);
            if (aabb.hasNaN() || aabb.getSize() == 0.0D) {
                aabb = new AABB(treeper.getX() - 2.0D, treeper.getY() - 2.0D, treeper.getZ() - 2.0D, treeper.getX() + 2.0D, treeper.getY() + 2.0D, treeper.getZ() + 2.0D);
            }

            return frustum.isVisible(aabb);
        }
    }

}
