package com.ninni.species.server.entity.util;

import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

public enum GooberBehavior {
    IDLE("Idling", SoundEvents.EMPTY, 0),
    YAWN("Yawning", SpeciesSoundEvents.GOOBER_YAWN.get(), 30),
    SNEEZING("Sneezing", SpeciesSoundEvents.GOOBER_SNEEZE.get(), 110),
    REAR_UP("RearingUp", SpeciesSoundEvents.GOOBER_REAR_UP.get(), 75);
    private final String name;
    private final SoundEvent sound;
    private final int length;

    GooberBehavior(String name, SoundEvent sound, int length) {
        this.name = name;
        this.sound = sound;
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
}