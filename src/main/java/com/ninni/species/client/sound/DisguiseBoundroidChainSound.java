package com.ninni.species.client.sound;

import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;

/** Looping chain-rattle for Boundroid/Winch disguise bodies; mirrors AC's {@code BoundroidSound}
 *  but skips the {@code isSilent} gate and stops via {@link #requestStop()}. */
public final class DisguiseBoundroidChainSound extends AbstractTickableSoundInstance {

    private static final ResourceLocation CHAIN_LOOP_ID =
            new ResourceLocation("alexscaves", "boundroid_chain_loop");

    private final LivingEntity body;
    private final Entity companion;
    private float moveFade = 0F;
    private float prevDistance = 0F;
    private volatile boolean externallyStopped;

    public DisguiseBoundroidChainSound(LivingEntity body, Entity companion) {
        super(resolveSound(), SoundSource.NEUTRAL, SoundInstance.createUnseededRandom());
        this.body = body;
        this.companion = companion;
        this.attenuation = Attenuation.LINEAR;
        this.looping = true;
        this.volume = 0F;
        this.pitch = 1F;
        this.x = body.getX();
        this.y = body.getY();
        this.z = body.getZ();
        this.delay = 0;
    }

    private static SoundEvent resolveSound() {
        SoundEvent event = BuiltInRegistries.SOUND_EVENT.get(CHAIN_LOOP_ID);
        return event != null ? event : net.minecraft.sounds.SoundEvents.EMPTY;
    }

    /** Flip the sound to a stopped state — checked by {@link #canPlaySound} and {@link #tick}. */
    public void requestStop() {
        externallyStopped = true;
    }

    @Override
    public boolean canPlaySound() {
        return !externallyStopped && body.isAlive() && !body.isRemoved()
                && companion != null && !companion.isRemoved()
                && BuiltInRegistries.SOUND_EVENT.get(CHAIN_LOOP_ID) != null;
    }

    @Override
    public boolean canStartSilent() {
        return true;
    }

    @Override
    public void tick() {
        if (externallyStopped || body.isRemoved() || !body.isAlive()
                || companion == null || companion.isRemoved()) {
            this.volume = 0F;
            this.stop();
            return;
        }
        this.x = body.getX();
        this.y = body.getY();
        this.z = body.getZ();

        // Mirrors AC's BoundroidSound: stationary chain → moveFade ramps to 1 (silent);
        // moving chain → moveFade decays to 0 (full volume). Pitch tracks delta magnitude.
        float currentDistance = (float) body.distanceTo(companion);
        float delta = Math.min(Math.abs(prevDistance - currentDistance) * 20F, 1F);
        if (delta <= 0.3F) {
            moveFade = Math.min(1F, moveFade + 0.1F);
        } else {
            moveFade = Math.max(0F, moveFade - 0.25F);
        }
        this.volume = 1F - moveFade;
        this.pitch = 1F + delta;
        this.prevDistance = currentDistance;
    }
}
