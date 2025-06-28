package com.ninni.species.mixin;

import com.ninni.species.server.entity.util.AbstractArrowAccess;
import com.ninni.species.server.item.CrankbowItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;


@Mixin(AbstractArrow.class)
public abstract class AbstractArrowMixin extends Projectile implements AbstractArrowAccess {
    @Shadow public AbstractArrow.Pickup pickup;
    @Shadow protected abstract ItemStack getPickupItem();
    @Unique private boolean ignoresImmunityFrame;

    protected AbstractArrowMixin(EntityType<? extends Projectile> p_37248_, Level p_37249_) {
        super(p_37248_, p_37249_);
    }

    @Inject(at = @At("HEAD"), method = "tryPickup", cancellable = true)
    private void S$tryPickup(Player player, CallbackInfoReturnable<Boolean> cir) {
        ItemStack stack = player.getMainHandItem();
        if (stack.getItem() instanceof CrankbowItem && CrankbowItem.getContentWeight(stack) < CrankbowItem.getMaxWeight(stack)) {
            cir.cancel();
            switch (this.pickup) {
                case ALLOWED -> {
                    CrankbowItem.add(stack,this.getPickupItem());
                    cir.setReturnValue(true);
                }
                case CREATIVE_ONLY -> cir.setReturnValue(player.getAbilities().instabuild);
                default -> cir.setReturnValue(false);
            }
        }
    }

    @Inject(method = "onHitEntity", at = @At("TAIL"))
    private void S$onHitEntity(EntityHitResult result, CallbackInfo ci) {
        if (this.ignoresImmunityFrame) {
            Entity entity = result.getEntity();
            if (entity instanceof LivingEntity living) {
                living.hurtMarked = true;
                living.invulnerableTime = 0;
            }
        }
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    private void S$readAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        this.ignoresImmunityFrame = tag.getBoolean("IgnoresImmunityFrame");
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    private void S$addAdditionalSaveData(CompoundTag tag, CallbackInfo ci) {
        tag.putBoolean("IgnoresImmunityFrame", this.ignoresImmunityFrame);
    }

    @Override
    public boolean ignoresImmunityFrame() {
        return ignoresImmunityFrame;
    }

    @Override
    public void setTgnoreImmunityFrame(boolean ignore) {
        this.ignoresImmunityFrame = ignore;
    }
}
