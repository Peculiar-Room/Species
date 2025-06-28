package com.ninni.species.client.screen;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

//Code taken and modified from Alex
public class ScreenShakeEvent {
    private final Vec3 position;
    private final int duration;
    private final float degree;
    private final float distance;
    private final boolean groundRequired;
    private int age;

    public ScreenShakeEvent(Vec3 position, int duration, float degree, float distance, boolean groundRequired) {
        this.position = position;
        this.duration = duration;
        this.degree = degree;
        this.distance = distance;
        this.groundRequired = groundRequired;
        this.age = 0;
    }


    public float getDegree(Entity cameraEntity, float partialTicks) {
        double dist = position.distanceTo(cameraEntity.position());
        if (dist < distance && (!groundRequired || cameraEntity.onGround())) {
            return (1F - (float) (dist / distance)) * degree * (float) Math.sin(((age + partialTicks) / duration) * Math.PI);
        } else return 0.0F;
    }

    public void tick() {
        age++;
    }

    public boolean isDone() {
        return age >= duration;
    }
}
