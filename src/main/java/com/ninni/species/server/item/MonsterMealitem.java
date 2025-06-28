package com.ninni.species.server.item;

import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.registry.SpeciesStatusEffects;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class MonsterMealitem extends Item {

    public MonsterMealitem() {
        super(new Item.Properties().food(new FoodProperties.Builder().nutrition(3).saturationMod(1.2F).alwaysEat().build()));
    }

    @Override
    public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entity) {
        if (level instanceof ServerLevel) {
            int i = level.random.nextInt(4);
            MobEffect effect;
            int durationInSeconds;
            switch (i) {
                default -> {
                    effect = SpeciesStatusEffects.COMBUSTION.get();
                    durationInSeconds = 180;
                }
                case 1 -> {
                    effect = SpeciesStatusEffects.IRON_WILL.get();
                    durationInSeconds = 30;
                }
                case 2 -> {
                    effect = SpeciesStatusEffects.TANKED.get();
                    durationInSeconds = 20;
                }
                case 3 -> {
                    effect = SpeciesStatusEffects.SNATCHED.get();
                    durationInSeconds = 10;
                }
            }
            entity.addEffect(new MobEffectInstance(effect, 20 * durationInSeconds, 0));
            level.playSound(null, entity.getX(), entity.getY(), entity.getZ(), SpeciesSoundEvents.MONSTER_MEAL_APPLY.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        }
        return super.finishUsingItem(stack, level, entity);
    }


    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.literal(""));
        list.add(Component.translatable("item.species.whenEaten").withStyle(ChatFormatting.DARK_PURPLE));
        list.add(Component.literal(" ").append(Component.translatable("item.species.monster_meal.desc.effect").withStyle(Style.EMPTY.withColor(0xe72a8b))));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }

    @Override
    public SoundEvent getEatingSound() {
        return SpeciesSoundEvents.WICKED_WAX_EAT.get();
    }
}
