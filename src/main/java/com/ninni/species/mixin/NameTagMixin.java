package com.ninni.species.mixin;

import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.server.entity.mob.update_3.CliffHanger;
import com.ninni.species.server.entity.mob.update_3.Hanger;
import com.ninni.species.server.entity.mob.update_3.LeafHanger;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.NameTagItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(NameTagItem.class)
public abstract class NameTagMixin extends Item {
    public NameTagMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "interactLivingEntity", at = @At("HEAD"), cancellable = true)
    private void S$interact(ItemStack stack, Player player, LivingEntity livingEntity, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        if (stack.hasCustomHoverName() && livingEntity instanceof Hanger && (stack.getHoverName().getString().equals("Dinnerbone") || stack.getHoverName().getString().equals("Grumm"))) {
            if (!player.level().isClientSide && livingEntity.isAlive()) {
                if (livingEntity instanceof LeafHanger hanger) {
                    hanger.convertTo(SpeciesEntities.CLIFF_HANGER.get(), false);
                } else if (livingEntity instanceof CliffHanger hanger) {
                    hanger.convertTo(SpeciesEntities.LEAF_HANGER.get(), false);
                }
                stack.shrink(1);
            }
            cir.setReturnValue(InteractionResult.sidedSuccess(player.level().isClientSide));
        }
    }
}
