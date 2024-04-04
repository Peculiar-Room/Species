package com.ninni.species.entity.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.registry.SpeciesMemoryModuleTypes;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

public class StompAttack extends Behavior<Cruncher> {
    private static final Cruncher.CruncherState cruncherState = Cruncher.CruncherState.STOMP;

    public StompAttack() {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                SpeciesMemoryModuleTypes.STOMP_CHARGING, MemoryStatus.VALUE_ABSENT
        ), cruncherState.getDuration());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel serverLevel, Cruncher livingEntity) {
        return livingEntity.canAttack() && livingEntity.isTargetClose();
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
        livingEntity.getBrain().setMemoryWithExpiry(SpeciesMemoryModuleTypes.STOMP_CHARGING, Unit.INSTANCE, 18);
    }

    @Override
    protected void tick(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        Brain<Cruncher> brain = livingEntity.getBrain();
        LivingEntity target = brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);

        if (target == null) return;

        livingEntity.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);

        if (brain.getMemory(SpeciesMemoryModuleTypes.STOMP_CHARGING).isPresent()) return;

        for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(6.0D))) {

            boolean flag = entity.getY() > livingEntity.getY() && entity.distanceTo(livingEntity) > 6;
            boolean self = entity instanceof Cruncher;

            if (self || flag) continue;

            float damage = 10.0F / (0.15F * entity.distanceTo(livingEntity));

            Vec3 vec3 = livingEntity.position().add(0.0, 1.6f, 0.0);
            Vec3 vec32 = entity.getEyePosition().subtract(vec3);
            Vec3 vec33 = vec32.normalize();
            entity.hurt(serverLevel.damageSources().sonicBoom(livingEntity), damage);
            double d = 0.5 * (1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double e = 2.5 * (1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            entity.push(vec33.x() * e, vec33.y() * d, vec33.z() * e);
        }
        livingEntity.playSound(SoundEvents.GENERIC_EXPLODE, 2.0F, 1.0F);
        livingEntity.getBrain().setMemoryWithExpiry(SpeciesMemoryModuleTypes.STOMP_CHARGING, Unit.INSTANCE, 42);
    }

    @Override
    protected void stop(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        if (livingEntity.getState() == cruncherState) {
            livingEntity.transitionTo(Cruncher.CruncherState.IDLE);
        }
    }
}
