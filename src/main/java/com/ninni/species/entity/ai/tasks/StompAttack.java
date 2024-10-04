package com.ninni.species.entity.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.species.client.particles.SpeciesParticles;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.init.SpeciesDamageTypes;
import com.ninni.species.init.SpeciesMemoryModuleTypes;
import com.ninni.species.init.SpeciesSoundEvents;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.phys.Vec3;

public class StompAttack extends Behavior<Cruncher> {
    private static final Cruncher.CruncherState cruncherState = Cruncher.CruncherState.STOMP;

    public StompAttack() {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                SpeciesMemoryModuleTypes.STOMP_CHARGING.get(), MemoryStatus.VALUE_ABSENT
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
        livingEntity.playSound(SpeciesSoundEvents.CRUNCHER_STOMP.get(), 2.0F, 1.0F);
        livingEntity.getBrain().setMemoryWithExpiry(SpeciesMemoryModuleTypes.STOMP_CHARGING.get(), Unit.INSTANCE, 14);
    }

    @Override
    protected void tick(ServerLevel serverLevel, Cruncher cruncher, long l) {
        Brain<Cruncher> brain = cruncher.getBrain();
        LivingEntity target = brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);

        if (target == null) return;

        cruncher.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);

        if (brain.getMemory(SpeciesMemoryModuleTypes.STOMP_CHARGING.get()).isPresent()) return;

        for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, cruncher.getBoundingBox().inflate(6.0D))) {

            boolean reachable = entity.getY() > cruncher.getY() && entity.distanceTo(cruncher) > 6;
            boolean self = entity instanceof Cruncher;

            if (self || reachable) continue;

            float damage = 8.0F / (0.15F * entity.distanceTo(cruncher));

            Vec3 vec3 = cruncher.position().add(0.0, 1.6f, 0.0);
            Vec3 vec32 = entity.getEyePosition().subtract(vec3);
            Vec3 vec33 = vec32.normalize();
            entity.hurt(serverLevel.damageSources().source(SpeciesDamageTypes.CRUNCH, cruncher), damage);
            double d = 0.35 * (1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double e = 2 * (1.0 - entity.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            entity.push(vec33.x() * e, vec33.y() * d, vec33.z() * e);
        }

        for (int i = 0; i <= (cruncher.getRandom().nextInt(50) + 80); i++) serverLevel.sendParticles(ParticleTypes.CLOUD, cruncher.getRandomX(2), cruncher.getY() + 0.25f, cruncher.getRandomZ(2), 1, 0.0, 0.0, 0.0,0.0);
        for (int i = 0; i <= (cruncher.getRandom().nextInt(20) + 20); i++) serverLevel.sendParticles(SpeciesParticles.ASCENDING_DUST.get(), cruncher.getRandomX(2), cruncher.getY() + 0.25f, cruncher.getRandomZ(2), 1, 0.0, 1.0, 0.0,0.0);

        cruncher.getBrain().setMemoryWithExpiry(SpeciesMemoryModuleTypes.STOMP_CHARGING.get(), Unit.INSTANCE, 42);
    }

    @Override
    protected void stop(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        if (livingEntity.getState() == cruncherState) {
            livingEntity.transitionTo(Cruncher.CruncherState.IDLE);
        }
    }
}