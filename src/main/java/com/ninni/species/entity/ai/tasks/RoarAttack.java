package com.ninni.species.entity.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.registry.SpeciesMemoryModuleTypes;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;
import net.minecraft.world.entity.player.Player;
import com.ninni.species.registry.*;

public class RoarAttack extends Behavior<Cruncher> {
    private static final Cruncher.CruncherState cruncherState = Cruncher.CruncherState.ROAR;
    private final UniformInt cooldown = UniformInt.of(300, 600);

    public RoarAttack() {
        super(ImmutableMap.of(
                MemoryModuleType.ATTACK_TARGET, MemoryStatus.VALUE_PRESENT,
                SpeciesMemoryModuleTypes.ROAR_CHARGING, MemoryStatus.VALUE_ABSENT,
                SpeciesMemoryModuleTypes.ROAR_COOLDOWN, MemoryStatus.VALUE_ABSENT
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
        livingEntity.transitionTo(cruncherState);
        livingEntity.playSound(SpeciesSoundEvents.CRUNCHER_ROAR, 1.0F, 1.0F);
        livingEntity.getBrain().setMemoryWithExpiry(SpeciesMemoryModuleTypes.ROAR_CHARGING, Unit.INSTANCE, 12);
    }

    @Override
    protected void tick(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        Brain<Cruncher> brain = livingEntity.getBrain();
        LivingEntity target = brain.getMemory(MemoryModuleType.ATTACK_TARGET).orElse(null);

        if (target == null) return;

        livingEntity.lookAt(EntityAnchorArgument.Anchor.EYES, target.position());
        brain.eraseMemory(MemoryModuleType.WALK_TARGET);

        if (brain.getMemory(SpeciesMemoryModuleTypes.ROAR_CHARGING).isPresent()) return;

        for (LivingEntity entity : serverLevel.getEntitiesOfClass(LivingEntity.class, livingEntity.getBoundingBox().inflate(13.0D))) {

            boolean isCreative = entity instanceof Player player && player.getAbilities().instabuild;
            boolean isCruncher = entity instanceof Cruncher;

            if (isCreative || isCruncher) continue;

            entity.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 20 * 10, 1));
            entity.addEffect(new MobEffectInstance(MobEffects.WEAKNESS, 20 * 20));
        }

        brain.setMemoryWithExpiry(SpeciesMemoryModuleTypes.ROAR_CHARGING, Unit.INSTANCE, 96);
    }

    @Override
    protected void stop(ServerLevel serverLevel, Cruncher livingEntity, long l) {
        if (livingEntity.getState() == cruncherState) {
            livingEntity.transitionTo(Cruncher.CruncherState.IDLE);
            livingEntity.getBrain().setMemoryWithExpiry(SpeciesMemoryModuleTypes.ROAR_COOLDOWN, Unit.INSTANCE, this.cooldown.sample(livingEntity.getRandom()));
        }
    }
}
