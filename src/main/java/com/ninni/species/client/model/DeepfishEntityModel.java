package com.ninni.species.client.model;

import com.google.common.collect.ImmutableList;
import com.ninni.species.entity.DeepfishEntity;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;

import java.util.Collections;
import java.util.List;

import static net.minecraft.client.render.entity.model.EntityModelPartNames.*;
import static net.minecraft.util.math.MathHelper.cos;

public class DeepfishEntityModel<T extends DeepfishEntity> extends SinglePartEntityModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart tail;

    public DeepfishEntityModel(ModelPart root) {
        this.root = root;

        this.body = root.getChild("body");

        this.leftEar = this.body.getChild(LEFT_EAR);
        this.rightEar  = this.body.getChild(RIGHT_EAR);
        this.tail = this.body.getChild(TAIL);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData root = modelData.getRoot();

        ModelPartData body = root.addChild(
                BODY,
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new Dilation(0.0F))
                        .uv(0, 16)
                        .cuboid(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 20.0F, 0.0F)
        );

        ModelPartData leftEar = body.addChild(
                LEFT_EAR,
                ModelPartBuilder.create()
                        .uv(0, 28)
                        .cuboid(0.0F, -2.5F, -2.5F, 2.0F, 5.0F, 5.0F, new Dilation(0.0F)),
                ModelTransform.pivot(4.0F, 0.5F, -0.5F)
        );

        ModelPartData rightEar = body.addChild(
                RIGHT_EAR,
                ModelPartBuilder.create()
                        .uv(0, 28)
                        .mirrored()
                        .cuboid(-2.0F, -2.5F, -2.5F, 2.0F, 5.0F, 5.0F, new Dilation(0.0F))
                        .mirrored(false),
                ModelTransform.pivot(-4.0F, 0.5F, -0.5F)
        );

        ModelPartData tail = body.addChild(
                TAIL,
                ModelPartBuilder.create()
                        .uv(18, 8)
                        .cuboid(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 8.0F, new Dilation(0.0F)),
                ModelTransform.pivot(0.0F, 0.0F, 4.0F)
        );

        return TexturedModelData.of(modelData, 48, 48);
    }


    @Override
    public ModelPart getPart() {
        return this.root;
    }

    public List<ModelPart> getAllParts() {
        return ImmutableList.of(this.body, this.tail, this.leftEar, this.rightEar);
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        float speed = 1.5f;
        float degree = 1.0f;
        this.body.pitch = headPitch * ((float)Math.PI / 180);
        this.body.yaw = headYaw * ((float)Math.PI / 180);

        this.body.yaw += cos(animationProgress * speed * 0.15F) * degree * 0.25F;
        this.body.pivotY = cos(animationProgress * speed * 0.15F + (float)Math.PI / 2) * degree * 0.25F + 20.0F;
        this.tail.yaw = cos(limbAngle * speed * 0.6F + 0.5F) * degree * 3F * limbDistance;

    }
}
