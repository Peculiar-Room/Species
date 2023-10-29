package com.ninni.species.entity.pose;

import net.minecraft.world.entity.Pose;

public enum SpeciesPose {
    PLANTING,
    SHAKE_SUCCESS,
    SHAKE_FAIL,
    YAWNING,
    YAWNING_LAYING_DOWN,
    LAYING_DOWN,
    REARING_UP,
    ATTACK,
    STUN;

    public Pose get() {
        return Pose.valueOf(this.name());
    }
}
