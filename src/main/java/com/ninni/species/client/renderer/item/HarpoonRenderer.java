package com.ninni.species.client.renderer.item;


import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.server.entity.mob.update_3.Harpoon;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class HarpoonRenderer<T extends Harpoon> extends EntityRenderer<T> {
    public static final ResourceLocation TEXTURE_ROPE = new ResourceLocation(MOD_ID, "textures/entity/hanger/coil/coil_rope.png");
    public static final ResourceLocation TEXTURE_HOOK = new ResourceLocation(MOD_ID, "textures/entity/hanger/hook/harpoon_hook.png");

    public HarpoonRenderer(EntityRendererProvider.Context p_174008_) {
        super(p_174008_);
    }

    @Override
    public void render(T entity, float v, float v1, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, v, v1, poseStack, buffer, packedLight);
        if (entity.getOwner() != null && entity.getOwner() instanceof Player player && !entity.ownerPositionO.equals(new Vec3(0, 0, 0)) && !entity.ownerPosition.equals(new Vec3(0, 0, 0))) {
            Vec3 playerPositionLerped = new Vec3(
                    Mth.lerp(v1, entity.ownerPositionO.x, entity.ownerPosition.x),
                    Mth.lerp(v1, entity.ownerPositionO.y, entity.ownerPosition.y),
                    Mth.lerp(v1, entity.ownerPositionO.z, entity.ownerPosition.z)
            );
            Vec3 harpoonPositionLerped = new Vec3(
                    Mth.lerp(v1, entity.xo, entity.getX()),
                    Mth.lerp(v1, entity.yo, entity.getY()),
                    Mth.lerp(v1, entity.zo, entity.getZ())
            );


            VertexConsumer builder = buffer.getBuffer(RenderType.entityCutoutNoCull(getTextureLocation(entity)));

            Vec3 base = new Vec3(0, -0.05, 0);
            float yawRad = -player.getYRot() * Mth.DEG_TO_RAD;
            float pitchRad = player.getXRot() * Mth.DEG_TO_RAD;
            Vec3 handOffset = new Vec3(player.getUsedItemHand() == InteractionHand.MAIN_HAND && player.getMainArm() == HumanoidArm.RIGHT || player.getUsedItemHand() == InteractionHand.OFF_HAND && player.getMainArm() == HumanoidArm.LEFT ? -0.25 : 0.25, 0.0, 0);

            Vec3 tip = playerPositionLerped.subtract(harpoonPositionLerped)
                    .add(0, player.getBbHeight(), 0)
                    .add( handOffset.yRot(yawRad).xRot(pitchRad));

            Vec3 direction = tip.subtract(base);
            double length = direction.length();

            if (!entity.isAlive()) return;

            Vec3 dirNorm = direction.normalize();
            Vec3 up = Math.abs(dirNorm.y) < 0.9 ? new Vec3(0, 1, 0) : new Vec3(1, 0, 0);
            Vec3 right = dirNorm.cross(up).normalize().scale(0.2);
            Vec3 forward = dirNorm.cross(right).normalize().scale(0.2);


            int startLight = entity.getAnchorPos() == null ? packedLight : LevelRenderer.getLightColor(entity.level(), entity.getAnchorPos().below());
            int endLight = LevelRenderer.getLightColor(entity.level(), BlockPos.containing(player.position()));
            int avgPackedLight = averagePackedLight(startLight, endLight);

            poseStack.pushPose();
            float vMax = (float) length;

            drawRopeQuad(poseStack, builder, base, tip, right, vMax, avgPackedLight, 1, 1);
            drawRopeQuad(poseStack, builder, base, tip, forward, vMax, avgPackedLight, 1, 1);

            poseStack.popPose();

            VertexConsumer hookBuilder = buffer.getBuffer(RenderType.entityTranslucent(TEXTURE_HOOK));

            poseStack.pushPose();

            poseStack.translate(base.x, base.y + 0.15, base.z);

            float hookScale = 0.5F;
            poseStack.scale(-hookScale, hookScale, hookScale);

            int hookLight = LevelRenderer.getLightColor(entity.level(), BlockPos.containing(entity.position()));

            drawHookCross(poseStack, hookBuilder, hookLight);
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

    @Override
    public boolean shouldRender(T hook, Frustum frustum, double v, double v1, double v2) {
        if (hook.getOwner() != null) return true;
        return super.shouldRender(hook, frustum, v, v1, v2);
    }

    @Override
    public ResourceLocation getTextureLocation(T p_114482_) {
        return TEXTURE_ROPE;
    }

    private void drawHookCross(PoseStack poseStack, VertexConsumer builder, int packedLight) {
        PoseStack.Pose pose = poseStack.last();

        float halfSize = 0.5f;

        // Get camera-aligned normal (billboarding effect)
        Vec3 viewNormal = new Vec3(pose.pose().m02(), pose.pose().m12(), pose.pose().m22()).normalize();

        // First quad (XY plane, facing camera normal)
        builder.vertex(pose.pose(), -halfSize, -halfSize, 0f)
                .color(1f, 1f, 1f, 1f).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                .normal((float) viewNormal.x, (float) viewNormal.y, (float) viewNormal.z).endVertex();

        builder.vertex(pose.pose(), halfSize, -halfSize, 0f)
                .color(1f, 1f, 1f, 1f).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                .normal((float) viewNormal.x, (float) viewNormal.y, (float) viewNormal.z).endVertex();

        builder.vertex(pose.pose(), halfSize, halfSize, 0f)
                .color(1f, 1f, 1f, 1f).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                .normal((float) viewNormal.x, (float) viewNormal.y, (float) viewNormal.z).endVertex();

        builder.vertex(pose.pose(), -halfSize, halfSize, 0f)
                .color(1f, 1f, 1f, 1f).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                .normal((float) viewNormal.x, (float) viewNormal.y, (float) viewNormal.z).endVertex();

        // Second quad (YZ plane — rotated 90° around Y)
        Vec3 viewNormal2 = new Vec3(pose.pose().m00(), pose.pose().m10(), pose.pose().m20()).normalize();

        builder.vertex(pose.pose(), 0f, -halfSize, -halfSize)
                .color(1f, 1f, 1f, 1f).uv(0f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                .normal((float) viewNormal2.x, (float) viewNormal2.y, (float) viewNormal2.z).endVertex();

        builder.vertex(pose.pose(), 0f, -halfSize, halfSize)
                .color(1f, 1f, 1f, 1f).uv(1f, 1f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                .normal((float) viewNormal2.x, (float) viewNormal2.y, (float) viewNormal2.z).endVertex();

        builder.vertex(pose.pose(), 0f, halfSize, halfSize)
                .color(1f, 1f, 1f, 1f).uv(1f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                .normal((float) viewNormal2.x, (float) viewNormal2.y, (float) viewNormal2.z).endVertex();

        builder.vertex(pose.pose(), 0f, halfSize, -halfSize)
                .color(1f, 1f, 1f, 1f).uv(0f, 0f).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight)
                .normal((float) viewNormal2.x, (float) viewNormal2.y, (float) viewNormal2.z).endVertex();
    }


    void drawRopeQuad(PoseStack poseStack, VertexConsumer builder, Vec3 base, Vec3 tip, Vec3 side, float vMax, int packedLight, float baseOpacity, float tipOpacity) {
        PoseStack.Pose pose = poseStack.last();

        builder.vertex(pose.pose(), (float)(base.x + side.x), (float)(base.y + side.y), (float)(base.z + side.z))
                .color(1F, 1F, 1F, baseOpacity)
                .uv(0F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();

        builder.vertex(pose.pose(), (float)(base.x - side.x), (float)(base.y - side.y), (float)(base.z - side.z))
                .color(1F, 1F, 1F, baseOpacity)
                .uv(1F, 0F)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();

        builder.vertex(pose.pose(), (float)(tip.x - side.x), (float)(tip.y - side.y), (float)(tip.z - side.z))
                .color(1F, 1F, 1F, tipOpacity)
                .uv(1F, vMax)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();

        builder.vertex(pose.pose(), (float)(tip.x + side.x), (float)(tip.y + side.y), (float)(tip.z + side.z))
                .color(1F, 1F, 1F, tipOpacity)
                .uv(0F, vMax)
                .overlayCoords(OverlayTexture.NO_OVERLAY)
                .uv2(packedLight)
                .normal(pose.normal(), 0, 1, 0)
                .endVertex();
    }
}
