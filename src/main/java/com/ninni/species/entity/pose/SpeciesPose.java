package com.ninni.species.entity.pose;

import net.minecraft.world.entity.Pose;

public enum SpeciesPose {
    SCARED,
    PLANTING,
    UPROOTING,
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
