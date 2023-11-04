package com.ninni.species.mixin;

import com.ninni.species.entity.Springling;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.effect.MobEffectUtil;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
public abstract class PlayerMixin extends LivingEntity {
    @Shadow @Final private Inventory inventory;

    protected PlayerMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }


    @Inject(method = "getDestroySpeed", at = @At("HEAD"), cancellable = true)
    public void springlingDestroySpeed(BlockState blockState, CallbackInfoReturnable<Float> cir) {
        if (this.getVehicle() instanceof Springling) {
            cir.cancel();
            float f = this.inventory.getDestroySpeed(blockState);
            if (f > 1.0f) {
                int i = EnchantmentHelper.getBlockEfficiency(this);
                ItemStack itemStack = this.getMainHandItem();
                if (i > 0 && !itemStack.isEmpty()) {
                    f += (float) (i * i + 1);
                }
            }
            if (MobEffectUtil.hasDigSpeed(this)) {
                f *= 1.0f + (float) (MobEffectUtil.getDigSpeedAmplification(this) + 1) * 0.2f;
            }
            if (this.hasEffect(MobEffects.DIG_SLOWDOWN)) {
                f *= (switch (this.getEffect(MobEffects.DIG_SLOWDOWN).getAmplifier()) {
                    case 0 -> 0.3f;
                    case 1 -> 0.09f;
                    case 2 -> 0.0027f;
                    default -> 8.1E-4f;
                });
            }
            if (this.isEyeInFluid(FluidTags.WATER) && !EnchantmentHelper.hasAquaAffinity(this)) {
                f /= 5.0f;
            }
            if (!this.getVehicle().onGround()) {
                f /= 5.0f;
            }
            cir.setReturnValue(f);
        }
    }

}
