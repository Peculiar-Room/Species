package com.ninni.species.entity.enums;

import com.ninni.species.entity.pose.SpeciesPose;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Pose;

public enum CruncherBehavior {
    IDLE("Idling", SoundEvents.EMPTY, Pose.STANDING, 0),
    ROAR("Roaring", SoundEvents.EMPTY, Pose.ROARING, 77),
    ATTACK("Attacking", SoundEvents.EMPTY, SpeciesPose.ATTACK.get(), 20),
    STUN("Stunned", SoundEvents.EMPTY, SpeciesPose.STUN.get(), 0);
    private final String name;
    private final SoundEvent sound;
    private final Pose pose;
    private final int length;

    CruncherBehavior(String name, SoundEvent sound, Pose pose, int length) {
        this.name = name;
        this.sound = sound;
        this.pose = pose;
        this.length = length;
    }

    public String getName() {
        return name;
    }
    public SoundEvent getSound() {
        return sound;
    }
    public int getLength() {
        return length;
    }
    public Pose getPose() {
        return pose;
    }
}