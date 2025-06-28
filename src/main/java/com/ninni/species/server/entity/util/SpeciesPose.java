package com.ninni.species.server.entity.util;

import net.minecraft.world.entity.Pose;

public enum SpeciesPose {
    KNOT,
    READJUSTING,
    TWITCHING,
    LOOKING_AROUND,
    SHAKING,
    SPLITTING,
    FLAPPING,
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
    DASH,
    SLASH_ATTACK,
    STUN,
    CROUCHING_STUN,
    COUGHING,
    HOWLING,
    SEARCHING,
    RECHARGE,
    REDIRECT_DAMAGE,
    ABSORB_DAMAGE,
    SPAWNING,
    HAUNTING,
    SPOT;

    public Pose get() {
        return Pose.valueOf(this.name());
    }
}