package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.AnimationState;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/** Spawn Coastal Crab bury, crouch-driven and decoupled from the native cycle (which
 *  mis-routes our pre-set {@code isBuried} into {@code emerge}). We pin {@code BuryingCooldown}
 *  high and drive the bury/emerge animations + sounds + dirt particles ourselves. */
public final class SpawnCrabBehavior implements DisguiseBehavior {

    public static final SpawnCrabBehavior INSTANCE = new SpawnCrabBehavior();

    private static final TagKey<Block> BURY_BLOCKS_TAG = TagKey.create(
            Registries.BLOCK, new ResourceLocation("spawn", "coastal_crab_bury_blocks"));
    private static final ResourceLocation BURY_SOUND_ID = new ResourceLocation("spawn", "entity.coastal_crab.bury");
    private static final ResourceLocation EMERGE_SOUND_ID = new ResourceLocation("spawn", "entity.coastal_crab.emerge");

    private static final int BURY_DURATION_TICKS = 20;
    private static final int COOLDOWN_SUPPRESS = 1_000_000;
    /** Match {@code playDiggingEffects}: 10 BLOCK particles offset 0.65 below block centre. */
    private static final int PARTICLE_COUNT = 10;
    private static final double PARTICLE_Y_OFFSET = -0.65;
    /** Step-sound cadence inside {@code playDiggingEffects}. */
    private static final int STEP_SOUND_INTERVAL = 5;

    private static volatile boolean reflectionInited;
    private static Method setBuriedMethod;
    private static Method isBuriedMethod;
    private static Method setBuryingCooldownMethod;
    private static Field buryStateField;
    private static Field emergeStateField;
    private static Field buryTimerField;

    private static final class State {
        boolean crouching;
        boolean overSand;
    }

    /** Per-side maps so the rising-edge race in singleplayer (both sides share the same JVM and
     *  tick the same wearer) doesn't drop the sound/particle emission on whichever side ticks
     *  second — that side would see {@code st.crouching} already flipped and skip the edge work. */
    private final Map<UUID, State> serverStates = new ConcurrentHashMap<>();
    private final Map<UUID, State> clientStates = new ConcurrentHashMap<>();

    private SpawnCrabBehavior() {}

    @Override
    public void onCreated(LivingEntity wearer, LivingEntity disguise) {
        initReflection(disguise.getClass());
        // Clear NBT-loaded buried state so equip-time particles don't fire.
        invokeBoolean(setBuriedMethod, disguise, false);
        invokeInt(setBuryingCooldownMethod, disguise, COOLDOWN_SUPPRESS);
        if (buryTimerField != null) {
            try { buryTimerField.setInt(disguise, 0); } catch (IllegalAccessException ignored) {}
        }
        AnimationState bury = readState(buryStateField, disguise);
        AnimationState emerge = readState(emergeStateField, disguise);
        if (bury != null) bury.stop();
        if (emerge != null) emerge.stop();
    }

    @Override
    public void postTick(LivingEntity wearer, LivingEntity disguise) {
        initReflection(disguise.getClass());
        if (setBuriedMethod == null || setBuryingCooldownMethod == null) return;

        // Pin cooldown high every tick so handleBurying always decrements without firing
        // bury()/emerge(). Our behavior owns the bury state machine.
        invokeInt(setBuryingCooldownMethod, disguise, COOLDOWN_SUPPRESS);

        boolean crouchingNow = wearer.isCrouching();
        boolean overSandNow = isOverBuryBlock(wearer);
        Map<UUID, State> states = wearer.level().isClientSide ? clientStates : serverStates;
        State st = states.computeIfAbsent(wearer.getUUID(), k -> new State());
        boolean crouchEdge = crouchingNow != st.crouching;

        AnimationState bury = readState(buryStateField, disguise);
        AnimationState emerge = readState(emergeStateField, disguise);

        if (crouchEdge) {
            if (crouchingNow) {
                // Rising edge — animation, flag, timer; sound only on bury-tag blocks.
                if (emerge != null) emerge.stop();
                if (bury != null) bury.start(disguise.tickCount);
                writeBuryTimer(disguise, BURY_DURATION_TICKS);
                invokeBoolean(setBuriedMethod, disguise, true);
                if (overSandNow) {
                    playSoundServer(disguise, BURY_SOUND_ID, 1.0F);
                }
            } else {
                // Falling edge — emerge animation; sound only if we were buried over a tag block.
                if (bury != null) bury.stop();
                if (emerge != null) emerge.start(disguise.tickCount);
                writeBuryTimer(disguise, BURY_DURATION_TICKS);
                if (st.overSand && isBuried(disguise)) {
                    playSoundServer(disguise, EMERGE_SOUND_ID, 1.0F);
                }
                invokeBoolean(setBuriedMethod, disguise, false);
            }
            st.crouching = crouchingNow;
            st.overSand = overSandNow;
        } else {
            st.overSand = overSandNow;
        }

        // Per-tick dirt particles + step-sound cadence while the timer is running over a
        // bury-tag block. Replicates playDiggingEffects without going through the natural path.
        if (overSandNow && readBuryTimer(disguise) > 0) {
            spawnDiggingEffects(disguise);
        }

        // Stop the bury keyframe once the timer hits zero so the model's static BURIED pose
        // engages (gated on isBuried && !buryAnim.isStarted() && buryTimer == 0).
        if (crouchingNow && bury != null && bury.isStarted() && readBuryTimer(disguise) <= 0) {
            bury.stop();
        }
    }

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        if (wearer != null) {
            UUID id = wearer.getUUID();
            serverStates.remove(id);
            clientStates.remove(id);
        }
    }

    private static boolean isOverBuryBlock(LivingEntity wearer) {
        BlockPos below = wearer.blockPosition().below();
        return wearer.level().getBlockState(below).is(BURY_BLOCKS_TAG);
    }

    private static void spawnDiggingEffects(LivingEntity disguise) {
        if (!(disguise.level() instanceof ServerLevel server)) return;
        BlockPos onPos = disguise.getOnPos();
        BlockPos above = onPos.above();
        BlockState blockstate = disguise.level().getBlockState(onPos);
        if (!blockstate.isSolidRender(disguise.level(), onPos)) return;
        Vec3 c = Vec3.atCenterOf(above).add(0.0, PARTICLE_Y_OFFSET, 0.0);
        server.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, blockstate),
                c.x, c.y, c.z, PARTICLE_COUNT, 0.0, 0.0, 0.0, 0.0);
        if (disguise.tickCount % STEP_SOUND_INTERVAL == 0) {
            disguise.playSound(blockstate.getSoundType(disguise.level(), onPos.below(), disguise).getStepSound(),
                    0.5F, 1.25F);
        }
    }

    private static void playSoundServer(LivingEntity disguise, ResourceLocation id, float vol) {
        if (!(disguise.level() instanceof ServerLevel)) return;
        SoundEvent sound = BuiltInRegistries.SOUND_EVENT.get(id);
        if (sound == null) return;
        disguise.level().playSound(null, disguise.getX(), disguise.getY(), disguise.getZ(),
                sound, SoundSource.NEUTRAL, vol, 1.0F);
    }

    private static int readBuryTimer(LivingEntity disguise) {
        if (buryTimerField == null) return 0;
        try { return buryTimerField.getInt(disguise); }
        catch (IllegalAccessException e) { return 0; }
    }

    private static void writeBuryTimer(LivingEntity disguise, int value) {
        if (buryTimerField == null) return;
        try { buryTimerField.setInt(disguise, value); }
        catch (IllegalAccessException ignored) {}
    }

    private static boolean isBuried(LivingEntity disguise) {
        if (isBuriedMethod == null) return false;
        try { return (boolean) isBuriedMethod.invoke(disguise); }
        catch (ReflectiveOperationException e) { return false; }
    }

    private static AnimationState readState(Field field, LivingEntity disguise) {
        if (field == null) return null;
        try {
            Object v = field.get(disguise);
            return (v instanceof AnimationState s) ? s : null;
        } catch (IllegalAccessException e) { return null; }
    }

    private static void invokeBoolean(Method m, LivingEntity disguise, boolean value) {
        if (m == null) return;
        try { m.invoke(disguise, value); }
        catch (ReflectiveOperationException ignored) {}
    }

    private static void invokeInt(Method m, LivingEntity disguise, int value) {
        if (m == null) return;
        try { m.invoke(disguise, value); }
        catch (ReflectiveOperationException ignored) {}
    }

    private static void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (SpawnCrabBehavior.class) {
            if (reflectionInited) return;
            setBuriedMethod = ReflectionHelper.publicMethod(entityClass, "setBuried", boolean.class);
            isBuriedMethod = ReflectionHelper.publicMethod(entityClass, "isBuried");
            setBuryingCooldownMethod = ReflectionHelper.publicMethod(entityClass, "setBuryingCooldown", int.class);
            buryStateField = ReflectionHelper.declaredFieldOfType(entityClass, "buryAnimationState", AnimationState.class);
            emergeStateField = ReflectionHelper.declaredFieldOfType(entityClass, "emergeAnimationState", AnimationState.class);
            buryTimerField = ReflectionHelper.declaredFieldOfType(entityClass, "buryTimer", int.class);
            reflectionInited = true;
        }
    }
}
