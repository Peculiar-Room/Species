package com.ninni.species.client.animation;

import static com.ninni.species.client.model.entity.BirtEntityModel.ANTENNA;
import static net.minecraft.client.render.entity.model.EntityModelPartNames.BODY;
import static net.minecraft.client.render.entity.model.EntityModelPartNames.LEFT_LEG;
import static net.minecraft.client.render.entity.model.EntityModelPartNames.LEFT_WING;
import static net.minecraft.client.render.entity.model.EntityModelPartNames.RIGHT_LEG;
import static net.minecraft.client.render.entity.model.EntityModelPartNames.RIGHT_WING;
import static net.minecraft.client.render.entity.model.EntityModelPartNames.TAIL;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

@Environment(EnvType.CLIENT)
public class BirtAnimations {

        public static final Animation FLY = Animation.Builder.create(1f).looping()
                .addBoneAnimation(BODY, new Transformation(Transformation.Targets.TRANSLATE,
                        new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.25f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.5f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.75f, AnimationHelper.createTranslationalVector(0f, -1f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(1f, AnimationHelper.createTranslationalVector(0f, 0f, 0f), Transformation.Interpolations.LINEAR))
                )
                .addBoneAnimation(BODY, new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0f, AnimationHelper.createRotationalVector(0f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.125f, AnimationHelper.createRotationalVector(-5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.375f, AnimationHelper.createRotationalVector(5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.625f, AnimationHelper.createRotationalVector(-5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.875f, AnimationHelper.createRotationalVector(5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(1f, AnimationHelper.createRotationalVector(0f, 0f, 0f), Transformation.Interpolations.LINEAR))
                )
                .addBoneAnimation(ANTENNA, new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0f, AnimationHelper.createRotationalVector(-47.5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.125f, AnimationHelper.createRotationalVector(-35f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.25f, AnimationHelper.createRotationalVector(-47.5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.375f, AnimationHelper.createRotationalVector(-35f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.5f, AnimationHelper.createRotationalVector(-47.5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.625f, AnimationHelper.createRotationalVector(-35f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.75f, AnimationHelper.createRotationalVector(-47.5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.875f, AnimationHelper.createRotationalVector(-35f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(1f, AnimationHelper.createRotationalVector(-47.5f, 0f, 0f), Transformation.Interpolations.LINEAR))
                )
                .addBoneAnimation(TAIL, new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0f, AnimationHelper.createRotationalVector(-33.75f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.125f, AnimationHelper.createRotationalVector(-22.5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.375f, AnimationHelper.createRotationalVector(-45f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.625f, AnimationHelper.createRotationalVector(-22.5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.875f, AnimationHelper.createRotationalVector(-45f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(1f, AnimationHelper.createRotationalVector(-33.75f, 0f, 0f), Transformation.Interpolations.LINEAR))
                )
                .addBoneAnimation(LEFT_LEG, new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0f, AnimationHelper.createRotationalVector(37.5f + 22.5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.08333333333333333f, AnimationHelper.createRotationalVector(45 + 22.5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.3333333333333333f, AnimationHelper.createRotationalVector(45f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.5833333333333334f, AnimationHelper.createRotationalVector(45 + 22.5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.8333333333333334f, AnimationHelper.createRotationalVector(45f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(1f, AnimationHelper.createRotationalVector(37.5f + 22.5f, 0f, 0f), Transformation.Interpolations.LINEAR))
                )
                .addBoneAnimation(LEFT_WING, new Transformation(Transformation.Targets.TRANSLATE,
                        new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 1f), Transformation.Interpolations.LINEAR))
                )
                .addBoneAnimation(LEFT_WING, new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0f, AnimationHelper.createRotationalVector(-65f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.25f, AnimationHelper.createRotationalVector(-65f, -1.1776683095376939e-7f, -135f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.5f, AnimationHelper.createRotationalVector(-65f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.75f, AnimationHelper.createRotationalVector(-65f, -1.1776683095376939e-7f, -135f), Transformation.Interpolations.LINEAR),
                        new Keyframe(1f, AnimationHelper.createRotationalVector(-65f, 0f, 0f), Transformation.Interpolations.LINEAR))
                )
                .addBoneAnimation(RIGHT_WING, new Transformation(Transformation.Targets.TRANSLATE,
                        new Keyframe(0f, AnimationHelper.createTranslationalVector(0f, 0f, 1f), Transformation.Interpolations.LINEAR))
                )
                .addBoneAnimation(RIGHT_WING, new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0f, AnimationHelper.createRotationalVector(-65f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.25f, AnimationHelper.createRotationalVector(-65f, 1.1776683095376939e-7f, 135f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.5f, AnimationHelper.createRotationalVector(-65f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.75f, AnimationHelper.createRotationalVector(-65f, 1.1776683095376939e-7f, 135f), Transformation.Interpolations.LINEAR),
                        new Keyframe(1f, AnimationHelper.createRotationalVector(-65f, 0f, 0f), Transformation.Interpolations.LINEAR))
                )
                .addBoneAnimation(RIGHT_LEG, new Transformation(Transformation.Targets.ROTATE,
                        new Keyframe(0f, AnimationHelper.createRotationalVector(41.25f + 22.5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.08333333333333333f, AnimationHelper.createRotationalVector(33.75f + 22.5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.20833333333333334f, AnimationHelper.createRotationalVector(45f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.4583333333333333f, AnimationHelper.createRotationalVector(45 + 22.5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.7083333333333334f, AnimationHelper.createRotationalVector(45f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(0.9583333333333334f, AnimationHelper.createRotationalVector(45 + 22.5f, 0f, 0f), Transformation.Interpolations.LINEAR),
                        new Keyframe(1f, AnimationHelper.createRotationalVector(41.25f + 22.5f, 0f, 0f), Transformation.Interpolations.LINEAR))
                )
                .build();

}
