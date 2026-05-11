package com.ninni.species.server.disguise.demo;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.disguise.SpeciesAPI;
import com.ninni.species.registry.SpeciesConfig;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

/** Demo of {@link com.ninni.species.api.disguise.ActionContext}-aware dispatch on the pig
 *  disguise (4 branches: ground/flying/swimming/underwater). Runtime-gated on
 *  {@code wickedMask.enableCosmeticsDemo} since SERVER config isn't loaded at setup time. */
public final class ContextualActionDemo {

    private ContextualActionDemo() {}

    private static boolean enabled() {
        try { return SpeciesConfig.ENABLE_COSMETICS_DEMO.get(); }
        catch (Throwable ignored) { return false; }
    }

    public static void registerDemo() {
        SpeciesAPI.composeBehavior(EntityType.PIG, DisguiseBehavior.specialAction(
                (wearer, disguise, ctx) -> {
                    if (!enabled()) return;
                    Level level = wearer.level();
                    if (level.isClientSide || !(level instanceof ServerLevel server)) return;
                    double x = wearer.getX();
                    double y = wearer.getY() + wearer.getBbHeight() * 0.5;
                    double z = wearer.getZ();
                    switch (ctx.phase()) {
                        case GROUND -> {
                            server.sendParticles(ParticleTypes.HAPPY_VILLAGER, x, y, z, 12, 0.4, 0.4, 0.4, 0.0);
                            server.playSound(null, x, y, z, SoundEvents.PIG_AMBIENT,
                                    SoundSource.PLAYERS, 1.5F, 1.0F);
                        }
                        case FLYING -> {
                            server.sendParticles(ParticleTypes.CLOUD, x, y, z, 12, 0.5, 0.3, 0.5, 0.05);
                            server.playSound(null, x, y, z, SoundEvents.PHANTOM_FLAP,
                                    SoundSource.PLAYERS, 1.5F, 1.0F);
                        }
                        case SWIMMING -> {
                            server.sendParticles(ParticleTypes.SPLASH, x, y, z, 16, 0.5, 0.2, 0.5, 0.1);
                            server.playSound(null, x, y, z, SoundEvents.DOLPHIN_SPLASH,
                                    SoundSource.PLAYERS, 1.5F, 1.0F);
                        }
                        case UNDERWATER -> {
                            server.sendParticles(ParticleTypes.BUBBLE_COLUMN_UP, x, y, z, 20, 0.3, 0.5, 0.3, 0.0);
                            server.playSound(null, x, y, z, SoundEvents.AMBIENT_UNDERWATER_LOOP_ADDITIONS,
                                    SoundSource.PLAYERS, 1.5F, 1.0F);
                        }
                    }
                }));
    }
}
