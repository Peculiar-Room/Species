package com.ninni.species.server.item;

import com.ninni.species.server.item.util.HasImportantInteraction;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.block.Block;

public class IchorBottle extends BlockItem implements HasImportantInteraction {

    public IchorBottle(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack itemStack, Player player, LivingEntity livingEntity, InteractionHand interactionHand) {
        if (livingEntity instanceof Mob mob) {
            if (mob.getHealth() < mob.getMaxHealth()) {
                if (!player.getAbilities().instabuild) {
                    itemStack.shrink(1);
                    ItemStack glassBottle = new ItemStack(Items.GLASS_BOTTLE);
                    if (!player.getInventory().add(glassBottle)) {
                        player.drop(glassBottle, false);
                    }
                }
                mob.heal(mob.getMaxHealth()/4);
                mob.playSound(SpeciesSoundEvents.ICHOR_BOTTLE.get(), 1,1 );
                if (livingEntity.level() instanceof ServerLevel serverLevel) {
                    for (int i = 0; i < 7; ++i) {
                        double d = livingEntity.getRandom().nextGaussian() * 0.02;
                        double e = livingEntity.getRandom().nextGaussian() * 0.02;
                        double f = livingEntity.getRandom().nextGaussian() * 0.02;
                        serverLevel.sendParticles(ParticleTypes.HEART, livingEntity.getRandomX(1.0D), livingEntity.getRandomY() + 0.5D, livingEntity.getRandomZ(1.0D), 0, d, e, f, 1);
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(itemStack, player, livingEntity, interactionHand);
    }

    public String getDescriptionId() {
        return this.getOrCreateDescriptionId();
    }


    public InteractionResult useOn(UseOnContext useOnContext) {
        InteractionResult interactionResult = super.useOn(useOnContext);
        Player player = useOnContext.getPlayer();
        if (interactionResult.consumesAction() && player != null && !player.isCreative()) {
            InteractionHand interactionHand = useOnContext.getHand();
            if (player.getItemInHand(interactionHand).getCount() == 0) {
                player.setItemInHand(interactionHand, Items.GLASS_BOTTLE.getDefaultInstance());
            } else {
                if (!player.addItem(Items.GLASS_BOTTLE.getDefaultInstance())) {
                    player.drop(Items.GLASS_BOTTLE.getDefaultInstance(), true);
                }
            }
        }

        return interactionResult;
    }
}