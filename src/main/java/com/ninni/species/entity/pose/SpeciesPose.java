package com.ninni.species.entity.pose;

import net.minecraft.world.entity.Pose;

public enum SpeciesPose {
    PLANTING,
    SHAKE_SUCCESS,
    SHAKE_FAIL,
    LAYING_DOWN;

    public Pose get() {
        return Pose.valueOf(this.name());
    }
}
