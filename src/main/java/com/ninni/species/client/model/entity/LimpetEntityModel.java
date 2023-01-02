package com.ninni.species.client.model.entity;

import com.ninni.species.entity.LimpetEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.client.render.entity.model.EntityModelPartNames.*;
import static net.minecraft.util.math.MathHelper.cos;

@Environment(EnvType.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class LimpetEntityModel<T extends LimpetEntity> extends SinglePartEntityModel<T> {
    public static final String SHELL = "shell";
    public static final String SHELL_TOP = "shell_top";
    public static final String SHELL_BOTTOM = "shell_bottom";
    public static final String MINERAL = "mineral";
    public static final String MINERAL_ROTATED = "mineral_rotated";

    private final ModelPart root;

    private final ModelPart body;
    private final ModelPart leftEye;
    private final ModelPart rightEye;
    private final ModelPart shell;
    private final ModelPart shellTop;
    private final ModelPart mineral;
    private final ModelPart mineralRotated;
    private final ModelPart shellBottom;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public LimpetEntityModel(ModelPart root) {
        this.root = root;

        this.body = root.getChild(BODY);
        this.leftLeg = root.getChild(LEFT_LEG);
        this.rightLeg = root.getChild(RIGHT_LEG);

        this.leftEye = body.getChild(LEFT_EYE);
        this.rightEye = body.getChild(RIGHT_EYE);
        this.shell = body.getChild(SHELL);

        this.shellTop = shell.getChild(SHELL_TOP);
        this.shellBottom = shell.getChild(SHELL_BOTTOM);

        this.mineral = shellTop.getChild(MINERAL);
        this.mineralRotated = shellTop.getChild(MINERAL_ROTATED);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData body = modelPartData.addChild(
                BODY, ModelPartBuilder.create()
                        .uv(38, 11)
                        .cuboid(-5.0F, -6.0F, -5.0F, 10.0F, 10.0F, 10.0F),
                ModelTransform.pivot(0.0F, 11.0F, 0.0F)
        );

        ModelPartData left_eye = body.addChild(
                LEFT_EYE, ModelPartBuilder.create()
                        .uv(36, 0)
                        .cuboid(-1.5F, -3.0F, -8.0F, 3.0F, 3.0F, 8.0F, new Dilation(0.00F)),
                ModelTransform.pivot(2.5F, 1.0F, -5.0F)
        );

        ModelPartData right_eye = body.addChild(
                RIGHT_EYE, ModelPartBuilder.create()
                        .uv(36, 0)
                        .cuboid(-1.5F, -3.0F, -8.0F, 3.0F, 3.0F, 8.0F, new Dilation(0.00F)),
                ModelTransform.pivot(-2.5F, 1.0F, -5.0F)
        );

        ModelPartData shell = body.addChild(
                SHELL, ModelPartBuilder.create(),
                ModelTransform.pivot(0.0F, 0.0F, 0.0F)
        );

        ModelPartData shell_top = shell.addChild(
                SHELL_TOP, ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-6.0F, -6.0F, -6.0F, 12.0F, 9.0F, 12.0F), 
                ModelTransform.pivot(0.0F, -1.0F, 0.0F)
        );

        ModelPartData cube_r1 = shell_top.addChild(
                MINERAL, ModelPartBuilder.create()
                        .uv(0, 23)
                        .cuboid(0.0F, 12.0F, -8.0F, 0.0F, 4.0F, 16.0F), 
                ModelTransform.of(0.0F, -22.0F, 0.0F, 0.0F, 0.7854F, 0.0F)
        );

        ModelPartData cube_r2 = shell_top.addChild(
                MINERAL_ROTATED, ModelPartBuilder.create()
                        .uv(0, 23)
                        .cuboid(0.0F, 12.0F, -8.0F, 0.0F, 4.0F, 16.0F), 
                ModelTransform.of(0.0F, -22.0F, 0.0F, 0.0F, -0.7854F, 0.0F)
        );

        ModelPartData shell_bottom = shell.addChild(
                SHELL_BOTTOM, ModelPartBuilder.create()
                        .uv(0, 21)
                        .cuboid(-6.0F, -6.0F, -6.0F, 12.0F, 6.0F, 12.0F),
                ModelTransform.pivot(0.0F, 5.0F, 0.0F)
        );

        ModelPartData left_leg = modelPartData.addChild(
                LEFT_LEG, ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-1.5F, -1.0F, -1.0F, 3.0F, 9.0F, 3.0F),
                ModelTransform.pivot(2.5F, 16.0F, 0.0F)
        );

        ModelPartData right_leg = modelPartData.addChild(
                RIGHT_LEG, ModelPartBuilder.create()
                        .uv(0, 0)
                        .mirrored()
                        .cuboid(-1.5F, -1.0F, -1.0F, 3.0F, 9.0F, 3.0F)
                        .mirrored(false),
                ModelTransform.pivot(-2.5F, 16.0F, 0.0F)
        );

        return TexturedModelData.of(modelData, 80, 48);
    }

    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float pi = (float)Math.PI;

        float speed = 1.5f;
        float degree = 1.0f;

        //eye looking direction
        leftEye.pitch = headPitch * pi/180;
        leftEye.yaw = headYaw * pi/180;
        rightEye.pitch = headPitch * pi/180;
        rightEye.yaw = headYaw * pi/180;

        //random eye rotation
        leftEye.roll = MathHelper.sin(animationProgress * speed * 0.05F) * degree * 0.1F;
        leftEye.pitch += MathHelper.cos(animationProgress * speed * 0.025F) * degree * 0.2F;
        rightEye.roll = MathHelper.sin(animationProgress * speed * 0.05F + pi) * degree * 0.1F;
        rightEye.pitch += MathHelper.cos(animationProgress * speed * 0.025F + pi) * degree * 0.2F;

        //retreating in its shell
        if (entity.isScared()) {
            this.rightLeg.visible = false;
            this.leftLeg.visible = false;
            this.root.pivotY = 8.0F;
            this.shell.roll = 0.0F;
        } else {
            shell.roll = MathHelper.sin(limbAngle * speed * 0.6F) * degree * 0.25F * limbDistance;
            this.rightLeg.visible = true;
            this.leftLeg.visible = true;
            this.root.pivotY = 0.0F;
        }

        //walking
        body.pivotY = cos(limbAngle * speed * 0.6F + pi/2) * 1.4F * limbDistance + 11.0F;
        rightLeg.pitch = cos(limbAngle * speed * 0.6F) * 1.4F * limbDistance;
        leftLeg.pitch = cos(limbAngle * speed * 0.6F + pi) * 1.4F * limbDistance;
    }
}
