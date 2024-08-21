package com.ninni.species.item;

import com.ninni.species.entity.Springling;
import com.ninni.species.registry.SpeciesParticles;
import net.minecraft.server.level.ServerLevel;
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

            if (mob instanceof Springling springling) {
                springling.setExtendedAmount(0);
                springling.setTame(false);
                springling.setOwnerUUID(null);
            }

            if (livingEntity.level() instanceof ServerLevel serverLevel) {
                for (int i = 0; i < 3; ++i) {
                    double d = livingEntity.getRandom().nextGaussian() * 0.02;
                    double e = livingEntity.getRandom().nextGaussian() * 0.02;
                    double f = livingEntity.getRandom().nextGaussian() * 0.02;
                    serverLevel.sendParticles(SpeciesParticles.YOUTH_POTION, livingEntity.getRandomX(1.0D), livingEntity.getRandomY() + 0.5D, livingEntity.getRandomZ(1.0D), 0, d, e, f, 1);
                }
            }
            return InteractionResult.sidedSuccess(player.level().isClientSide);
        }
        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}
