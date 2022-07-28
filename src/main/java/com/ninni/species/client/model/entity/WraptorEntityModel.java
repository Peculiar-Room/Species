package com.ninni.species.client.model.entity;

import com.google.common.collect.ImmutableList;
import com.ninni.species.entity.WraptorEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.Dilation;
import net.minecraft.client.model.ModelData;
import net.minecraft.client.model.ModelPart;
import net.minecraft.client.model.ModelPartBuilder;
import net.minecraft.client.model.ModelPartData;
import net.minecraft.client.model.ModelTransform;
import net.minecraft.client.model.TexturedModelData;
import net.minecraft.client.render.entity.model.AnimalModel;

import java.util.Collections;

import static net.minecraft.client.render.entity.model.EntityModelPartNames.*;
import static net.minecraft.util.math.MathHelper.*;

@Environment(EnvType.CLIENT)
public class WraptorEntityModel<E extends WraptorEntity> extends AnimalModel<E> {
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

        this.body             = this.root.getChild(BODY);
        this.rightLeg         = this.root.getChild(RIGHT_LEG);
        this.leftLeg          = this.root.getChild(LEFT_LEG);

        this.rightWing        = this.body.getChild(RIGHT_WING);
        this.leftWing         = this.body.getChild(LEFT_WING);
        this.neck             = this.body.getChild(NECK);
        this.tailBase         = this.body.getChild(TAIL_BASE);
        this.bodyFeathers     = this.body.getChild(BODY_FEATHERS);

        this.neckFeathers     = this.neck.getChild(NECK_FEATHERS);
        this.neckBaseFeathers = this.neck.getChild(NECK_BASE_FEATHERS);
        this.head             = this.neck.getChild(HEAD);

        this.headFeathers     = this.head.getChild(HEAD_FEATHERS);
        this.featherTuft      = this.head.getChild(FEATHER_TUFT);

        this.tuft             = this.featherTuft.getChild(TUFT);
        this.rotatedTuft      = this.featherTuft.getChild(ROTATED_TUFT);

        this.tail             = this.tailBase.getChild(TAIL);

        this.tailTip          = this.tail.getChild(TAIL_TIP);

        this.tailFeathers     = this.tailTip.getChild(TAIL_FEATHERS);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData data = new ModelData();
        ModelPartData root = data.getRoot();

        ModelPartData body = root.addChild(
            BODY,
            ModelPartBuilder.create()
                            .uv(0, 0)
                            .cuboid(-4.0F, -4.0F, -6.0F, 8.0F, 8.0F, 12.0F),
            ModelTransform.of(0.0F, 10.0F, 0.0F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData bodyFeathers = body.addChild(
            BODY_FEATHERS,
            ModelPartBuilder.create()
                            .uv(52, 32)
                            .cuboid(-4.0F, -5.0F, 0.5F, 8.0F, 9.0F, 12.0F, new Dilation(1.25F)),
            ModelTransform.of(0.0F, 1.0F, -6.5F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData leftWing = body.addChild(
            LEFT_WING,
            ModelPartBuilder.create()
                            .uv(-12, 45)
                            .mirrored(false)
                            .cuboid(0.0F, 0.0F, -1.5F, 19.0F, 0.0F, 12.0F),
            ModelTransform.of(4.0F, -2.0F, -3.5F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData rightWing = body.addChild(
            RIGHT_WING,
            ModelPartBuilder.create()
                            .uv(-12, 45)
                            .mirrored(true)
                            .cuboid(-19.0F, 0.0F, -1.5F, 19.0F, 0.0F, 12.0F),
            ModelTransform.of(-4.0F, -2.0F, -3.5F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData tailBase = body.addChild(
            TAIL_BASE,
            ModelPartBuilder.create()
                            .uv(0, 20)
                            .cuboid(-2.5F, -1.5F, 0.0F, 5.0F, 5.0F, 11.0F),
            ModelTransform.of(0.0F, -2.5F, 6.0F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData tail = tailBase.addChild(
            TAIL,
            ModelPartBuilder.create()
                            .uv(32, 24)
                            .cuboid(-2.0F, -8.0F, -2.0F, 4.0F, 8.0F, 4.0F),
            ModelTransform.of(0.0F, -1.5F, 9.0F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData tailTip = tail.addChild(
            TAIL_TIP,
            ModelPartBuilder.create()
                            .uv(0, 36)
                            .cuboid(-1.5F, -1.5F, -6.0F, 3.0F, 3.0F, 6.0F),
            ModelTransform.of(0.0F, -6.5F, -2.0F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData tailFeathers = tailTip.addChild(
            TAIL_FEATHERS,
            ModelPartBuilder.create()
                            .uv(18, 36)
                            .cuboid(-5.5F, -9.0F, 0.0F, 11.0F, 9.0F, 0.0F),
            ModelTransform.of(0.0F, -1.5F, -6.0F, -0.7854F, 0.0F, 0.0F)
        );

        ModelPartData neck = body.addChild(
            NECK,
            ModelPartBuilder.create()
                            .uv(48, 24)
                            .cuboid(-2.0F, -14.0F, -2.0F, 4.0F, 16.0F, 4.0F),
            ModelTransform.of(0.0F, -1.0F, -6.0F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData neckFeathers = neck.addChild(
            NECK_FEATHERS,
            ModelPartBuilder.create()
                            .uv(62, 9)
                            .cuboid(-2.0F, 2.0F, -1.5F, 4.0F, 3.0F, 4.0F, new Dilation(0.5F)),
            ModelTransform.of(0.0F, -14.0F, -0.5F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData neckBaseFeathers = neck.addChild(
            NECK_BASE_FEATHERS,
            ModelPartBuilder.create()
                            .uv(62, 0)
                            .cuboid(-2.0F, 2.0F, -1.5F, 4.0F, 5.0F, 4.0F, new Dilation(0.5F)),
            ModelTransform.of(0.0F, -9.0F, -0.5F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData head = neck.addChild(
            HEAD,
            ModelPartBuilder.create()
                            .uv(28, 0)
                            .cuboid(-3.0F, -5.02F, -3.0F, 6.0F, 5.0F, 6.0F)
                            .uv(30, 11)
                            .cuboid(-3.5F, -3.0F, -8.0F, 7.0F, 3.0F, 10.0F),
            ModelTransform.of(0.0F, -14.0F, -0.5F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData featherTuft = head.addChild(
            FEATHER_TUFT,
            ModelPartBuilder.create(),
            ModelTransform.of(0.0F, -7.5F, 0.0F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData rotatedTuft = featherTuft.addChild(
            ROTATED_TUFT,
            ModelPartBuilder.create()
                            .uv(54, 16)
                            .cuboid(-3.5F, -2.5F, 0.0F, 7.0F, 5.0F, 0.0F),
            ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, -0.7854F, 0.0F)
        );

        ModelPartData tuft = featherTuft.addChild(
            TUFT,
            ModelPartBuilder.create()
                            .uv(54, 16)
                            .cuboid(-3.5F, -2.5F, 0.0F, 7.0F, 5.0F, 0.0F),
            ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.7854F, 0.0F)
        );

        ModelPartData headFeathers = head.addChild(
            HEAD_FEATHERS,
            ModelPartBuilder.create()
                            .uv(72, 10)
                            .cuboid(-3.0F, -5.0F, -3.0F, 6.0F, 7.0F, 6.0F, new Dilation(0.75F)),
            ModelTransform.of(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData rightLeg = root.addChild(
            RIGHT_LEG,
            ModelPartBuilder.create()
                            .uv(-6, 0)
                            .mirrored(false)
                            .cuboid(-3.0F, 12.98F, -5.0F, 6.0F, 0.0F, 6.0F)
                            .uv(54, 0)
                            .mirrored(true)
                            .cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 14.0F, 2.0F),
            ModelTransform.of(-4.0F, 11.0F, 0.0F, 0.0F, 0.0F, 0.0F)
        );

        ModelPartData leftLeg = root.addChild(
            LEFT_LEG,
            ModelPartBuilder.create()
                            .uv(-6, 0)
                            .mirrored(false)
                            .cuboid(-3.0F, 12.98F, -5.0F, 6.0F, 0.0F, 6.0F)
                            .uv(54, 0)
                            .mirrored(false)
                            .cuboid(-1.0F, -1.0F, -1.0F, 2.0F, 14.0F, 2.0F),
            ModelTransform.of(4.0F, 11.0F, 0.0F, 0.0F, 0.0F, 0.0F)
        );

        return TexturedModelData.of(data, 96, 64);
    }

    @Override protected Iterable<ModelPart> getHeadParts() { return Collections.emptyList(); }
    @Override protected Iterable<ModelPart> getBodyParts() { return ImmutableList.of(this.body, this.leftLeg, this.rightLeg); }

    @Override
    public void setAngles(E entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        limbDistance = clamp(limbDistance, -0.45F, 0.45F);



        int stage = entity.getFeatherStage();
        float speed = 1.5f;
        float degree = 1.0f;

        this.head.pitch = headPitch * ((float) Math.PI / 180f) - (headPitch * ((float) Math.PI / 180f)) / 2;
        this.head.yaw = headYaw * ((float) Math.PI / 180f) - (headYaw * ((float) Math.PI / 180f)) / 2;
        this.neck.pitch = (headPitch * ((float) Math.PI / 180f)) / 2;
        this.neck.yaw = (headYaw * ((float) Math.PI / 180f)) / 2;

        this.rightLeg.pitch = cos(limbAngle * speed * 0.6F) * 1.4F * limbDistance;
        this.leftLeg.pitch = cos(limbAngle * speed * 0.6F + (float)Math.PI) * 1.4F * limbDistance;

        this.neck.pitch += cos(limbAngle * speed * 0.45F + (float)Math.PI / 2) * 0.6F * limbDistance;
        this.head.pitch += cos(limbAngle * speed * 0.45F + (float)Math.PI + (float)Math.PI / 2) * 0.6F * limbDistance;

        this.body.pivotY = cos(limbAngle * speed * 1.2F + (float)Math.PI / 2) * 2F * limbDistance + 10.0F;

        this.tailBase.pitch = cos(limbAngle * speed * 1.2F) * 0.4F * limbDistance;
        this.tail.pitch = cos(limbAngle * speed * 1.2F + (float)Math.PI) * 0.3F * limbDistance;
        this.tailTip.pitch = cos(limbAngle * speed * 1.2F + (float)Math.PI / 2) * 0.4F * limbDistance;
        this.tailFeathers.pitch = cos(limbAngle * speed * 1.2F + (float)Math.PI / 2) * 0.4F * limbDistance -0.7854F;

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
            if (entity.isOnGround()) {
                this.leftWing.roll = 0.8F;
                this.leftWing.yaw = cos(limbAngle * speed * 0.45F) * 0.5F * limbDistance - 0.25F;
                this.rightWing.roll = -0.8F;
                this.rightWing.yaw = cos(limbAngle * speed * 0.45F + (float)Math.PI) * 0.5F * limbDistance + 0.25F;
            } else {
                this.leftWing.roll = cos(animationProgress * speed * 0.3F) * degree * 4F * 0.25F;
                this.leftWing.yaw = cos(animationProgress * speed * 0.3F + (float) Math.PI / 2) * degree * 0.5F * 0.25F - 0.15F;
                this.rightWing.roll = cos(animationProgress * speed * 0.3F + (float) Math.PI) * degree * 4F * 0.25F;
                this.rightWing.yaw = cos(animationProgress * speed * 0.3F + (float) Math.PI + (float) Math.PI / 2) * degree * 0.5F * 0.25F + 0.15F;
            }
        } else {
            this.bodyFeathers.visible = true;
            this.leftWing.visible = false;
            this.rightWing.visible = false;
            this.leftWing.roll = 0;
            this.leftWing.yaw = 0;
            this.rightWing.roll = 0;
            this.rightWing.yaw = 0;
        }

        if (this.child) {
            this.neckBaseFeathers.visible = false;
            this.neckFeathers.visible = false;
            this.headFeathers.visible = false;
        }
    }
}
