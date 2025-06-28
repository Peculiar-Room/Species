package com.ninni.species.mixin;

import com.ninni.species.server.entity.mob.update_1.Stackatick;
import com.ninni.species.server.item.util.HasImportantInteraction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Mob.class)
public abstract class MobMixin extends LivingEntity {

    protected MobMixin(EntityType<? extends LivingEntity> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "checkAndHandleImportantInteractions", at = @At("HEAD"), cancellable = true)
    private void S$checkAndHandleImportantInteractions(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        InteractionResult interactionResult;
        ItemStack itemStack = player.getItemInHand(hand);

        if (itemStack.getItem() instanceof HasImportantInteraction && (interactionResult = itemStack.interactLivingEntity(player, this, hand)).consumesAction()) {
            cir.setReturnValue(interactionResult);
        }
    }

    @Inject(method = "interact", at = @At("HEAD"), cancellable = true)
    private void S$interact(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (this.getVehicle() instanceof Stackatick && player.isSecondaryUseActive()) {
            this.stopRiding();
            cir.setReturnValue(InteractionResult.SUCCESS);
        }
    }
}
