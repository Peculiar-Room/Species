package com.ninni.species.server.item;

import com.ninni.species.registry.SpeciesNetwork;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.entity.mob.update_3.Harpoon;
import com.ninni.species.server.packet.HarpoonSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;

public class HarpoonItem extends Item {
    public HarpoonItem(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        if (!level.isClientSide) {
            ItemStack stack = player.getItemInHand(hand);
            Harpoon projectile = new Harpoon(level, player);
            projectile.shoot(0, 1, 0, 2.0F, 0.0F);
            if (player instanceof ServerPlayer serverPlayer) {
                PacketDistributor.sendToPlayer(serverPlayer, new HarpoonSyncPacket(projectile.getId()));
                //SpeciesNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> serverPlayer), new HarpoonSyncPacket(projectile.getId()));
            }
            level.addFreshEntity(projectile);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SpeciesSoundEvents.HARPOON_THROWN.get(), player.getSoundSource(), 1.0F, 1.0F);
            stack.hurtAndBreak(1, player, player.getEquipmentSlotForItem(player.getItemInHand(hand)));
        }
        player.startUsingItem(hand);

        return InteractionResultHolder.sidedSuccess(player.getItemInHand(hand), level.isClientSide());
    }

    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }

}