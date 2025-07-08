package com.ninni.species.server.item;

import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.registry.SpeciesStatusEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class BirtdayCakeSliceItem extends Item {

    public BirtdayCakeSliceItem(Properties settings) {
        super(settings);
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (!livingEntity.hasEffect(SpeciesStatusEffects.BIRTD.get())) level.playSound(null, livingEntity.blockPosition(), SpeciesSoundEvents.ENTITY_BIRTD.get(), livingEntity.getSoundSource(), 1, 1);
        return super.finishUsingItem(stack, level, livingEntity);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.species.birtday_cake_slice.desc.birtd").withStyle(ChatFormatting.RED));
        list.add(Component.literal(""));
        list.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
        list.add(Component.translatable("item.species.birtday_cake_slice.desc.effect").withStyle(ChatFormatting.RED));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}