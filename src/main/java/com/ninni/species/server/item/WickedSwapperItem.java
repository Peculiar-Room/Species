package com.ninni.species.server.item;

import com.ninni.species.server.entity.mob.update_3.WickedSwapperProjectile;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;

public class WickedSwapperItem extends Item {

    public WickedSwapperItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level  level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        level.playSound(null, player.getX(), player.getY(), player.getZ(), SpeciesSoundEvents.WICKED_SWAPPER_THROW.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        player.awardStat(Stats.ITEM_USED.get(this));
        player.getCooldowns().addCooldown(this, 20);

        if (!level.isClientSide) {
            WickedSwapperProjectile projectile = new WickedSwapperProjectile(level, player);
            projectile.setPos(player.getX(), player.getEyeY() - 0.1, player.getZ());
            projectile.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 2, 1.0F);
            level.addFreshEntity(projectile);
        }
        if (!player.getAbilities().instabuild) stack.shrink(1);

        return InteractionResultHolder.success(stack);
    }
}
