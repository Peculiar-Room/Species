package com.ninni.species.client.renderer.entity.feature;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.client.model.mob.update_2.SpringlingModel;
import com.ninni.species.client.model.mob.update_3.SpectreModel;
import com.ninni.species.client.renderer.entity.SpectreRenderer;
import com.ninni.species.client.renderer.entity.SpringlingRenderer;
import com.ninni.species.client.renderer.entity.rendertypes.OffsetRenderLayer;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.server.entity.mob.update_2.Springling;
import com.ninni.species.server.entity.mob.update_3.Spectre;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

@OnlyIn(Dist.CLIENT)
public class SpringlingNeckLayer extends RenderLayer<Springling, SpringlingModel<Springling>> {

    public SpringlingNeckLayer(RenderLayerParent<Springling, SpringlingModel<Springling>> parent) {
        super(parent);
    }

    @Override
    public void render(PoseStack poseStack, MultiBufferSource buffer, int packedLight, Springling entity, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch) {
        float interpolatedStep = Mth.lerp(partialTicks, entity.previousQuantizedStep, entity.currentQuantizedStep) / 10.0f;
        float height = interpolatedStep * -1.469F - 0.8125F;
        float vMax = height;


        poseStack.pushPose();

        Vec3 base = new Vec3(0, 0, 0);
        Vec3 tip = new Vec3(0, height, 0);
        poseStack.translate(0.0D, 0.4375F, 0.0D);
        poseStack.mulPose(new Quaternionf().rotationY(interpolatedStep * -2));

        ResourceLocation texture = entity.getName().getString().equalsIgnoreCase("piston") ? SpringlingRenderer.TEXTURE_PISTON_NECK : SpringlingRenderer.TEXTURE_NECK;

        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));
        int overlay = LivingEntityRenderer.getOverlayCoords(entity, 0.0F);

        float halfWidth = 0.125f;

        drawNeckQuad(poseStack, builder, base.add(0, 0, -halfWidth), tip.add(0, 0, -halfWidth), new Vec3(halfWidth, 0, 0), new Vec3(0, 0, -1), vMax, packedLight, overlay);
        drawNeckQuad(poseStack, builder, base.add(0, 0, halfWidth), tip.add(0, 0, halfWidth), new Vec3(-halfWidth, 0, 0), new Vec3(0, 0, 1), vMax, packedLight, overlay);
        drawNeckQuad(poseStack, builder, base.add(-halfWidth, 0, 0), tip.add(-halfWidth, 0, 0), new Vec3(0, 0, halfWidth), new Vec3(-1, 0, 0), vMax, packedLight, overlay);
        drawNeckQuad(poseStack, builder, base.add(halfWidth, 0, 0), tip.add(halfWidth, 0, 0), new Vec3(0, 0, -halfWidth), new Vec3(1, 0, 0), vMax, packedLight, overlay);

        poseStack.popPose();
    }


    public void drawNeckQuad(PoseStack poseStack, VertexConsumer builder, Vec3 base, Vec3 tip, Vec3 side, Vec3 normalDir, float vMax, int packedLight, int overlay) {
        PoseStack.Pose pose = poseStack.last();
        float uMin = 0.375f;
        float uMax = 0.625f;

        builder.vertex(pose.pose(), (float)(base.x + side.x), (float)(base.y + side.y), (float)(base.z + side.z))
                .color(1F, 1F, 1F, (float) 1.0)
                .uv(uMin, 0F)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(pose.normal(), (float)normalDir.x, (float)normalDir.y, (float)normalDir.z)
                .endVertex();

        builder.vertex(pose.pose(), (float)(base.x - side.x), (float)(base.y - side.y), (float)(base.z - side.z))
                .color(1F, 1F, 1F, (float) 1.0)
                .uv(uMax, 0F)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(pose.normal(), (float)normalDir.x, (float)normalDir.y, (float)normalDir.z)
                .endVertex();

        builder.vertex(pose.pose(), (float)(tip.x - side.x), (float)(tip.y - side.y), (float)(tip.z - side.z))
                .color(1F, 1F, 1F, (float) 1.0)
                .uv(uMax, vMax)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(pose.normal(), (float)normalDir.x, (float)normalDir.y, (float)normalDir.z)
                .endVertex();

        builder.vertex(pose.pose(), (float)(tip.x + side.x), (float)(tip.y + side.y), (float)(tip.z + side.z))
                .color(1F, 1F, 1F, (float) 1.0)
                .uv(uMin, vMax)
                .overlayCoords(overlay)
                .uv2(packedLight)
                .normal(pose.normal(), (float)normalDir.x, (float)normalDir.y, (float)normalDir.z)
                .endVertex();
    }
}