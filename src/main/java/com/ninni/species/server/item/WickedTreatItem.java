package com.ninni.species.server.item;

import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.registry.SpeciesStatusEffects;
import com.ninni.species.server.item.util.HasImportantInteraction;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.OwnableEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class WickedTreatItem extends Item implements HasImportantInteraction {
    public WickedTreatItem() {
        super(new Properties());
    }

    @Override
    public InteractionResult interactLivingEntity(ItemStack stack, Player player, LivingEntity livingEntity, InteractionHand hand) {
        if (livingEntity instanceof OwnableEntity ownableEntity && ownableEntity.getOwner() != null && ownableEntity.getOwner().is(player) && !player.getCooldowns().isOnCooldown(stack.getItem())) {
            livingEntity.addEffect(new MobEffectInstance(SpeciesStatusEffects.SNATCHED.get(), 45 * 20, 1));
            livingEntity.addEffect(new MobEffectInstance(SpeciesStatusEffects.IRON_WILL.get(), 45 * 20, 0));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.REGENERATION, 45 * 20, 0));
            livingEntity.addEffect(new MobEffectInstance(MobEffects.DAMAGE_BOOST, 20 * 20, 0));
            player.getCooldowns().addCooldown(stack.getItem(), 45 * 20);
            stack.shrink(1);

            if (player.level() instanceof ServerLevel serverLevel) {
                serverLevel.sendParticles(
                        SpeciesParticles.WICKED_FLAME.get(),
                        livingEntity.getX(), livingEntity.getY(0.6), livingEntity.getZ(),
                        20,
                        0.3, 0.3, 0.3,
                        1.0D
                );
            }
            livingEntity.playSound(SpeciesSoundEvents.WICKED_TREAT_APPLY.get());

            return InteractionResult.SUCCESS;
        }
        return super.interactLivingEntity(stack, player, livingEntity, hand);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {

        list.add(Component.translatable("item.species.wicked_treat.desc").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
        list.add(Component.literal(""));
        list.add(Component.translatable("item.species.wicked_treat.desc.snatched").withStyle(Style.EMPTY.withColor(0xe72a8b)));
        list.add(Component.translatable("item.species.wicked_treat.desc.iron_will").withStyle(Style.EMPTY.withColor(0xe72a8b)));
        list.add(Component.translatable("item.species.wicked_treat.desc.regeneration").withStyle(ChatFormatting.BLUE));
        list.add(Component.translatable("item.species.wicked_treat.desc.strength").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}
