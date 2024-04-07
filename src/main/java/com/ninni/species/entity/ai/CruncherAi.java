package com.ninni.species.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.entity.ai.tasks.RoarAttack;
import com.ninni.species.entity.ai.tasks.Sit;
import com.ninni.species.entity.ai.tasks.SpitPellet;
import com.ninni.species.entity.ai.tasks.StompAttack;
import com.ninni.species.registry.SpeciesMemoryModuleTypes;
import com.ninni.species.registry.SpeciesSensorTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.PositionTracker;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromAttackTargetIfTargetOutOfReach;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StayCloseToTarget;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.ai.sensing.Sensor;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.level.Level;

import java.util.Optional;
import java.util.UUID;
import java.util.function.Predicate;

public class CruncherAi {
    public static final ImmutableList<SensorType<? extends Sensor<? super Cruncher>>> SENSOR_TYPES = ImmutableList.of(
            SensorType.NEAREST_LIVING_ENTITIES,
            SensorType.NEAREST_PLAYERS,
            SensorType.HURT_BY,
            SensorType.NEAREST_PLAYERS,
            SpeciesSensorTypes.CRUNCHER_ATTACK_ENTITY_SENSOR
    );
    public static final ImmutableList<MemoryModuleType<?>> MEMORY_TYPES = ImmutableList.of(
            MemoryModuleType.LOOK_TARGET,
            MemoryModuleType.NEAREST_VISIBLE_LIVING_ENTITIES,
            MemoryModuleType.WALK_TARGET,
            MemoryModuleType.CANT_REACH_WALK_TARGET_SINCE,
            MemoryModuleType.PATH,
            MemoryModuleType.IS_PANICKING,
            MemoryModuleType.AVOID_TARGET,
            MemoryModuleType.ATTACK_TARGET,
            MemoryModuleType.NEAREST_LIVING_ENTITIES,
            MemoryModuleType.NEAREST_ATTACKABLE,
            MemoryModuleType.LIKED_PLAYER,
            MemoryModuleType.NEAREST_PLAYERS,
            MemoryModuleType.NEAREST_VISIBLE_PLAYER,
            MemoryModuleType.NEAREST_VISIBLE_ATTACKABLE_PLAYER,
            SpeciesMemoryModuleTypes.ROAR_CHARGING,
            SpeciesMemoryModuleTypes.STOMP_CHARGING,
            SpeciesMemoryModuleTypes.SPIT_CHARGING,
            SpeciesMemoryModuleTypes.ROAR_COOLDOWN
    );

    public static Brain<Cruncher> makeBrain(Brain<Cruncher> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initFightActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<Cruncher> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new Sit(),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink() {
                    @Override
                    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Mob mob) {
                        if (mob instanceof Cruncher cruncher && cruncher.cannotWalk()) return false;

                        return super.checkExtraStartConditions(serverLevel, mob);
                    }
                }
        ));
    }

    private static void initIdleActivity(Brain<Cruncher> brain) {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(0, StayCloseToTarget.create(CruncherAi::getLikedTracker, CruncherAi::isPassive, 2, 4, 1.0F)),
                Pair.of(1, StartAttacking.create(Predicate.not(CruncherAi::isPassive), cruncher -> cruncher.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
                Pair.of(2, StartAttacking.create(Predicate.not(CruncherAi::isPassive), Cruncher::getHurtBy)),
                Pair.of(3, new SpitPellet()),
                Pair.of(4, new RunOne<>(
                        ImmutableList.of(
                                Pair.of(RandomStroll.stroll(1.0F), 1),
                                Pair.of(new DoNothing(30, 60), 1)
                        )))));
    }

    private static void initFightActivity(Brain<Cruncher> brain) {
        brain.addActivityWithConditions(
                Activity.FIGHT,
                ImmutableList.of(
                        Pair.of(0, StopAttackingIfTargetInvalid.create()),
                        Pair.of(1, new RoarAttack()),
                        Pair.of(2, SetWalkTargetFromAttackTargetIfTargetOutOfReach.create(1.2F)),
                        Pair.of(3, new StompAttack())
                ),
                ImmutableSet.of(
                        Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)
                )
        );
    }

    private static boolean isPassive(LivingEntity livingEntity) {
        return livingEntity instanceof Cruncher cruncher && cruncher.getHunger() <= 0;
    }

    private static Optional<PositionTracker> getLikedTracker(LivingEntity livingEntity) {
        return CruncherAi.getLikedPlayer(livingEntity).map(serverPlayer -> new EntityTracker(serverPlayer, true));
    }

    public static Optional<ServerPlayer> getLikedPlayer(LivingEntity livingEntity) {
        Level level = livingEntity.level();
        if (!level.isClientSide() && level instanceof ServerLevel serverLevel) {
            Optional<UUID> optional = livingEntity.getBrain().getMemory(MemoryModuleType.LIKED_PLAYER);
            if (optional.isPresent()) {
                Entity entity = serverLevel.getEntity(optional.get());
                if (entity instanceof ServerPlayer serverPlayer) {
                    if ((serverPlayer.gameMode.isSurvival() || serverPlayer.gameMode.isCreative()) && serverPlayer.closerThan(livingEntity, 64.0)) {
                        return Optional.of(serverPlayer);
                    }
                }
                return Optional.empty();
            }
        }
        return Optional.empty();
    }

    public static void updateActivity(Cruncher cruncher) {
        cruncher.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
    }

}
