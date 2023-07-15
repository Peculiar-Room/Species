package com.ninni.species.client.animation;

import com.ninni.species.client.model.entity.BirtModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;
import net.minecraft.client.model.geom.PartNames;

@Environment(EnvType.CLIENT)
public class BirtAnimations {

        public static final AnimationDefinition FLY = AnimationDefinition.Builder.withLength(1f).looping()
                .addAnimation(PartNames.BODY, new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.25f, KeyframeAnimations.posVec(0f, -1f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.5f, KeyframeAnimations.posVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.75f, KeyframeAnimations.posVec(0f, -1f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(1f, KeyframeAnimations.posVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR))
                )
                .addAnimation(PartNames.BODY, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0f, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.125f, KeyframeAnimations.degreeVec(-5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.375f, KeyframeAnimations.degreeVec(5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.625f, KeyframeAnimations.degreeVec(-5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.875f, KeyframeAnimations.degreeVec(5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(1f, KeyframeAnimations.degreeVec(0f, 0f, 0f), AnimationChannel.Interpolations.LINEAR))
                )
                .addAnimation(BirtModel.ANTENNA, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0f, KeyframeAnimations.degreeVec(-47.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.125f, KeyframeAnimations.degreeVec(-35f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.25f, KeyframeAnimations.degreeVec(-47.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.375f, KeyframeAnimations.degreeVec(-35f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.5f, KeyframeAnimations.degreeVec(-47.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.625f, KeyframeAnimations.degreeVec(-35f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.75f, KeyframeAnimations.degreeVec(-47.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.875f, KeyframeAnimations.degreeVec(-35f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(1f, KeyframeAnimations.degreeVec(-47.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR))
                )
                .addAnimation(PartNames.TAIL, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0f, KeyframeAnimations.degreeVec(-33.75f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.125f, KeyframeAnimations.degreeVec(-22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.375f, KeyframeAnimations.degreeVec(-45f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.625f, KeyframeAnimations.degreeVec(-22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.875f, KeyframeAnimations.degreeVec(-45f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(1f, KeyframeAnimations.degreeVec(-33.75f, 0f, 0f), AnimationChannel.Interpolations.LINEAR))
                )
                .addAnimation(PartNames.LEFT_LEG, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0f, KeyframeAnimations.degreeVec(37.5f + 22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.08333333333333333f, KeyframeAnimations.degreeVec(45 + 22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.3333333333333333f, KeyframeAnimations.degreeVec(45f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.5833333333333334f, KeyframeAnimations.degreeVec(45 + 22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.8333333333333334f, KeyframeAnimations.degreeVec(45f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(1f, KeyframeAnimations.degreeVec(37.5f + 22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR))
                )
                .addAnimation(PartNames.LEFT_WING, new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 1f), AnimationChannel.Interpolations.LINEAR))
                )
                .addAnimation(PartNames.LEFT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0f, KeyframeAnimations.degreeVec(-65f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.25f, KeyframeAnimations.degreeVec(-65f, -1.1776683095376939e-7f, -135f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.5f, KeyframeAnimations.degreeVec(-65f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.75f, KeyframeAnimations.degreeVec(-65f, -1.1776683095376939e-7f, -135f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(1f, KeyframeAnimations.degreeVec(-65f, 0f, 0f), AnimationChannel.Interpolations.LINEAR))
                )
                .addAnimation(PartNames.RIGHT_WING, new AnimationChannel(AnimationChannel.Targets.POSITION,
                        new Keyframe(0f, KeyframeAnimations.posVec(0f, 0f, 1f), AnimationChannel.Interpolations.LINEAR))
                )
                .addAnimation(PartNames.RIGHT_WING, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0f, KeyframeAnimations.degreeVec(-65f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.25f, KeyframeAnimations.degreeVec(-65f, 1.1776683095376939e-7f, 135f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.5f, KeyframeAnimations.degreeVec(-65f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.75f, KeyframeAnimations.degreeVec(-65f, 1.1776683095376939e-7f, 135f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(1f, KeyframeAnimations.degreeVec(-65f, 0f, 0f), AnimationChannel.Interpolations.LINEAR))
                )
                .addAnimation(PartNames.RIGHT_LEG, new AnimationChannel(AnimationChannel.Targets.ROTATION,
                        new Keyframe(0f, KeyframeAnimations.degreeVec(41.25f + 22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.08333333333333333f, KeyframeAnimations.degreeVec(33.75f + 22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.20833333333333334f, KeyframeAnimations.degreeVec(45f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.4583333333333333f, KeyframeAnimations.degreeVec(45 + 22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.7083333333333334f, KeyframeAnimations.degreeVec(45f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(0.9583333333333334f, KeyframeAnimations.degreeVec(45 + 22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR),
                        new Keyframe(1f, KeyframeAnimations.degreeVec(41.25f + 22.5f, 0f, 0f), AnimationChannel.Interpolations.LINEAR))
                )
                .build();

}
