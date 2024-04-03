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

        if (brain.getMemory(SpeciesMemoryModuleTypes.STOMP_CHARGING).isPresent()) return;

        for (Player player : serverLevel.getEntitiesOfClass(Player.class, livingEntity.getBoundingBox().inflate(6.0D))) {

            if (player.getY() > livingEntity.getY() && player.distanceTo(livingEntity) > 4) continue;

            Vec3 vec3 = livingEntity.position().add(0.0, 1.6f, 0.0);
            Vec3 vec32 = player.getEyePosition().subtract(vec3);
            Vec3 vec33 = vec32.normalize();
            player.hurt(serverLevel.damageSources().sonicBoom(livingEntity), 5.0F);
            double d = 0.5 * (1.0 - player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            double e = 2.5 * (1.0 - player.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
            player.push(vec33.x() * e, vec33.y() * d, vec33.z() * e);
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
