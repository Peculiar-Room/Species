package com.ninni.species.client.renderer.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.client.model.mob.update_2.SpringlingModel;
import com.ninni.species.server.entity.mob.update_2.Springling;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public class SpringlingRenderer<T extends LivingEntity> extends MobRenderer<Springling, SpringlingModel<Springling>> {
    public static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/springling/springling.png");
    public static final ResourceLocation TEXTURE_NECK = new ResourceLocation(MOD_ID, "textures/entity/springling/springling_neck.png");
    public static final ResourceLocation TEXTURE_PISTON = new ResourceLocation(MOD_ID, "textures/entity/springling/springling.png");
    public static final ResourceLocation TEXTURE_PISTON_NECK = new ResourceLocation(MOD_ID, "textures/entity/springling/springling_neck_piston.png");

    public SpringlingRenderer(EntityRendererProvider.Context ctx) {
        super(ctx, new SpringlingModel<>(ctx.bakeLayer(SpeciesEntityModelLayers.SPRINGLING)), 0F);
    }

    @Override
    public void render(Springling entity, float entityYaw, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int packedLight) {
        super.render(entity, entityYaw, partialTicks, poseStack, buffer, packedLight);

        float interpolatedStep = Mth.lerp(partialTicks, entity.previousQuantizedStep, entity.currentQuantizedStep) / 10.0f;
        float height = interpolatedStep * 1.469F + 0.8125F;
        float vMax = height;

        poseStack.pushPose();

        Vec3 base = new Vec3(0, 0, 0);
        Vec3 tip = new Vec3(0, height, 0);

        this.setupRotations(entity, poseStack, getBob(entity, partialTicks), entityYaw, partialTicks);

        if (this.getModel().young) poseStack.scale(0.25F, 0.25F, 0.25F);
        poseStack.translate(0.0D, 1.0625F, 0.0D);
        poseStack.mulPose(new Quaternionf().rotationY(interpolatedStep * -2));

        ResourceLocation texture = entity.getName().getString().equalsIgnoreCase("piston") ? TEXTURE_PISTON_NECK : TEXTURE_NECK;
        VertexConsumer builder = buffer.getBuffer(RenderType.entityCutoutNoCull(texture));

        float halfWidth = 0.125f;
        int overlay = getOverlayCoords(entity, this.getWhiteOverlayProgress(entity, partialTicks));

        drawNeckQuad(poseStack, builder, base.add(0, 0, -halfWidth), tip.add(0, 0, -halfWidth), new Vec3(halfWidth, 0, 0), new Vec3(0, 0, -1), vMax, packedLight, overlay);
        drawNeckQuad(poseStack, builder, base.add(0, 0, halfWidth), tip.add(0, 0, halfWidth), new Vec3(-halfWidth, 0, 0), new Vec3(0, 0, 1), vMax, packedLight, overlay);
        drawNeckQuad(poseStack, builder, base.add(-halfWidth, 0, 0), tip.add(-halfWidth, 0, 0), new Vec3(0, 0, halfWidth), new Vec3(-1, 0, 0), vMax, packedLight, overlay);
        drawNeckQuad(poseStack, builder, base.add(halfWidth, 0, 0), tip.add(halfWidth, 0, 0), new Vec3(0, 0, -halfWidth), new Vec3(1, 0, 0), vMax, packedLight, overlay);

        poseStack.popPose();
    }


    void drawNeckQuad(PoseStack poseStack, VertexConsumer builder, Vec3 base, Vec3 tip, Vec3 side, Vec3 normalDir, float vMax, int packedLight, int overlay) {
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



    @Override
    public ResourceLocation getTextureLocation(Springling entity) {
        return entity.getName().getString().equalsIgnoreCase("piston") ? TEXTURE_PISTON : TEXTURE;
    }
}