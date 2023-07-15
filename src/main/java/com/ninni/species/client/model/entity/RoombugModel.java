package com.ninni.species.client.model.entity;

import com.google.common.collect.ImmutableList;
import com.ninni.species.entity.Roombug;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import java.util.Collections;

@Environment(EnvType.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class RoombugModel<E extends Roombug> extends AgeableListModel<E> {
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

    public RoombugModel(ModelPart root) {
        this.root = root;

        this.body = root.getChild(PartNames.BODY);

        this.leftAntenna = body.getChild(LEFT_ANTENNA);
        this.rightAntenna = body.getChild(RIGHT_ANTENNA);
        this.leftForeLeg = body.getChild(LEFT_FORE_LEG);
        this.leftBackLeg = body.getChild(LEFT_BACK_LEG);
        this.leftMidLeg = body.getChild(LEFT_MID_LEG);
        this.rightMidLeg = body.getChild(RIGHT_MID_LEG);
        this.rightForeLeg = body.getChild(RIGHT_FORE_LEG);
        this.rightBackLeg = body.getChild(RIGHT_BACK_LEG);

    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();

        PartDefinition body = root.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-8.0F, -5.0F, -10.0F, 16.0F, 5.0F, 20.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 23.0F, 0.0F)
        );

        PartDefinition leftAntenna = body.addOrReplaceChild(
                LEFT_ANTENNA,
                CubeListBuilder.create()
                        .texOffs(0, 3)
                        .addBox(-1.5F, -7.0F, 0.0F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(3.5F, -5.0F, -8.5F, 0.3927F, 0.0F, 0.0F)
        );

        PartDefinition rightAntenna = body.addOrReplaceChild(
                RIGHT_ANTENNA,
                CubeListBuilder.create()
                        .texOffs(0, 3)
                        .mirror()
                        .addBox(-3.5F, -7.0F, 0.0F, 5.0F, 7.0F, 0.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offsetAndRotation(-3.5F, -5.0F, -8.5F, 0.3927F, 0.0F, 0.0F)
        );

        PartDefinition leftForeLeg = body.addOrReplaceChild(
                LEFT_FORE_LEG,
                CubeListBuilder.create()
                        .texOffs(-2, 0)
                        .addBox(0.0F, 0.0F, -1.5F, 9.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(6.0F, 0.0F, -3.5F, 0.0F, 0.7854F, 0.1309F)
        );

        PartDefinition rightForeLeg = body.addOrReplaceChild(
                RIGHT_FORE_LEG,
                CubeListBuilder.create()
                        .texOffs(-2, 0)
                        .mirror()
                        .addBox(-9.0F, 0.0F, -1.5F, 9.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offsetAndRotation(-6.0F, 0.0F, -3.5F, 0.0F, -0.7854F, -0.1309F)
        );

        PartDefinition leftMidLeg = body.addOrReplaceChild(
                LEFT_MID_LEG,
                CubeListBuilder.create()
                        .texOffs(-2, 0)
                        .addBox(0.0F, 0.0F, -1.5F, 9.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(6.0F, 0.0F, 1.5F, 0.0F, -0.3927F, 0.0873F)
        );

        PartDefinition rightMidLeg = body.addOrReplaceChild(
                RIGHT_MID_LEG,
                CubeListBuilder.create()
                        .texOffs(-2, 0)
                        .mirror()
                        .addBox(-9.0F, 0.0F, -1.5F, 9.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offsetAndRotation(-6.0F, 0.0F, 1.5F, 0.0F, 0.3927F, -0.0873F)
        );

        PartDefinition leftBackLeg = body.addOrReplaceChild(
                LEFT_BACK_LEG,
                CubeListBuilder.create()
                        .texOffs(-2, 0)
                        .addBox(0.0F, 0.0F, -1.5F, 9.0F, 0.0F, 2.0F, new CubeDeformation(0.0F)),
                PartPose.offsetAndRotation(6.0F, 0.0F, 6.5F, 0.0F, -0.7854F, 0.1309F)
        );

        PartDefinition rightBackLeg = body.addOrReplaceChild(
                RIGHT_BACK_LEG,
                CubeListBuilder.create()
                        .texOffs(-2, 0)
                        .mirror()
                        .addBox(-9.0F, 0.0F, -1.5F, 9.0F, 0.0F, 2.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offsetAndRotation(-6.0F, 0.0F, 6.5F, 0.0F, 0.7854F, -0.1309F)
        );

        return LayerDefinition.create(modelData, 80, 32);
    }

    @Override protected Iterable<ModelPart> headParts() { return Collections.emptyList(); }
    @Override protected Iterable<ModelPart> bodyParts() { return ImmutableList.of(this.root); }

    @Override
    public void setupAnim(E entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        limbDistance = Mth.clamp(limbDistance, -0.45F, 0.45F);
        float pi = (float)Math.PI;

        float speed = 1;
        float degree = 1;

        this.body.y = 22.5F;

        leftAntenna.zRot = Mth.sin(animationProgress * speed * 0.2F) * degree * 0.1F;
        leftAntenna.xRot = Mth.cos(animationProgress * speed * 0.1F) * degree * 0.2F + pi/4;
        rightAntenna.zRot = Mth.sin(animationProgress * speed * 0.2F + pi) * degree * 0.1F;
        rightAntenna.xRot = Mth.cos(animationProgress * speed * 0.1F + pi) * degree * 0.2F + pi/4;

        leftForeLeg.zRot = Mth.cos(limbAngle * speed * 0.6F) * degree * 0.25F * limbDistance + 0.1745F;
        leftForeLeg.yRot = Mth.sin(limbAngle * speed * 0.6F) * degree * -0.5F * limbDistance + 0.3927F;
        rightForeLeg.zRot = Mth.cos(limbAngle * speed * 0.6F + (float)Math.PI/2) * degree * 0.25F * limbDistance - 0.1745F;
        rightForeLeg.yRot = Mth.sin(limbAngle * speed * 0.6F + (float)Math.PI/2) * degree * -1F * limbDistance - 0.3927F;

        leftMidLeg.zRot = Mth.sin(limbAngle * speed * 0.6F) * degree * 0.25F * limbDistance + 0.1745F;
        leftMidLeg.yRot = Mth.cos(limbAngle * speed * 0.6F) * degree * 1F * limbDistance - 0.1745F;
        rightMidLeg.zRot = Mth.sin(limbAngle * speed * 0.6F + (float)Math.PI/2) * degree * 0.25F * limbDistance - 0.1745F;
        rightMidLeg.yRot = Mth.cos(limbAngle * speed * 0.6F + (float)Math.PI/2) * degree * 1F * limbDistance + 0.1745F;

        leftBackLeg.zRot = Mth.cos(limbAngle * speed * 0.6F + (float)Math.PI) * degree * 0.25F * limbDistance + 0.2618F;
        leftBackLeg.yRot = Mth.sin(limbAngle * speed * 0.6F + (float)Math.PI) * degree * -1F * limbDistance - 0.7854F;
        rightBackLeg.zRot = Mth.cos(limbAngle * speed * 0.6F + (float)Math.PI/1.5F) * degree * 0.25F * limbDistance - 0.2618F;
        rightBackLeg.yRot = Mth.sin(limbAngle * speed * 0.6F + (float)Math.PI/1.5F) * degree * -1F * limbDistance + 0.7854F;


        if (entity.isInSittingPose()) {
            this.body.y = 23.5F;
            leftForeLeg.zRot = 0;
            leftMidLeg.zRot = 0;
            leftBackLeg.zRot = 0;
            rightForeLeg.zRot = 0;
            rightMidLeg.zRot = 0;
            rightBackLeg.zRot = 0;
        }
    }
}
