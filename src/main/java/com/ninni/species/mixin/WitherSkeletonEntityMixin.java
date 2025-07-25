package com.ninni.species.mixin;

import com.ninni.species.server.entity.mob.update_1.Wraptor;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.goal.AvoidEntityGoal;
import net.minecraft.world.entity.monster.AbstractSkeleton;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WitherSkeleton.class)
public abstract class WitherSkeletonEntityMixin extends AbstractSkeleton {
    private WitherSkeletonEntityMixin(EntityType<? extends AbstractSkeleton> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "registerGoals", at = @At("TAIL"))
    private void onInitGoals(CallbackInfo ci) {
        this.goalSelector.addGoal(4, new AvoidEntityGoal<>(this, Wraptor.class, 10.0F, 1.D, 1.25D));
    }
}
