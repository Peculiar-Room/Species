package com.ninni.species.server.item;

import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WickedDopeItem extends Item {

    public WickedDopeItem() {
        super(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(2).alwaysEat().build()));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {

        if (player.getActiveEffects().isEmpty()) {
            ItemStack itemstack = player.getItemInHand(hand);
            player.displayClientMessage(Component.translatable("item.species.wicked_dope.no_effects").withStyle(Style.EMPTY.withColor(0xe72a8b)), true);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SpeciesSoundEvents.WICKED_DOPE_FAIL.get(), player.getSoundSource(), 1.0F, 1.0F);
            player.getCooldowns().addCooldown(this.asItem(), 60);
            return InteractionResultHolder.pass(itemstack);
        }

        return super.use(level, player, hand);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (entity instanceof Player player && !player.getActiveEffects().isEmpty()) {
            if (!player.isCreative()) player.getCooldowns().addCooldown(this, 20 * 60 * 2);
            player.getActiveEffects().forEach(mobEffectInstance -> player.addEffect(new MobEffectInstance(mobEffectInstance.getEffect(), mobEffectInstance.getDuration(), mobEffectInstance.getAmplifier() + 1)));
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SpeciesSoundEvents.WICKED_DOPE_BOOST.get(), entity.getSoundSource(), 1.0F, 1.0F);
        }
        return super.finishUsingItem(stack, level, entity);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.literal(""));
        list.add(Component.translatable("item.species.whenEaten").withStyle(ChatFormatting.DARK_PURPLE));
        list.add(Component.literal(" ").append(Component.translatable("item.species.wicked_dope.desc.effect").withStyle(Style.EMPTY.withColor(0xe72a8b))));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }

    @Override
    public SoundEvent getEatingSound() {
        return SpeciesSoundEvents.WICKED_WAX_EAT.get();
    }
}
