package com.ninni.species.client.model.entity;

import com.google.common.collect.ImmutableList;
import com.ninni.species.entity.RoombugEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.AnimalModel;
import net.minecraft.util.math.MathHelper;

import java.util.Collections;

import static net.minecraft.client.render.entity.model.EntityModelPartNames.*;
import static net.minecraft.util.math.MathHelper.clamp;

@Environment(EnvType.CLIENT)
public class RoombugEntityModel<E extends RoombugEntity> extends AnimalModel<E> {
    public static final String LEFT_ANTENNA = "left_antenna";
    public static final String RIGHT_ANTENNA = "right_antenna";
    public static final String LEFT_FORE_LEG = "left_fore_leg";
    public static final String RIGHT_FORE_LEG = "right_fore_leg";
    public static final String LEFT_MID_LEG = "left_mid_leg";
    public static final String RIGHT_MID_LEG = "right_mid_leg";
    public static final String LEFT_BACK_LEG = "left_back_leg";
    public static final String RIGHT_BACK_LEG = "right_back_leg";

    private final ModelPart body;
    private final ModelPart leftAntenna;
    private final ModelPart rightAntenna;
    private final ModelPart leftForeLeg;
    private final ModelPart rightForeLeg;
    private final ModelPart leftMidLeg;
    private final ModelPart rightMidLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart rightBackLeg;

    private final ModelPart root;

    public RoombugEntityModel(ModelPart root) {
        this.root = root;

        this.body = root.getChild(BODY);

        this.leftAntenna = body.getChild(LEFT_ANTENNA);
        this.rightAntenna = body.getChild(RIGHT_ANTENNA);
        this.leftForeLeg = body.getChild(LEFT_FORE_LEG);
        this.leftBackLeg = body.getChild(LEFT_BACK_LEG);
        this.leftMidLeg = body.getChild(LEFT_MID_LEG);
        this.rightMidLeg = body.getChild(RIGHT_MID_LEG);
        this.rightForeLeg = body.getChild(RIGHT_FORE_LEG);
        this.rightBackLeg = body.getChild(RIGHT_BACK_LEG);

    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData body = modelPartData.addChild(
                BODY,
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-8.0F, -4.0F, -10.0F, 16.0F, 4.0F, 20.0F),
                ModelTransform.pivot(0.0F, 23.0F, 0.0F)
        );

        ModelPartData leftAntenna = body.addChild(
                LEFT_ANTENNA, ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(0.0F, -0.5F, -4.0F, 0.0F, 3.0F, 4.0F),
                ModelTransform.pivot(3.0F, -3.5F, -10.0F)
        );

        ModelPartData rightAntenna = body.addChild(
                RIGHT_ANTENNA, ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(0.0F, -0.5F, -4.0F, 0.0F, 3.0F, 4.0F),
                ModelTransform.pivot(-3.0F, -3.5F, -10.0F)
        );

        ModelPartData leftForeLeg = body.addChild(
                LEFT_FORE_LEG, ModelPartBuilder.create()
                        .uv(-2, 27)
                        .cuboid(0.0F, 0.0F, -1.5F, 7.0F, 0.0F, 2.0F),
                ModelTransform.of(6.0F, 0.0F, -1.5F, 0.0F, 0.3927F, 0.1745F)
        );

        ModelPartData rightForeLeg = body.addChild(
                RIGHT_FORE_LEG, ModelPartBuilder.create()
                        .uv(-2, 27)
                        .mirrored()
                        .cuboid(-7.0F, 0.0F, -1.5F, 7.0F, 0.0F, 2.0F),
                ModelTransform.of(-6.0F, 0.0F, -1.5F, 0.0F, -0.3927F, -0.1745F)
        );

        ModelPartData leftMidLeg = body.addChild(
                LEFT_MID_LEG, ModelPartBuilder.create()
                        .uv(-2, 27)
                        .cuboid(0.0F, 0.0F, -1.5F, 7.0F, 0.0F, 2.0F),
                ModelTransform.of(6.0F, 0.0F, 0.5F, 0.0F, -0.7854F, 0.1745F)
        );

        ModelPartData rightMidLeg = body.addChild(
                RIGHT_MID_LEG, ModelPartBuilder.create()
                .uv(-2, 27)
                .mirrored()
                .cuboid(-7.0F, 0.0F, -1.5F, 7.0F, 0.0F, 2.0F),
                ModelTransform.of(-6.0F, 0.0F, 0.5F, 0.0F, 0.7854F, -0.1745F)
        );

        ModelPartData leftBackLeg = body.addChild(
                LEFT_BACK_LEG, ModelPartBuilder.create()
                        .uv(-2, 27)
                        .cuboid(0.0F, 0.0F, -1.5F, 7.0F, 0.0F, 2.0F),
                ModelTransform.of(6.0F, 0.0F, 2.5F, 0.0F, -0.7854F, 0.2618F)
        );

        ModelPartData rightBackLeg = body.addChild(
                RIGHT_BACK_LEG, ModelPartBuilder.create()
                        .uv(-2, 27)
                        .mirrored()
                        .cuboid(-7.0F, 0.0F, -1.5F, 7.0F, 0.0F, 2.0F),
                ModelTransform.of(-6.0F, 0.0F, 2.5F, 0.0F, 0.7854F, -0.2618F)
        );

        return TexturedModelData.of(modelData, 80, 48);
    }

    @Override protected Iterable<ModelPart> getHeadParts() { return Collections.emptyList(); }
    @Override protected Iterable<ModelPart> getBodyParts() { return ImmutableList.of(this.root); }

    @Override
    public void setAngles(E entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        limbDistance = clamp(limbDistance, -0.45F, 0.45F);

        float speed = 1.5f;
        float degree = 1;

        this.body.pivotY = 22.5F;

        leftAntenna.roll = MathHelper.sin(animationProgress * speed * 0.1F) * degree * 0.4F * 0.25F;
        leftAntenna.pitch = MathHelper.cos(animationProgress * speed * 0.05F) * degree * 0.75F * 0.25F;

        rightAntenna.roll = MathHelper.sin(animationProgress * speed * 0.1F + (float)Math.PI) * degree * 0.4F * 0.25F;
        rightAntenna.pitch = MathHelper.cos(animationProgress * speed * 0.05F + (float)Math.PI) * degree * 0.75F * 0.25F;

        leftForeLeg.roll = MathHelper.cos(limbAngle * speed * 0.6F) * degree * 0.25F * limbDistance + 0.1745F;
        leftForeLeg.yaw = MathHelper.sin(limbAngle * speed * 0.6F) * degree * -0.5F * limbDistance + 0.3927F;
        rightForeLeg.roll = MathHelper.cos(limbAngle * speed * 0.6F + (float)Math.PI/2) * degree * 0.25F * limbDistance - 0.1745F;
        rightForeLeg.yaw = MathHelper.sin(limbAngle * speed * 0.6F + (float)Math.PI/2) * degree * -1F * limbDistance - 0.3927F;

        leftMidLeg.roll = MathHelper.sin(limbAngle * speed * 0.6F) * degree * 0.25F * limbDistance + 0.1745F;
        leftMidLeg.yaw = MathHelper.cos(limbAngle * speed * 0.6F) * degree * 1F * limbDistance - 0.1745F;
        rightMidLeg.roll = MathHelper.sin(limbAngle * speed * 0.6F + (float)Math.PI/2) * degree * 0.25F * limbDistance - 0.1745F;
        rightMidLeg.yaw = MathHelper.cos(limbAngle * speed * 0.6F + (float)Math.PI/2) * degree * 1F * limbDistance + 0.1745F;

        leftBackLeg.roll = MathHelper.cos(limbAngle * speed * 0.6F + (float)Math.PI) * degree * 0.25F * limbDistance + 0.2618F;
        leftBackLeg.yaw = MathHelper.sin(limbAngle * speed * 0.6F + (float)Math.PI) * degree * -1F * limbDistance - 0.7854F;
        rightBackLeg.roll = MathHelper.cos(limbAngle * speed * 0.6F + (float)Math.PI/1.5F) * degree * 0.25F * limbDistance - 0.2618F;
        rightBackLeg.yaw = MathHelper.sin(limbAngle * speed * 0.6F + (float)Math.PI/1.5F) * degree * -1F * limbDistance + 0.7854F;

        if (entity.isInSittingPose()) {
            this.body.pivotY = 23.5F;
            leftForeLeg.roll =0;
            leftMidLeg.roll = 0;
            leftBackLeg.roll =0;
            rightForeLeg.roll =0;
            rightMidLeg.roll = 0;
            rightBackLeg.roll =0;
        }
    }
}
