package com.ninni.species.mixin;

import com.ninni.species.registry.SpeciesStatusEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureElement;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public abstract class ItemMixin implements FeatureElement, ItemLike, net.minecraftforge.common.extensions.IForgeItem {


    @Inject(method = "use", at = @At("HEAD"), cancellable = true)
    public void makePlayersNotEat(Level level, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResultHolder<ItemStack>> cir) {
        if (player != null && player.hasEffect(SpeciesStatusEffects.BLOODLUST.get())) {
            cir.cancel();
            ItemStack itemstack = player.getItemInHand(hand);
            if (itemstack.isEdible()) {
                player.displayClientMessage(Component.translatable("effect.species.bloodlust.reason").withStyle(ChatFormatting.DARK_RED), true);
                player.getCooldowns().addCooldown(this.asItem(), 60);

                cir.setReturnValue(InteractionResultHolder.success(player.getItemInHand(hand)));
            } else {
                cir.setReturnValue(InteractionResultHolder.pass(player.getItemInHand(hand)));
            }
        }
    }
}
