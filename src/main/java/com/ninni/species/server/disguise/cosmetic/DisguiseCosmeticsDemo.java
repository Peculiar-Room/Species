package com.ninni.species.server.disguise.cosmetic;

import com.ninni.species.api.disguise.DisguiseCosmetics;
import com.ninni.species.api.disguise.DisguiseRenderer;
import com.ninni.species.registry.SpeciesConfig;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.RandomSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.loading.FMLEnvironment;

/** Demo registrations covering every {@link DisguiseCosmetics} axis. Gated at invocation time
 *  on {@code wickedMask.enableCosmeticsDemo} (checking at registration races SERVER config load). */
public final class DisguiseCosmeticsDemo {

    private DisguiseCosmeticsDemo() {}

    /** Demo flag, with throwables (config still loading) → false. */
    private static boolean enabled() {
        try {
            return SpeciesConfig.ENABLE_COSMETICS_DEMO.get();
        } catch (Throwable ignored) {
            return false;
        }
    }

    /** Registers demos unconditionally; runtime flag gate inside each method controls effect. */
    public static void registerDemo() {
        // PIG → villager hurt/death sounds.
        DisguiseCosmeticRegistry.register(EntityType.PIG, new DisguiseCosmetics() {
            @Override
            public SoundEvent overrideHurtSound(LivingEntity wearer, LivingEntity disguise, DamageSource source) {
                return enabled() ? SoundEvents.VILLAGER_HURT : null;
            }

            @Override
            public SoundEvent overrideDeathSound(LivingEntity wearer, LivingEntity disguise) {
                return enabled() ? SoundEvents.VILLAGER_DEATH : null;
            }
        });

        // COW → red mooshroom texture.
        final ResourceLocation redMooshroomTexture = new ResourceLocation("minecraft", "textures/entity/cow/red_mooshroom.png");
        DisguiseCosmeticRegistry.register(EntityType.COW, new DisguiseCosmetics() {
            @Override
            public ResourceLocation overrideTexture(LivingEntity wearer, LivingEntity disguise) {
                return enabled() ? redMooshroomTexture : null;
            }
        });

        // SHEEP → heart particles every ~10 ticks.
        DisguiseCosmeticRegistry.register(EntityType.SHEEP, new DisguiseCosmetics() {
            @Override
            public void emitParticles(LivingEntity wearer, LivingEntity disguise, Level level, RandomSource rng) {
                if (!enabled()) return;
                if (rng.nextInt(10) != 0) return;
                double x = wearer.getX() + (rng.nextDouble() - 0.5) * wearer.getBbWidth();
                double y = wearer.getY() + 0.5 + rng.nextDouble() * wearer.getBbHeight();
                double z = wearer.getZ() + (rng.nextDouble() - 0.5) * wearer.getBbWidth();
                level.addParticle(ParticleTypes.HEART, x, y, z, 0.0, 0.05, 0.0);
            }
        });

        // CHICKEN → death sound only (verifies null hurt-hook falls through to vanilla).
        DisguiseCosmeticRegistry.register(EntityType.CHICKEN, new DisguiseCosmetics() {
            @Override
            public SoundEvent overrideDeathSound(LivingEntity wearer, LivingEntity disguise) {
                return enabled() ? SoundEvents.GHAST_SCREAM : null;
            }
        });

        // RABBIT → custom name tag.
        final Component rabbitName = Component.literal("Mr. Bunny").withStyle(ChatFormatting.LIGHT_PURPLE);
        DisguiseCosmeticRegistry.register(EntityType.RABBIT, new DisguiseCosmetics() {
            @Override
            public Component overrideNameTag(LivingEntity wearer, LivingEntity disguise) {
                return enabled() ? rabbitName : null;
            }
        });

        // SPIDER → world-render scale 0.6 (smaller in world; inventory preview unaffected).
        DisguiseCosmeticRegistry.register(EntityType.SPIDER, new DisguiseCosmetics() {
            @Override
            public float overrideWorldScale(LivingEntity wearer, LivingEntity disguise) {
                return enabled() ? 0.6F : Float.NaN;
            }
        });

        // BAT → magenta outline + auto-applied infinite Glowing II (hidden particles).
        // Equivalent to /effect give @s minecraft:glowing infinite 1 true. Removed on un-equip/swap.
        DisguiseCosmeticRegistry.register(EntityType.BAT, new DisguiseCosmetics() {
            @Override
            public Integer overrideGlowColor(LivingEntity wearer, LivingEntity disguise) {
                return enabled() ? 0xFF00FF : null;
            }

            @Override
            public java.util.Collection<net.minecraft.world.effect.MobEffectInstance> wearerEffectsWhileWorn(LivingEntity wearer, LivingEntity disguise) {
                if (!enabled()) return java.util.Collections.emptyList();
                return java.util.Collections.singletonList(new net.minecraft.world.effect.MobEffectInstance(
                        net.minecraft.world.effect.MobEffects.GLOWING,
                        net.minecraft.world.effect.MobEffectInstance.INFINITE_DURATION,
                        1, false, false, true));
            }
        });

        // SQUID → ambient sound roughly every 5 seconds (random spread).
        DisguiseCosmeticRegistry.register(EntityType.SQUID, new DisguiseCosmetics() {
            @Override
            public SoundEvent overrideAmbientSound(LivingEntity wearer, LivingEntity disguise) {
                return enabled() ? SoundEvents.SQUID_AMBIENT : null;
            }
        });

        // SLIME → DisguiseRenderer with continuous Y-spin. Client-side only:
        // DisguiseRenderer signatures reference client-only render types.
        if (FMLEnvironment.dist == Dist.CLIENT) {
            ClientOnlyDemos.registerSlimeRenderer();
        }
    }

    /** Lazy-loaded; never resolved on a dedicated server. */
    private static final class ClientOnlyDemos {
        static void registerSlimeRenderer() {
            DisguiseCosmeticRegistry.register(EntityType.SLIME, new DisguiseCosmetics() {
                @Override
                public DisguiseRenderer overrideRenderer(LivingEntity wearer, LivingEntity disguise) {
                    if (!enabled()) return null;
                    return (w, d, baseRenderer, bodyYaw, partialTick, poseStack, buffer, packedLight) -> {
                        poseStack.pushPose();
                        float spin = (w.tickCount + partialTick) * 6.0F;
                        poseStack.translate(0.0, d.getBbHeight() * 0.5, 0.0);
                        poseStack.mulPose(com.mojang.math.Axis.YP.rotationDegrees(spin));
                        poseStack.translate(0.0, -d.getBbHeight() * 0.5, 0.0);
                        @SuppressWarnings({"rawtypes", "unchecked"})
                        net.minecraft.client.renderer.entity.EntityRenderer raw = baseRenderer;
                        raw.render(d, bodyYaw, partialTick, poseStack, buffer, packedLight);
                        poseStack.popPose();
                    };
                }
            });
        }
    }
}
