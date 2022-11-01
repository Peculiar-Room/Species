package com.ninni.species.client.model.entity;

import com.ninni.species.client.animation.BirtAnimations;
import com.ninni.species.entity.BirtEntity;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.*;
import net.minecraft.client.render.entity.model.SinglePartEntityModel;
import net.minecraft.util.math.MathHelper;

import static net.minecraft.client.render.entity.model.EntityModelPartNames.*;

@Environment(EnvType.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class BirtEntityModel<T extends BirtEntity> extends SinglePartEntityModel<T> {
    public static final String ANTENNA = "antenna";

    private final ModelPart root;

    private final ModelPart body;
    private final ModelPart antenna;
    private final ModelPart tail;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public BirtEntityModel(ModelPart root) {
        this.root = root;

        this.body = root.getChild(BODY);

        this.antenna = this.body.getChild(ANTENNA);
        this.tail = this.body.getChild(TAIL);
        this.leftLeg = this.body.getChild(LEFT_LEG);
        this.rightLeg = this.body.getChild(RIGHT_LEG);
        this.leftWing = this.body.getChild(LEFT_WING);
        this.rightWing = this.body.getChild(RIGHT_WING);
    }

    public static TexturedModelData getTexturedModelData() {
        ModelData modelData = new ModelData();
        ModelPartData modelPartData = modelData.getRoot();

        ModelPartData body = modelPartData.addChild(
                BODY,
                ModelPartBuilder.create()
                        .uv(0, 0)
                        .cuboid(-3.5F, -5.0F, -3.5F, 7.0F, 7.0F, 7.0F)
                        .uv(18, 17)
                        .cuboid(-2.5F, -1.0F, -5.5F, 5.0F, 2.0F, 2.0F),
                ModelTransform.pivot(0.0F, 20.0F, 0.0F)
        );

        ModelPartData antenna = body.addChild(
                ANTENNA,
                ModelPartBuilder.create()
                        .uv(0, 1)
                        .cuboid(-0.5F, -4.0F, -0.5F, 1.0F, 5.0F, 1.0F),
                ModelTransform.pivot(0.0F, -5.0F, 0.0F)
        );

        ModelPartData tail = body.addChild(
                TAIL, 
                ModelPartBuilder.create()
                        .uv(0, 24)
                        .cuboid(-1.5F, 0.0F, 0.0F, 3.0F, 1.0F, 3.0F),
                ModelTransform.pivot(0.0F, 0.0F, 3.5F)
        );

        ModelPartData leftLeg = body.addChild(
                LEFT_LEG,
                ModelPartBuilder.create()
                        .uv(8, 14)
                        .cuboid(-1.5F, 0.0F, -2.0F, 3.0F, 2.0F, 2.0F),
                ModelTransform.pivot(2.0F, 2.0F, 0.5F)
        );

        ModelPartData rightLeg = body.addChild(
                RIGHT_LEG,
                ModelPartBuilder.create()
                        .uv(8, 14)
                        .cuboid(-1.5F, 0.0F, -2.0F, 3.0F, 2.0F, 2.0F),
                ModelTransform.pivot(-2.0F, 2.0F, 0.5F)
        );

        ModelPartData leftWing = body.addChild(
                LEFT_WING,
                ModelPartBuilder.create()
                        .uv(0, 14)
                        .cuboid(0.0F, -2.0F, -0.5F, 1.0F, 4.0F, 6.0F),
                ModelTransform.pivot(3.5F, -1.0F, -1.0F)
        );

        ModelPartData rightWing = body.addChild(
                RIGHT_WING,
                ModelPartBuilder.create()
                        .uv(0, 14)
                        .mirrored()
                        .cuboid(-1.0F, -2.0F, -0.5F, 1.0F, 4.0F, 6.0F)
                        .mirrored(false),
                ModelTransform.pivot(-3.5F, -1.0F, -1.0F)
        );

        return TexturedModelData.of(modelData, 32, 32);
    }
    @Override
    public ModelPart getPart() {
        return this.root;
    }

    @Override
    public void setAngles(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.getPart().traverse().forEach(ModelPart::resetTransform);
        if (entity.antennaTicks > 0) {
            this.antenna.pitch += MathHelper.cos(animationProgress * 0.6F);
        }
        this.updateAnimation(entity.flyingAnimationState, BirtAnimations.FLY, animationProgress);
    }
}
