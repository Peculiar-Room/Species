package com.ninni.species.entity.ai;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.mojang.datafixers.util.Pair;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.entity.ai.tasks.RoarAttack;
import com.ninni.species.entity.ai.tasks.StompAttack;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.AnimalPanic;
import net.minecraft.world.entity.ai.behavior.DoNothing;
import net.minecraft.world.entity.ai.behavior.LookAtTargetSink;
import net.minecraft.world.entity.ai.behavior.MoveToTargetSink;
import net.minecraft.world.entity.ai.behavior.RandomStroll;
import net.minecraft.world.entity.ai.behavior.RunOne;
import net.minecraft.world.entity.ai.behavior.SetEntityLookTargetSometimes;
import net.minecraft.world.entity.ai.behavior.SetWalkTargetFromLookTarget;
import net.minecraft.world.entity.ai.behavior.StartAttacking;
import net.minecraft.world.entity.ai.behavior.StopAttackingIfTargetInvalid;
import net.minecraft.world.entity.ai.behavior.declarative.BehaviorBuilder;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.schedule.Activity;

import java.util.function.Predicate;

public class CruncherAi {

    public static Brain<Cruncher> makeBrain(Brain<Cruncher> brain) {
        initCoreActivity(brain);
        initIdleActivity(brain);
        initRetreatActivity(brain);
        brain.setCoreActivities(ImmutableSet.of(Activity.CORE));
        brain.setDefaultActivity(Activity.IDLE);
        brain.useDefaultActivity();
        return brain;
    }

    private static void initCoreActivity(Brain<Cruncher> brain) {
        brain.addActivity(Activity.CORE, 0, ImmutableList.of(
                new AnimalPanic(1.4F),
                new LookAtTargetSink(45, 90),
                new MoveToTargetSink() {
                    @Override
                    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Mob mob) {
                        return mob instanceof Cruncher cruncher && !cruncher.canAttack();
                    }
                }
        ));
    }

    private static void initIdleActivity(Brain<Cruncher> brain) {
        brain.addActivity(Activity.IDLE, ImmutableList.of(
                Pair.of(0, StartAttacking.create(cruncher -> cruncher.getBrain().getMemory(MemoryModuleType.NEAREST_ATTACKABLE))),
                Pair.of(1, StartAttacking.create(Cruncher::getHurtBy)),
                Pair.of(2, new RunOne<>(
                        ImmutableList.of(
                                Pair.of(new DoNothing(30, 60), 1),
                                Pair.of(RandomStroll.stroll(1.0F), 2)
                        )))));
    }

    private static void initRetreatActivity(Brain<Cruncher> brain) {
        brain.addActivityWithConditions(
                Activity.FIGHT,
                ImmutableList.of(
                        Pair.of(0, StopAttackingIfTargetInvalid.create()),
                        Pair.of(1, new RoarAttack()),
                        Pair.of(2, new StompAttack())
                ),
                ImmutableSet.of(
                        Pair.of(MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT)
                )
        );
    }

    public static void updateActivity(Cruncher cruncher) {
        cruncher.getBrain().setActiveActivityToFirstValid(ImmutableList.of(Activity.FIGHT, Activity.IDLE));
    }

}
