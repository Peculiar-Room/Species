package com.ninni.species.server.disguise.cosmetic;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseCosmetics;
import com.ninni.species.api.disguise.DisguiseRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

/**
 * Composes multiple {@link DisguiseCosmetics}: override hooks first-non-null-wins, {@link #emitParticles}
 * additive in registration order. Use {@link DisguiseCosmeticRegistry#compose} to layer without overwriting.
 */
public final class CompositeDisguiseCosmetics implements DisguiseCosmetics {

    private final DisguiseCosmetics[] children;

    private CompositeDisguiseCosmetics(DisguiseCosmetics[] children) {
        this.children = children;
    }

    /** Builds a composite from the given cosmetics. Single child returned as-is. */
    public static DisguiseCosmetics of(DisguiseCosmetics... children) {
        if (children == null || children.length == 0) {
            throw new IllegalArgumentException("CompositeDisguiseCosmetics needs at least one child");
        }
        if (children.length == 1) return children[0];
        return new CompositeDisguiseCosmetics(children.clone());
    }

    @Override
    public SoundEvent overrideHurtSound(LivingEntity wearer, LivingEntity disguise, DamageSource source) {
        for (DisguiseCosmetics c : children) {
            SoundEvent v = c.overrideHurtSound(wearer, disguise, source);
            if (v != null) return v;
        }
        return null;
    }

    @Override
    public SoundEvent overrideDeathSound(LivingEntity wearer, LivingEntity disguise) {
        for (DisguiseCosmetics c : children) {
            SoundEvent v = c.overrideDeathSound(wearer, disguise);
            if (v != null) return v;
        }
        return null;
    }

    @Override
    public SoundEvent overrideAmbientSound(LivingEntity wearer, LivingEntity disguise) {
        for (DisguiseCosmetics c : children) {
            SoundEvent v = c.overrideAmbientSound(wearer, disguise);
            if (v != null) return v;
        }
        return null;
    }

    @Override
    public ResourceLocation overrideTexture(LivingEntity wearer, LivingEntity disguise) {
        for (DisguiseCosmetics c : children) {
            ResourceLocation v = c.overrideTexture(wearer, disguise);
            if (v != null) return v;
        }
        return null;
    }

    @Override
    public void emitParticles(LivingEntity wearer, LivingEntity disguise, Level level, RandomSource rng) {
        for (DisguiseCosmetics c : children) {
            try {
                c.emitParticles(wearer, disguise, level, rng);
            } catch (Throwable ignored) {
                // Particle failures are visual-only; swallowed to keep the tick alive.
            }
        }
    }

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        for (DisguiseCosmetics c : children) {
            try { c.onSpecialAction(wearer, disguise, context); }
            catch (Throwable ignored) { /* visual side-effect; tick must continue */ }
        }
    }

    @Override
    public Component overrideNameTag(LivingEntity wearer, LivingEntity disguise) {
        for (DisguiseCosmetics c : children) {
            Component v = c.overrideNameTag(wearer, disguise);
            if (v != null) return v;
        }
        return null;
    }

    @Override
    public float overrideWorldScale(LivingEntity wearer, LivingEntity disguise) {
        for (DisguiseCosmetics c : children) {
            float v = c.overrideWorldScale(wearer, disguise);
            if (!Float.isNaN(v) && v != 0.0F) return v;
        }
        return Float.NaN;
    }

    @Override
    public Integer overrideGlowColor(LivingEntity wearer, LivingEntity disguise) {
        for (DisguiseCosmetics c : children) {
            Integer v = c.overrideGlowColor(wearer, disguise);
            if (v != null) return v;
        }
        return null;
    }

    @Override
    public DisguiseRenderer overrideRenderer(LivingEntity wearer, LivingEntity disguise) {
        for (DisguiseCosmetics c : children) {
            DisguiseRenderer v = c.overrideRenderer(wearer, disguise);
            if (v != null) return v;
        }
        return null;
    }

    @Override
    public java.util.Collection<net.minecraft.world.effect.MobEffectInstance> wearerEffectsWhileWorn(LivingEntity wearer, LivingEntity disguise) {
        java.util.List<net.minecraft.world.effect.MobEffectInstance> all = null;
        for (DisguiseCosmetics c : children) {
            java.util.Collection<net.minecraft.world.effect.MobEffectInstance> v = c.wearerEffectsWhileWorn(wearer, disguise);
            if (v != null && !v.isEmpty()) {
                if (all == null) all = new java.util.ArrayList<>();
                all.addAll(v);
            }
        }
        return all != null ? all : java.util.Collections.emptyList();
    }
}
