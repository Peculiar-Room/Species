package com.ninni.species.server.item;

import com.ninni.species.server.item.util.HasImportantInteraction;
import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.server.criterion.SpeciesCriterion;
import com.ninni.species.server.entity.mob.update_2.Springling;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.registry.SpeciesTags;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class YouthPotion extends Item implements HasImportantInteraction {

    public YouthPotion(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if (livingEntity instanceof AgeableMob mob && !livingEntity.getType().is(SpeciesTags.ALWAYS_ADULT)) {

            if (!player.getAbilities().instabuild) {
                itemStack.shrink(1);
                ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);
                if (!player.getInventory().add(glassBottle)) {
                    player.drop(glassBottle, false);
                }
            }
            if (!mob.isBaby()) {
                mob.setBaby(true);
                if (player instanceof ServerPlayer serverPlayer) SpeciesCriterion.TURN_MOB_INTO_BABY.trigger(serverPlayer);
                mob.playSound(SpeciesSoundEvents.YOUTH_POTION_BABY.get(), 1, 1);
                if (livingEntity.level() instanceof ServerLevel serverLevel) {
                    double d = livingEntity.getRandom().nextGaussian() * 0.02;
                    double e = livingEntity.getRandom().nextGaussian() * 0.02;
                    double f = livingEntity.getRandom().nextGaussian() * 0.02;
                    serverLevel.sendParticles(SpeciesParticles.YOUTH_POTION.get(), livingEntity.getRandomX(1.0D), livingEntity.getRandomY() + 0.5D, livingEntity.getRandomZ(1.0D), 0, d, e, f, 1);
                }
            }
            if (mob instanceof Springling springling) {
                springling.setExtendedAmount(0);
                springling.setTame(false);
                springling.setOwnerUUID(null);
            }

            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }
}