package com.ninni.species.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ninni.species.entity.LimpetEntity;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.RunSometimes;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTarget;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetAwayFrom;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.schedule.Activity;

public class LimpetAi {

    public static Brain<LimpetEntity> makeBrain(Brain<LimpetEntity> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initRetreatActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<LimpetEntity> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new AnimalPanic(1.4F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink()
        ));
    }

    private static void initIdleActivity(Brain<LimpetEntity> brain) {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(0, new RunSometimes<LivingEntity>(new SetEntityLookTarget(EntityType.PLAYER, 6.0f), UniformInt.of(30, 60))),
                Pair.of(1, new RunOne<>(ImmutableList.of(
                        Pair.of(new RandomStroll(1.0f), 2),
                        Pair.of(new SetWalkTargetFromLookTarget(1.0f, 3), 2),
                        Pair.of(new DoNothing(30, 60), 1))
                ))));
    }

    private static void initRetreatActivity(Brain<LimpetEntity> brain) {
        brain.addActivityAndRemoveMemoryWhenStopped(
                Activity.AVOID,
                10,
                ImmutableList.of(
                        SetWalkTargetAwayFrom.entity(MemoryModuleType.AVOID_TARGET, 2.0F, 20, true),
                        new RunSometimes<LivingEntity>(new SetEntityLookTarget(8.0f), UniformInt.of(30, 60))
                ),
                MemoryModuleType.AVOID_TARGET
        );
    }

    public static void updateActivity(LimpetEntity limpet) {
        limpet.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.AVOID, Activity.IDLE));
    }

}
