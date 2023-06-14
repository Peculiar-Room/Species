package com.ninni.species.client.model.entity;

import com.google.common.collect.ImmutableList;
import com.ninni.species.entity.WraptorEntity;
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
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Collections;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class WraptorEntityModel<E extends WraptorEntity> extends AgeableListModel<E> {
    public static final String TAIL_BASE = "tail_base";
    public static final String TAIL_TIP = "tail_tip";
    public static final String BODY_FEATHERS = "body_feathers";
    public static final String NECK_FEATHERS = "neck_feathers";
    public static final String NECK_BASE_FEATHERS = "neck_base_feathers";
    public static final String HEAD_FEATHERS = "head_feathers";
    public static final String TAIL_FEATHERS = "tail_feathers";
    public static final String FEATHER_TUFT = "feather_tuft";
    public static final String TUFT = "tuft";
    public static final String ROTATED_TUFT = "rotated_tuft";

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart bodyFeathers;
    private final ModelPart leftWing;
    private final ModelPart rightWing;
    private final ModelPart tailBase;
    private final ModelPart tail;
    private final ModelPart tailTip;
    private final ModelPart tailFeathers;
    private final ModelPart neck;
    private final ModelPart neckFeathers;
    private final ModelPart neckBaseFeathers;
    private final ModelPart head;
    private final ModelPart headFeathers;
    private final ModelPart featherTuft;
    private final ModelPart rotatedTuft;
    private final ModelPart tuft;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public WraptorEntityModel(ModelPart root) {
        this.root = root;

        this.body             = this.root.getChild(PartNames.BODY);
        this.rightLeg         = this.root.getChild(PartNames.RIGHT_LEG);
        this.leftLeg          = this.root.getChild(PartNames.LEFT_LEG);

        this.rightWing        = this.body.getChild(PartNames.RIGHT_WING);
        this.leftWing         = this.body.getChild(PartNames.LEFT_WING);
        this.neck             = this.body.getChild(PartNames.NECK);
        this.tailBase         = this.body.getChild(TAIL_BASE);
        this.bodyFeathers     = this.body.getChild(BODY_FEATHERS);

        this.neckFeathers     = this.neck.getChild(NECK_FEATHERS);
        this.neckBaseFeathers = this.neck.getChild(NECK_BASE_FEATHERS);
        this.head             = this.neck.getChild(PartNames.HEAD);

        this.headFeathers     = this.head.getChild(HEAD_FEATHERS);
        this.featherTuft      = this.head.getChild(FEATHER_TUFT);

        this.tuft             = this.featherTuft.getChild(TUFT);
        this.rotatedTuft      = this.featherTuft.getChild(ROTATED_TUFT);

        this.tail             = this.tailBase.getChild(PartNames.TAIL);

        this.tailTip          = this.tail.getChild(TAIL_TIP);

        this.tailFeathers     = this.tailTip.getChild(TAIL_FEATHERS);
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition data = new MeshDefinition();
        PartDefinition root = data.getRoot();

        PartDefinition body = root.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 12.0F),
                PartPose.offsetAndRotation(0.0F, 10.0F, 0.0F, 0.0F, 0.0F, 0.0F)
        );

        PartDefinition bodyFeathers = body.addOrReplaceChild(
                BODY_FEATHERS,
                CubeListBuilder.create()
                        .texOffs(52, 32)
                        .addBox(-4.0F, -5.0F, 0.5F, 8.0F, 9.0F, 12.0F, new CubeDeformation(1.25F)),
                PartPose.offsetAndRotation(0.0F, 1.0F, -6.5F, 0.0F, 0.0F, 0.0F)
        );

        PartDefinition leftWing = body.addOrReplaceChild(
                PartNames.LEFT_WING,
                CubeListBuilder.create()
                        .texOffs(-12, 45)
                        .mirror(false)
                        .addBox(0.0F, 0.0F, -1.5F, 19.0F, 0.0F, 12.0F),
                PartPose.offsetAndRotation(4.0F, -2.0F, -3.5F, 0.0F, 0.0F, 0.0F)
        );

        PartDefinition rightWing = body.addOrReplaceChild(
                PartNames.RIGHT_WING,
                CubeListBuilder.create()
                        .texOffs(-12, 45)
                        .mirror(true)
                        .addBox(-19.0F, 0.0F, -1.5F, 19.0F, 0.0F, 12.0F),
                PartPose.offsetAndRotation(-4.0F, -2.0F, -3.5F, 0.0F, 0.0F, 0.0F)
        );

        PartDefinition tailBase = body.addOrReplaceChild(
                TAIL_BASE,
                CubeListBuilder.create()
                        .texOffs(0, 20)
                        .addBox(-2.5F, -1.5F, 0.0F, 5.0F, 5.0F, 11.0F),
                PartPose.offsetAndRotation(0.0F, -2.5F, 6.0F, 0.0F, 0.0F, 0.0F)
        );

        PartDefinition tail = tailBase.addOrReplaceChild(
                PartNames.TAIL,
                CubeListBuilder.create()
                        .texOffs(32, 24)
                        .addBox(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -1.5F, 9.0F, 0.0F, 0.0F, 0.0F)
        );

        PartDefinition tailTip = tail.addOrReplaceChild(
                TAIL_TIP,
                CubeListBuilder.create()
                        .texOffs(0, 36)
                        .addBox(-1.5F, -1.5F, -6.0F, 3.0F, 3.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, -6.5F, -2.0F, 0.0F, 0.0F, 0.0F)
        );

        PartDefinition tailFeathers = tailTip.addOrReplaceChild(
                TAIL_FEATHERS,
                CubeListBuilder.create()
                        .texOffs(18, 36)
                        .addBox(-5.5F, -9.0F, 0.0F, 11.0F, 9.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, -1.5F, -6.0F, -0.7854F, 0.0F, 0.0F)
        );

        PartDefinition neck = body.addOrReplaceChild(
                PartNames.NECK,
                CubeListBuilder.create()
                        .texOffs(48, 24)
                        .addBox(-2.0F, -14.0F, -2.0F, 4.0F, 16.0F, 4.0F),
                PartPose.offsetAndRotation(0.0F, -1.0F, -6.0F, 0.0F, 0.0F, 0.0F)
        );

        PartDefinition neckFeathers = neck.addOrReplaceChild(
                NECK_FEATHERS,
                CubeListBuilder.create()
                        .texOffs(62, 9)
                        .addBox(-2.0F, 2.0F, -1.5F, 4.0F, 3.0F, 4.0F, new CubeDeformation(0.5F)),
                PartPose.offsetAndRotation(0.0F, -14.0F, -0.5F, 0.0F, 0.0F, 0.0F)
        );

        PartDefinition neckBaseFeathers = neck.addOrReplaceChild(
                NECK_BASE_FEATHERS,
                CubeListBuilder.create()
                        .texOffs(62, 0)
                        .addBox(-2.0F, 2.0F, -1.5F, 4.0F, 5.0F, 4.0F, new CubeDeformation(0.5F)),
                PartPose.offsetAndRotation(0.0F, -9.0F, -0.5F, 0.0F, 0.0F, 0.0F)
        );

        PartDefinition head = neck.addOrReplaceChild(
                PartNames.HEAD,
                CubeListBuilder.create()
                        .texOffs(28, 0)
                        .addBox(-3.0F, -5.02F, -3.0F, 6.0F, 5.0F, 6.0F)
                        .texOffs(30, 11)
                        .addBox(-3.5F, -3.0F, -8.0F, 7.0F, 3.0F, 10.0F),
                PartPose.offsetAndRotation(0.0F, -14.0F, -0.5F, 0.0F, 0.0F, 0.0F)
        );

        PartDefinition featherTuft = head.addOrReplaceChild(
                FEATHER_TUFT,
                CubeListBuilder.create(),
                PartPose.offsetAndRotation(0.0F, -7.5F, 0.0F, 0.0F, 0.0F, 0.0F)
        );

        PartDefinition rotatedTuft = featherTuft.addOrReplaceChild(
                ROTATED_TUFT,
                CubeListBuilder.create()
                        .texOffs(54, 16)
                        .addBox(-3.5F, -2.5F, 0.0F, 7.0F, 5.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F)
        );

        PartDefinition tuft = featherTuft.addOrReplaceChild(
                TUFT,
                CubeListBuilder.create()
                        .texOffs(54, 16)
                        .addBox(-3.5F, -2.5F, 0.0F, 7.0F, 5.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F)
        );

        PartDefinition headFeathers = head.addOrReplaceChild(
                HEAD_FEATHERS,
                CubeListBuilder.create()
                        .texOffs(72, 10)
                        .addBox(-3.0F, -5.0F, -3.0F, 6.0F, 7.0F, 6.0F, new CubeDeformation(0.75F)),
                PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F)
        );

        PartDefinition rightLeg = root.addOrReplaceChild(
                PartNames.RIGHT_LEG,
                CubeListBuilder.create()
                        .texOffs(-6, 0)
                        .mirror(false)
                        .addBox(-3.0F, 12.98F, -5.0F, 6.0F, 0.0F, 6.0F)
                        .texOffs(54, 0)
                        .mirror(true)
                        .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 14.0F, 2.0F),
                PartPose.offsetAndRotation(-4.0F, 11.0F, 0.0F, 0.0F, 0.0F, 0.0F)
        );

        PartDefinition leftLeg = root.addOrReplaceChild(
                PartNames.LEFT_LEG,
                CubeListBuilder.create()
                        .texOffs(-6, 0)
                        .mirror(false)
                        .addBox(-3.0F, 12.98F, -5.0F, 6.0F, 0.0F, 6.0F)
                        .texOffs(54, 0)
                        .mirror(false)
                        .addBox(-1.0F, -1.0F, -1.0F, 2.0F, 14.0F, 2.0F),
                PartPose.offsetAndRotation(4.0F, 11.0F, 0.0F, 0.0F, 0.0F, 0.0F)
        );

        return LayerDefinition.create(data, 96, 64);
    }

    @Override protected Iterable<ModelPart> headParts() { return Collections.emptyList(); }
    @Override protected Iterable<ModelPart> bodyParts() { return ImmutableList.of(this.body, this.leftLeg, this.rightLeg); }

    @Override
    public void setupAnim(E entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        limbDistance = Mth.clamp(limbDistance, -0.45F, 0.45F);



        int stage = entity.getFeatherStage();
        float speed = 1.5f;
        float degree = 1.0f;

        this.head.xRot = headPitch * ((float) Math.PI / 180f) - (headPitch * ((float) Math.PI / 180f)) / 2;
        this.head.yRot = headYaw * ((float) Math.PI / 180f) - (headYaw * ((float) Math.PI / 180f)) / 2;
        this.neck.xRot = (headPitch * ((float) Math.PI / 180f)) / 2;
        this.neck.yRot = (headYaw * ((float) Math.PI / 180f)) / 2;

        this.rightLeg.xRot = Mth.cos(limbAngle * speed * 0.6F) * 1.4F * limbDistance;
        this.leftLeg.xRot = Mth.cos(limbAngle * speed * 0.6F + (float)Math.PI) * 1.4F * limbDistance;

        this.neck.xRot += Mth.cos(limbAngle * speed * 0.45F + (float)Math.PI / 2) * 0.6F * limbDistance;
        this.head.xRot += Mth.cos(limbAngle * speed * 0.45F + (float)Math.PI + (float)Math.PI / 2) * 0.6F * limbDistance;

        this.body.y = Mth.cos(limbAngle * speed * 1.2F + (float)Math.PI / 2) * 2F * limbDistance + 10.0F;

        this.tailBase.xRot = Mth.cos(limbAngle * speed * 1.2F) * 0.4F * limbDistance;
        this.tail.xRot = Mth.cos(limbAngle * speed * 1.2F + (float)Math.PI) * 0.3F * limbDistance;
        this.tailTip.xRot = Mth.cos(limbAngle * speed * 1.2F + (float)Math.PI / 2) * 0.4F * limbDistance;
        this.tailFeathers.xRot = Mth.cos(limbAngle * speed * 1.2F + (float)Math.PI / 2) * 0.4F * limbDistance -0.7854F;

        if (stage < 4) {
            this.headFeathers.visible = false;
            this.featherTuft.visible = false;
        } else {
            this.headFeathers.visible = true;
            this.featherTuft.visible = true;
        }
        this.neckFeathers.visible = stage >= 3;
        this.neckBaseFeathers.visible = stage >= 2;
        if (stage < 1 || entity.isBaby()) {
            this.bodyFeathers.visible = false;
            this.leftWing.visible = true;
            this.rightWing.visible = true;
            if (entity.onGround()) {
                this.leftWing.zRot = 0.8F;
                this.leftWing.yRot = Mth.cos(limbAngle * speed * 0.45F) * 0.5F * limbDistance - 0.25F;
                this.rightWing.zRot = -0.8F;
                this.rightWing.yRot = Mth.cos(limbAngle * speed * 0.45F + (float)Math.PI) * 0.5F * limbDistance + 0.25F;
            } else {
                this.leftWing.zRot = Mth.cos(animationProgress * speed * 0.3F) * degree * 4F * 0.25F;
                this.leftWing.yRot = Mth.cos(animationProgress * speed * 0.3F + (float) Math.PI / 2) * degree * 0.5F * 0.25F - 0.15F;
                this.rightWing.zRot = Mth.cos(animationProgress * speed * 0.3F + (float) Math.PI) * degree * 4F * 0.25F;
                this.rightWing.yRot = Mth.cos(animationProgress * speed * 0.3F + (float) Math.PI + (float) Math.PI / 2) * degree * 0.5F * 0.25F + 0.15F;
            }
        } else {
            this.bodyFeathers.visible = true;
            this.leftWing.visible = false;
            this.rightWing.visible = false;
            this.leftWing.zRot = 0;
            this.leftWing.yRot = 0;
            this.rightWing.zRot = 0;
            this.rightWing.yRot = 0;
        }

        if (this.young) {
            this.neckBaseFeathers.visible = false;
            this.neckFeathers.visible = false;
            this.headFeathers.visible = false;
        }
    }
}
