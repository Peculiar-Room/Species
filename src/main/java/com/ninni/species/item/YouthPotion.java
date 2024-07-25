package com.ninni.species.item;

import com.ninni.species.mixin.AgeableMobMixin;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class YouthPotion extends Item {

    public YouthPotion(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if (livingEntity instanceof AgeableMob mob) {

            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
                ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!player.getInventory().add(glassBottle)) {
                    player.drop(glassBottle, false);
                }
            }

            if (!mob.isBaby()) {
                mob.setBaby(true);
            }

            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }
        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
