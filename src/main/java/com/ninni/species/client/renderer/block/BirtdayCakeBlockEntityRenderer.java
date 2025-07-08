package com.ninni.species.client.renderer.block;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.server.block.BirtdayCakeBlock;
import com.ninni.species.server.block.entity.BirtdayCakeBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.network.chat.Component;

public class BirtdayCakeBlockEntityRenderer implements BlockEntityRenderer<BirtdayCakeBlockEntity> {

    public BirtdayCakeBlockEntityRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(BirtdayCakeBlockEntity entity, float partialTicks, PoseStack poseStack, MultiBufferSource buffer, int light, int overlay) {
        if (entity.getAge() > 0) {
            Minecraft mc = Minecraft.getInstance();
            Font font = mc.font;

            Component text = Component.literal(Integer.toString(entity.getAge()));

            double dx = entity.getBlockPos().getX() + 0.5 - mc.gameRenderer.getMainCamera().getPosition().x;
            double dz = entity.getBlockPos().getZ() + 0.5 - mc.gameRenderer.getMainCamera().getPosition().z;
            float angle = (float) Math.toDegrees(Math.atan2(dz, dx)) - 90.0F;
            float time = (entity.getLevel().getGameTime() + partialTicks) % 360;
            float floatOffset = (float) Math.sin(time / 20.0F) * 0.05F;

            poseStack.pushPose();

            poseStack.translate(0.5, 0.9 + floatOffset, 0.5);
            poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(-angle));
            poseStack.scale(-0.025F, -0.025F, 0.025F);

            int color = entity.getBlockState().getValue(BirtdayCakeBlock.LIT) ? 0xFFFFFF : 0x47b7d8;
            font.drawInBatch(text, -font.width(text) / 2f, 0, color, false, poseStack.last().pose(), buffer, Font.DisplayMode.NORMAL, 0, light);

            poseStack.popPose();
        }

    }


}