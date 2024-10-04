package com.ninni.species.entity.pose;

import net.minecraft.world.entity.Pose;

public enum SpeciesPose {
    SCARED,
    PLANTING,
    UPROOTING,
    SNEEZING,
    SNEEZING_LAYING_DOWN,
    YAWNING,
    YAWNING_LAYING_DOWN,
    LAYING_DOWN,
    REARING_UP,
    ATTACK,
    STUN,
    COUGHING,
    HOWLING;

    public Pose get() {
        return Pose.valueOf(this.name());
    }
}