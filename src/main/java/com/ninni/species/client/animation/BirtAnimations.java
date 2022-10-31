package com.ninni.species.client.animation;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.entity.animation.Animation;
import net.minecraft.client.render.entity.animation.AnimationHelper;
import net.minecraft.client.render.entity.animation.Keyframe;
import net.minecraft.client.render.entity.animation.Transformation;

import static com.ninni.species.client.model.entity.BirtEntityModel.ANTENNA;
import static net.minecraft.client.render.entity.model.EntityModelPartNames.*;

@Environment(EnvType.CLIENT)
public class BirtAnimations {

    public static final Animation FLY = Animation.Builder.create(1f).looping()
            .addBoneAnimation(BODY, new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0f, AnimationHelper.method_41823(0f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.25f, AnimationHelper.method_41823(0f, -1f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.5f, AnimationHelper.method_41823(0f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.75f, AnimationHelper.method_41823(0f, -1f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(1f, AnimationHelper.method_41823(0f, 0f, 0f), Transformation.Interpolations.field_37884))
            )
            .addBoneAnimation(BODY, new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0f, AnimationHelper.method_41829(0f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.125f, AnimationHelper.method_41829(-5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.375f, AnimationHelper.method_41829(5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.625f, AnimationHelper.method_41829(-5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.875f, AnimationHelper.method_41829(5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(1f, AnimationHelper.method_41829(0f, 0f, 0f), Transformation.Interpolations.field_37884))
            )
            .addBoneAnimation(ANTENNA, new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0f, AnimationHelper.method_41829(-47.5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.125f, AnimationHelper.method_41829(-35f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.25f, AnimationHelper.method_41829(-47.5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.375f, AnimationHelper.method_41829(-35f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.5f, AnimationHelper.method_41829(-47.5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.625f, AnimationHelper.method_41829(-35f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.75f, AnimationHelper.method_41829(-47.5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.875f, AnimationHelper.method_41829(-35f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(1f, AnimationHelper.method_41829(-47.5f, 0f, 0f), Transformation.Interpolations.field_37884))
            )
            .addBoneAnimation(TAIL, new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0f, AnimationHelper.method_41829(-33.75f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.125f, AnimationHelper.method_41829(-22.5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.375f, AnimationHelper.method_41829(-45f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.625f, AnimationHelper.method_41829(-22.5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.875f, AnimationHelper.method_41829(-45f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(1f, AnimationHelper.method_41829(-33.75f, 0f, 0f), Transformation.Interpolations.field_37884))
            )
            .addBoneAnimation(LEFT_LEG, new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0f, AnimationHelper.method_41829(37.5f + 22.5f, 0f, 0f), Transformation.Interpolations.field_37884),
                    new Keyframe(0.08333333333333333f, AnimationHelper.method_41829(45 + 22.5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.3333333333333333f, AnimationHelper.method_41829(45f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.5833333333333334f, AnimationHelper.method_41829(45 + 22.5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.8333333333333334f, AnimationHelper.method_41829(45f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(1f, AnimationHelper.method_41829(37.5f + 22.5f, 0f, 0f), Transformation.Interpolations.field_37884))
            )
            .addBoneAnimation(LEFT_WING, new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0f, AnimationHelper.method_41823(0f, 0f, 1f), Transformation.Interpolations.field_37884))
            )
            .addBoneAnimation(LEFT_WING, new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0f, AnimationHelper.method_41829(-65f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.25f, AnimationHelper.method_41829(-65f, -1.1776683095376939e-7f, -135f), Transformation.Interpolations.field_37884),
                    new Keyframe(0.5f, AnimationHelper.method_41829(-65f, 0f, 0f), Transformation.Interpolations.field_37884),
                    new Keyframe(0.75f, AnimationHelper.method_41829(-65f, -1.1776683095376939e-7f, -135f), Transformation.Interpolations.field_37884),
                    new Keyframe(1f, AnimationHelper.method_41829(-65f, 0f, 0f), Transformation.Interpolations.field_37884))
            )
            .addBoneAnimation(RIGHT_WING, new Transformation(Transformation.Targets.TRANSLATE,
                    new Keyframe(0f, AnimationHelper.method_41823(0f, 0f, 1f), Transformation.Interpolations.field_37884))
            )
            .addBoneAnimation(RIGHT_WING, new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0f, AnimationHelper.method_41829(-65f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.25f, AnimationHelper.method_41829(-65f, 1.1776683095376939e-7f, 135f), Transformation.Interpolations.field_37884),
                    new Keyframe(0.5f, AnimationHelper.method_41829(-65f, 0f, 0f), Transformation.Interpolations.field_37884),
                    new Keyframe(0.75f, AnimationHelper.method_41829(-65f, 1.1776683095376939e-7f, 135f), Transformation.Interpolations.field_37884),
                    new Keyframe(1f, AnimationHelper.method_41829(-65f, 0f, 0f), Transformation.Interpolations.field_37884))
            )
            .addBoneAnimation(RIGHT_LEG, new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0f, AnimationHelper.method_41829(41.25f + 22.5f, 0f, 0f), Transformation.Interpolations.field_37884),
                    new Keyframe(0.08333333333333333f, AnimationHelper.method_41829(33.75f + 22.5f, 0f, 0f), Transformation.Interpolations.field_37884),
                    new Keyframe(0.20833333333333334f, AnimationHelper.method_41829(45f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.4583333333333333f, AnimationHelper.method_41829(45 + 22.5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.7083333333333334f, AnimationHelper.method_41829(45f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.9583333333333334f, AnimationHelper.method_41829(45 + 22.5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(1f, AnimationHelper.method_41829(41.25f + 22.5f, 0f, 0f), Transformation.Interpolations.field_37884))
            )
            .build();

    public static final Animation MESSAGE = Animation.Builder.create(1.5f)
            .addBoneAnimation(ANTENNA, new Transformation(Transformation.Targets.ROTATE,
                    new Keyframe(0f, AnimationHelper.method_41829(0f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.125f, AnimationHelper.method_41829(60f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.25f, AnimationHelper.method_41829(-60f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.375f, AnimationHelper.method_41829(45f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.5f, AnimationHelper.method_41829(-45f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(0.75f, AnimationHelper.method_41829(22.5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(1f, AnimationHelper.method_41829(-22.5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(1.25f, AnimationHelper.method_41829(5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(1.4166666666666667f, AnimationHelper.method_41829(-5f, 0f, 0f), Transformation.Interpolations.field_37884), 
                    new Keyframe(1.5f, AnimationHelper.method_41829(0f, 0f, 0f), Transformation.Interpolations.field_37884))
            )
            .build();
}
