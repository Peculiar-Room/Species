package com.ninni.species.server.item;

import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SmokeBombItem extends Item {

    public SmokeBombItem(Properties properties) {
        super(properties);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity entity, int timeLeft) {
        if (entity instanceof Player player) {
            int chargeTime = this.getUseDuration(stack) - timeLeft;
            if (chargeTime < 10) {
                player.getCooldowns().addCooldown(this, 20);
                return;
            }

            level.playSound(null, player.getX(), player.getY(), player.getZ(), SpeciesSoundEvents.SMOKE_BOMB_USE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
            player.awardStat(Stats.ITEM_USED.get(this));

            if (!level.isClientSide) {
                if (level instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(SpeciesParticles.POOF.get(), entity.position().x, entity.position().y + 0.01, entity.position().z, 1, 0, 0, 0, 0.5F);
                    serverLevel.sendParticles(ParticleTypes.POOF, entity.position().x, entity.position().y + 1, entity.position().z, 100, 0, 0, 0, 0.15F);
                }
                player.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 20 * 15, 0, true, true));
                player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SPEED, 20 * 2, 2, true, true));

            }
            player.getCooldowns().addCooldown(this, 40);
            if (!player.getAbilities().instabuild) {
                stack.shrink(1);
            }
        }
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        player.startUsingItem(hand);
        level.playSound(null, player.getX(), player.getY(), player.getZ(), SpeciesSoundEvents.SMOKE_BOMB_CHARGE.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
        return InteractionResultHolder.consume(player.getItemInHand(hand));
    }

    @Override
    public int getUseDuration(ItemStack stack) {
        return 72000;
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.SPEAR;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.species.smoke_bomb.desc.invisibility").withStyle(ChatFormatting.BLUE));
        list.add(Component.translatable("item.species.smoke_bomb.desc.speed").withStyle(ChatFormatting.BLUE));
        list.add(Component.literal(""));
        list.add(Component.translatable("potion.whenDrank").withStyle(ChatFormatting.DARK_PURPLE));
        list.add(Component.translatable("item.species.smoke_bomb.desc.speed.effect").withStyle(ChatFormatting.BLUE));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}
