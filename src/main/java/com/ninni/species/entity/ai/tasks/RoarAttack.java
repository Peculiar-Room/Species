package com.ninni.species.entity.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.registry.SpeciesMemoryModuleTypes;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.behavior.BehaviorUtils;
import net.minecraft.world.entity.ai.behavior.EntityTracker;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class RoarAttack extends Behavior<Cruncher> {
    private static final Cruncher.CruncherState cruncherState = Cruncher.CruncherState.ROAR;

    public RoarAttack() {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                SpeciesMemoryModuleTypes.ROAR_CHARGING, MemoryStatus.VALUE_ABSENT
        ), cruncherState.getDuration());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Cruncher livingEntity) {
        return livingEntity.canAttack() && !livingEntity.isTargetClose();
    }

    @Override
    protected boolean canStillUse(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        return true;
    }

    @Override
    protected void start(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        if (livingEntity.getState() == Cruncher.CruncherState.IDLE) {
            livingEntity.transitionTo(cruncherState);
        }
        livingEntity.getBrain().setMemoryWithExpiry(SpeciesMemoryModuleTypes.ROAR_CHARGING, Unit.INSTANCE, 64);
    }

    @Override
    protected void tick(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        Brain<Cruncher> brain = livingEntity.getBrain();
        LivingEntity target = brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);

        if (target == null) return;

        livingEntity.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());

        if (brain.getMemory(SpeciesMemoryModuleTypes.ROAR_CHARGING).isPresent()) return;

        livingEntity.playSound(SoundEvents.WARDEN_ROAR, 2.0F, 1.0F);

        for (Player player : serverLevel.getEntitiesOfClass(Player.class, livingEntity.getBoundingBox().inflate(13.0D))) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 300));
            player.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 300));
        }

        brain.setMemoryWithExpiry(SpeciesMemoryModuleTypes.ROAR_CHARGING, Unit.INSTANCE, 96);
    }

    @Override
    protected void stop(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        if (livingEntity.getState() == cruncherState) {
            livingEntity.transitionTo(Cruncher.CruncherState.IDLE);
        }
    }
}
