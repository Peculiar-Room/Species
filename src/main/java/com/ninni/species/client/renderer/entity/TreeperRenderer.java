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