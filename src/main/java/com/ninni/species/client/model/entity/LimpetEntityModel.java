package com.ninni.species.client.model.entity;

import com.ninni.species.entity.LimpetEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class LimpetEntityModel<T extends LimpetEntity> extends HierarchicalModel<T> {
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

        this.body = root.getChild(PartNames.BODY);
        this.leftLeg = root.getChild(PartNames.LEFT_LEG);
        this.rightLeg = root.getChild(PartNames.RIGHT_LEG);

        this.leftEye = body.getChild(PartNames.LEFT_EYE);
        this.rightEye = body.getChild(PartNames.RIGHT_EYE);
        this.shell = body.getChild(SHELL);

        this.shellTop = shell.getChild(SHELL_TOP);
        this.shellBottom = shell.getChild(SHELL_BOTTOM);

        this.mineral = shellTop.getChild(MINERAL);
        this.mineralRotated = shellTop.getChild(MINERAL_ROTATED);
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        PartDefinition body = modelPartData.addOrReplaceChild(
                PartNames.BODY, CubeListBuilder.create()
                        .texOffs(38, 11)
                        .addBox(-5.0F, -6.0F, -5.0F, 10.0F, 10.0F, 10.0F),
                PartPose.offset(0.0F, 11.0F, 0.0F)
        );

        PartDefinition left_eye = body.addOrReplaceChild(
                PartNames.LEFT_EYE, CubeListBuilder.create()
                        .texOffs(36, 0)
                        .addBox(-1.5F, -3.0F, -8.0F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.00F)),
                PartPose.offset(2.5F, 1.0F, -5.0F)
        );

        PartDefinition right_eye = body.addOrReplaceChild(
                PartNames.RIGHT_EYE, CubeListBuilder.create()
                        .texOffs(36, 0)
                        .addBox(-1.5F, -3.0F, -8.0F, 3.0F, 3.0F, 8.0F, new CubeDeformation(0.00F)),
                PartPose.offset(-2.5F, 1.0F, -5.0F)
        );

        PartDefinition shell = body.addOrReplaceChild(
                SHELL, CubeListBuilder.create(),
                PartPose.offset(0.0F, 0.0F, 0.0F)
        );

        PartDefinition shell_top = shell.addOrReplaceChild(
                SHELL_TOP, CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-6.0F, -6.0F, -6.0F, 12.0F, 9.0F, 12.0F),
                PartPose.offset(0.0F, -1.0F, 0.0F)
        );

        PartDefinition cube_r1 = shell_top.addOrReplaceChild(
                MINERAL, CubeListBuilder.create()
                        .texOffs(0, 23)
                        .addBox(0.0F, 12.0F, -8.0F, 0.0F, 4.0F, 16.0F),
                PartPose.offsetAndRotation(0.0F, -22.0F, 0.0F, 0.0F, 0.7854F, 0.0F)
        );

        PartDefinition cube_r2 = shell_top.addOrReplaceChild(
                MINERAL_ROTATED, CubeListBuilder.create()
                        .texOffs(0, 23)
                        .addBox(0.0F, 12.0F, -8.0F, 0.0F, 4.0F, 16.0F),
                PartPose.offsetAndRotation(0.0F, -22.0F, 0.0F, 0.0F, -0.7854F, 0.0F)
        );

        PartDefinition shell_bottom = shell.addOrReplaceChild(
                SHELL_BOTTOM, CubeListBuilder.create()
                        .texOffs(0, 21)
                        .addBox(-6.0F, -6.0F, -6.0F, 12.0F, 6.0F, 12.0F),
                PartPose.offset(0.0F, 5.0F, 0.0F)
        );

        PartDefinition left_leg = modelPartData.addOrReplaceChild(
                PartNames.LEFT_LEG, CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-1.5F, -1.0F, -1.0F, 3.0F, 9.0F, 3.0F),
                PartPose.offset(2.5F, 16.0F, 0.0F)
        );

        PartDefinition right_leg = modelPartData.addOrReplaceChild(
                PartNames.RIGHT_LEG, CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror()
                        .addBox(-1.5F, -1.0F, -1.0F, 3.0F, 9.0F, 3.0F)
                        .mirror(false),
                PartPose.offset(-2.5F, 16.0F, 0.0F)
        );

        return LayerDefinition.create(modelData, 80, 48);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float pi = (float)Math.PI;

        float speed = 1.5f;
        float degree = 1.0f;

        //eye looking direction
        leftEye.xRot = headPitch * pi/180;
        leftEye.yRot = headYaw * pi/180;
        rightEye.xRot = headPitch * pi/180;
        rightEye.yRot = headYaw * pi/180;

        //random eye rotation
        leftEye.zRot = Mth.sin(animationProgress * speed * 0.05F) * degree * 0.1F;
        leftEye.xRot += Mth.cos(animationProgress * speed * 0.025F) * degree * 0.2F;
        rightEye.zRot = Mth.sin(animationProgress * speed * 0.05F + pi) * degree * 0.1F;
        rightEye.xRot += Mth.cos(animationProgress * speed * 0.025F + pi) * degree * 0.2F;

        //retreating in its shell
        if (entity.isScared()) {
            this.rightLeg.visible = false;
            this.leftLeg.visible = false;
            this.root.y = 8.0F;
            this.shell.zRot = 0.0F;
        } else {
            shell.zRot = Mth.sin(limbAngle * speed * 0.6F) * degree * 0.25F * limbDistance;
            this.rightLeg.visible = true;
            this.leftLeg.visible = true;
            this.root.y = 0.0F;
        }

        shell.visible = entity.getLimpetType().getId() != 0;

        //walking
        body.y = Mth.cos(limbAngle * speed * 0.6F + pi/2) * degree * limbDistance + 11.0F;
        rightLeg.xRot = Mth.cos(limbAngle * speed * 0.6F) * degree * 1.4F * limbDistance;
        leftLeg.xRot = Mth.cos(limbAngle * speed * 0.6F + pi) * degree * 1.4F * limbDistance;
    }
}
