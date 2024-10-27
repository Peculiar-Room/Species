package com.ninni.species.item;

import com.ninni.species.entity.BirtEgg;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class BirtEggItem extends Item {

    public BirtEggItem(Item.Properties settings) {
        super(settings);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand) {
        ItemStack itemStack = user.getItemInHand(hand);
        user.getCooldowns().addCooldown(this, 20);
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SpeciesSoundEvents.ITEM_BIRT_EGG_THROW, SoundSource.PLAYERS, 0.5f, 0.6f / (world.getRandom().nextFloat() * 0.4f + 0.8f));
        if (!world.isClientSide) {
            BirtEgg entity = new BirtEgg(world, user);
            entity.setItem(itemStack);
            entity.shootFromRotation(user, user.getXRot(), user.getYRot(), 0.0f, 1f, 1.0f);
            world.addFreshEntity(entity);
        }

        user.awardStat(Stats.ITEM_USED.get(this));
        if (!user.getAbilities().instabuild) {
            itemStack.shrink(1);
        }
        return InteractionResultHolder.sidedSuccess(itemStack, world.isClientSide);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.species.birt_egg.desc.birtd").withStyle(ChatFormatting.RED));
        list.add(Component.literal(""));
        list.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
        list.add(Component.translatable("item.species.birt_egg.desc.effect").withStyle(ChatFormatting.RED));

        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}