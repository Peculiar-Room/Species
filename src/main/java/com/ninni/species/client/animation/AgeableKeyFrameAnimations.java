package com.ninni.species.client.animation;

import com.ninni.species.client.model.HierarchicalAgeableListModel;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.util.Mth;
import org.joml.Vector3f;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Environment(value= EnvType.CLIENT)
public class AgeableKeyFrameAnimations {

    public static void animate(HierarchicalAgeableListModel<?> hierarchicalModel, AnimationDefinition animationDefinition, long l, float f, Vector3f vector3f) {
        float g = AgeableKeyFrameAnimations.getElapsedSeconds(animationDefinition, l);
        for (Map.Entry<String, List<AnimationChannel>> entry : animationDefinition.boneAnimations().entrySet()) {
            Optional<ModelPart> optional = hierarchicalModel.getAnyDescendantWithName(entry.getKey());
            List<AnimationChannel> list = entry.getValue();
            optional.ifPresent(modelPart -> list.forEach(animationChannel -> {
                Keyframe[] keyframes = animationChannel.keyframes();
                int i2 = Math.max(0, Mth.binarySearch(0, keyframes.length, i -> g <= keyframes[i].timestamp()) - 1);
                int j = Math.min(keyframes.length - 1, i2 + 1);
                Keyframe keyframe = keyframes[i2];
                Keyframe keyframe2 = keyframes[j];
                float h = g - keyframe.timestamp();
                float k = j != i2 ? Mth.clamp(h / (keyframe2.timestamp() - keyframe.timestamp()), 0.0f, 1.0f) : 0.0f;
                keyframe2.interpolation().apply(vector3f, k, keyframes, i2, j, f);
                animationChannel.target().apply(modelPart, vector3f);
            }));
        }
    }

    private static float getElapsedSeconds(AnimationDefinition animationDefinition, long l) {
        float f = (float)l / 1000.0f;
        return animationDefinition.looping() ? f % animationDefinition.lengthInSeconds() : f;
    }

    public static Vector3f posVec(float f, float g, float h) {
        return new Vector3f(f, -g, h);
    }

    public static Vector3f degreeVec(float f, float g, float h) {
        return new Vector3f(f * ((float)Math.PI / 180), g * ((float)Math.PI / 180), h * ((float)Math.PI / 180));
    }

    public static Vector3f scaleVec(double d, double e, double f) {
        return new Vector3f((float)(d - 1.0), (float)(e - 1.0), (float)(f - 1.0));
    }
}

