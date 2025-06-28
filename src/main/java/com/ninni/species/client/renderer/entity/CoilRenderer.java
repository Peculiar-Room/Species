package com.ninni.species.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.server.entity.mob.update_3.Coil;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.List;

import static com.ninni.species.Species.MOD_ID;

public class CoilRenderer extends EntityRenderer<Coil> {
    private final ModelPart coilKnot;
    private final ModelPart coil;
    public static final ResourceLocation TEXTURE_KNOT = new ResourceLocation(MOD_ID, "textures/entity/hanger/coil/coil_knot.png");
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/hanger/coil/coil.png");
    public static final ResourceLocation TEXTURE_ROPE = new ResourceLocation(MOD_ID, "textures/entity/hanger/coil/coil_rope.png");

    public CoilRenderer(EntityRendererProvider.Context context) {
        super(context);
        ModelPart modelPart = context.bakeLayer(SpeciesEntityModelLayers.COIL);
        this.coil = modelPart.getChild("knot");
        ModelPart modelPartKnot = context.bakeLayer(SpeciesEntityModelLayers.COIL_KNOT);
        this.coilKnot = modelPartKnot.getChild("knot");
    }

    @Override
    public ResourceLocation getTextureLocation(Coil coil) {
        return coil.isKnot() ? TEXTURE_KNOT : TEXTURE;
    }

    @Override
    public void render(Coil entity, float yaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, yaw, partialTicks, poseStack, buffer, packedLight);

        Direction direction2;
        if (Math.abs(entity.getXRot()) > 45) direction2 = entity.getXRot() > 0 ? Direction.DOWN : Direction.UP;
        else direction2 = Direction.fromYRot(entity.getYRot());

        Vec3 offset = Vec3.atLowerCornerOf(direction2.getNormal()).scale(0.125);
        BlockPos lightSamplePos = BlockPos.containing(entity.position().add(offset));

        int startLight = LevelRenderer.getLightColor(entity.level(), lightSamplePos);

        // Knot renderer
        if (entity.isKnot()) {

            poseStack.pushPose();
            VertexConsumer knotBuilder = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
            poseStack.scale(1.01F, 1.01F, 1.01F);
            poseStack.translate(0, -1, 0);
            this.coilKnot.render(poseStack, knotBuilder, startLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, entity.isBeingPlaced() ? 0.5F : 1);
            poseStack.popPose();
        }
        else {
            poseStack.pushPose();
            VertexConsumer knotBuilder = buffer.getBuffer(RenderType.entityTranslucent(this.getTextureLocation(entity)));
            poseStack.scale(1.01F, 1.01F, 1.01F);
            poseStack.translate(0, -1.375, 0);

            this.coil.render(poseStack, knotBuilder, startLight, OverlayTexture.NO_OVERLAY, 1, 1, 1, entity.isBeingPlaced() ? 0.5F : 1);
            poseStack.popPose();
        }

        // Coil renderer
        Coil endPoint = entity.getEndPoint();
        if (!entity.isStartPoint() && endPoint != null) {
            Direction direction3;
            if (Math.abs(endPoint.getXRot()) > 45) direction3 = endPoint.getXRot() > 0 ? Direction.DOWN : Direction.UP;
            else direction3 = Direction.fromYRot(endPoint.getYRot());

            Vec3 offset2 = Vec3.atLowerCornerOf(direction3.getNormal()).scale(0.125);
            BlockPos lightSamplePos2 = BlockPos.containing(endPoint.position().add(offset2));

            int endLight = LevelRenderer.getLightColor(entity.level(), lightSamplePos2);
            int avgPackedLight = averagePackedLight(startLight, endLight);

            VertexConsumer builder = buffer.getBuffer(RenderType.entityCutoutNoCull(TEXTURE_ROPE));

            Vec3 start = entity.getBoundingBox().getCenter().subtract(entity.position());
            Vec3 end = endPoint.getBoundingBox().getCenter().subtract(entity.position());

            int looseness = entity.getLooseness();

            float distance = (float) start.distanceTo(end);
            float sag = distance * 0.1F * looseness;

            Vec3 middle = start.add(end).scale(0.5).add(0, -sag, 0);

            Vec3 direction = end.subtract(start).normalize();
            Vec3 up = Math.abs(direction.y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
            Vec3 right = direction.cross(up).normalize().scale(0.2);
            Vec3 forward = direction.cross(right).normalize().scale(0.2);

            poseStack.pushPose();

            int segments = Math.min(24, (int) (distance * 2));
            List<Vec3> curve = new ArrayList<>();

            for (int i = 0; i <= segments; i++) {
                float t = i / (float) segments;
                curve.add(quadraticBezier(start, middle, end, t));
            }

            float accumulatedV = 0;

            for (int i = 0; i < curve.size() - 1; i++) {
                Vec3 p0 = curve.get(i);
                Vec3 p1 = curve.get(i + 1);
                float nextV = accumulatedV + ((float) p0.distanceTo(p1));

                drawCoilQuad(poseStack, builder, p0, p1, right, accumulatedV, nextV, avgPackedLight);
                drawCoilQuad(poseStack, builder, p0, p1, forward, accumulatedV, nextV, avgPackedLight);

                accumulatedV = nextV;
            }

            poseStack.popPose();
        }
    }

    private static int averagePackedLight(int a, int b) {
        int blockA = a & 0xFFFF;
        int skyA = (a >> 20) & 0xFFFF;
        int blockB = b & 0xFFFF;
        int skyB = (b >> 20) & 0xFFFF;

        int block = (blockA + blockB) / 2;
        int sky = (skyA + skyB) / 2;

        return (sky << 20) | block;
    }

    private Vec3 quadraticBezier(Vec3 p0, Vec3 p1, Vec3 p2, float t) {
        double it = 1 - t;
        return p0.scale(it * it)
                .add(p1.scale(2 * it * t))
                .add(p2.scale(t * t));
    }

    private void drawCoilQuad(PoseStack poseStack, VertexConsumer builder, Vec3 base, Vec3 tip, Vec3 side, float vStart, float vEnd, int packedLight) {
        PoseStack.Pose pose = poseStack.last();

        builder.vertex(pose.pose(), (float)(base.x + side.x), (float)(base.y + side.y), (float)(base.z + side.z))
                .color(1F, 1F, 1F, 1F)
                .uv(0F, vStart)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();

        builder.vertex(pose.pose(), (float)(base.x - side.x), (float)(base.y - side.y), (float)(base.z - side.z))
                .color(1F, 1F, 1F, 1F)
                .uv(1F, vStart)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();

        builder.vertex(pose.pose(), (float)(tip.x - side.x), (float)(tip.y - side.y), (float)(tip.z - side.z))
                .color(1F, 1F, 1F, 1F)
                .uv(1F, vEnd)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();

        builder.vertex(pose.pose(), (float)(tip.x + side.x), (float)(tip.y + side.y), (float)(tip.z + side.z))
                .color(1F, 1F, 1F, 1F)
                .uv(0F, vEnd)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();
    }

    @Override
    public boolean shouldRender(Coil entity, Frustum frustum, double v, double v1, double v2) {
        if (!entity.isStartPoint() && entity.getEndPoint() != null) return true;
        return super.shouldRender(entity, frustum, v, v1, v2);
    }

    public static LayerDefinition createKnotBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("knot",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-3.0F, -4.0F, -3.0F, 6.0F, 8.0F, 6.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();
        partdefinition.addOrReplaceChild("knot",
                CubeListBuilder.create().texOffs(0, 0)
                        .addBox(-2.0F, -2.0F, -2.0F, 4.0F, 4.0F, 4.0F),
                PartPose.offset(0.0F, 24.0F, 0.0F));
        return LayerDefinition.create(meshdefinition, 16, 16);
    }

}
