package com.ninni.species.server.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.criterion.SpeciesCriterion;
import com.ninni.species.server.entity.mob.update_3.Spectre;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ToolAction;
import net.minecraftforge.common.ToolActions;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpectraliburItem extends Item {
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public SpectraliburItem(Properties properties) {
        super(properties);

        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Weapon modifier", 8F, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier", -2.4F, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack mainHandItem = player.getMainHandItem();
        if (mainHandItem.is(this) && mainHandItem.hasTag() && mainHandItem.getTag().contains("Souls") && mainHandItem.getTag().getInt("Souls") > 0) {
            player.startUsingItem(hand);
            player.playSound(SpeciesSoundEvents.SPECTRALIBUR_START_CHARGING.get(), 1,1);
            return InteractionResultHolder.consume(mainHandItem);
        }
        return super.use(level, player, hand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int time) {
        super.onUseTick(level, livingEntity, stack, time);
        if (time < stack.getUseDuration()) {
            if (time % 10 == 0 && stack.hasTag() && stack.getTag().contains("Souls") && stack.getTag().getInt("Souls") > 0) {
                stack.getTag().putInt("Souls", Math.max(stack.getTag().getInt("Souls") - 1, 0));
                stack.getTag().putInt("UsingSouls", stack.getTag().getInt("UsingSouls") + 1);
                livingEntity.playSound(SpeciesSoundEvents.SPECTRALIBUR_USE_SOUL.get(), 1,0.75F + stack.getTag().getInt("UsingSouls") * 0.1F);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeUsed) {
        super.releaseUsing(stack, level, livingEntity, timeUsed);
        spawnSpectres(stack, level, livingEntity);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        super.onStopUsing(stack, entity, count);
        spawnSpectres(stack, entity.level(), entity);
    }

    public void spawnSpectres(ItemStack stack, Level level, LivingEntity livingEntity) {
        if (stack.hasTag() && stack.getTag().contains("UsingSouls") && stack.getTag().getInt("UsingSouls") > 0) {
            int usingSouls = stack.getTag().getInt("UsingSouls");
            if (level instanceof ServerLevel serverLevel && livingEntity instanceof Player player) {
                if (usingSouls == 1 || usingSouls == 3) Spectre.spawnSpectre(serverLevel, player, player.getOnPos().above(2), Spectre.Type.SPECTRE, true);
                if (usingSouls == 2 || usingSouls == 3 || usingSouls == 4) Spectre.spawnSpectre(serverLevel, player, player.getOnPos().above(2), Spectre.Type.JOUSTING_SPECTRE, true);
                if (usingSouls == 4) Spectre.spawnSpectre(serverLevel, player, player.getOnPos().above(2), Spectre.Type.JOUSTING_SPECTRE, true);
                if (usingSouls == 5) Spectre.spawnSpectre(serverLevel, player, player.getOnPos().above(2), Spectre.Type.HULKING_SPECTRE, true);
                Vec3 pos = livingEntity.position();
                serverLevel.sendParticles(SpeciesParticles.SPECTRALIBUR.get(), pos.x,pos.y + 0.01, pos.z, 1,0, 0, 0, 0);
            }
            if (livingEntity instanceof ServerPlayer serverPlayer) SpeciesCriterion.SUMMON_SPECTRE.trigger(serverPlayer);
            livingEntity.playSound(SpeciesSoundEvents.SPECTRALIBUR_RELEASE_SPECTRE.get(), 1,1);
            if (stack.getTag().getInt("Souls") == 0) stack.removeTagKey("Souls");
            stack.removeTagKey("UsingSouls");
        }
    }

    public int getUseDuration(ItemStack stack) {
        if (stack.hasTag() && stack.getTag().contains("Souls") && stack.getTag().getInt("Souls") > 0) return (stack.getTag().getInt("Souls") * 10) + 10;
        return 0;
    }

    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot p_43274_) {
        return p_43274_ == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(p_43274_);
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ToolAction toolAction) {
        return ToolActions.DEFAULT_SWORD_ACTIONS.contains(toolAction);
    }

    public float getDestroySpeed(ItemStack stack, BlockState state) {
        if (state.is(Blocks.COBWEB)) return 15.0F;
        else return state.is(BlockTags.SWORD_EFFICIENT) ? 1.5F : 1.0F;
    }

    public boolean canAttackBlock(BlockState state, Level level, BlockPos pos, Player player) {
        return !player.isCreative();
    }

    public boolean mineBlock(ItemStack stack, Level level, BlockState state, BlockPos pos, LivingEntity livingEntity) {
        return true;
    }

    public boolean isCorrectToolForDrops(BlockState p_43298_) {
        return p_43298_.is(Blocks.COBWEB);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack p_41452_) {
        return UseAnim.BLOCK;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, level, list, tooltipFlag);

        Style style = Style.EMPTY.withColor(0x44B4D1);

        if (stack.hasTag() && stack.getTag().contains("Souls") && stack.getTag().getInt("Souls") > 0) {
            list.add(Component.literal(" "));
            int souls = stack.getTag().getInt("Souls");
            list.add(Component.translatable("item.species.spectralibur.desc.release").withStyle(ChatFormatting.GRAY));

            if (souls > 1) list.add(Component.literal(" ").append(Component.translatable("item.species.spectralibur.desc.spectre.2", souls).withStyle(style)));
            else list.add(Component.literal(" ").append(Component.translatable("item.species.spectralibur.desc.spectre.1", souls).withStyle(style)));

            if (souls / 2 > 0) {
                if (souls >= 4) list.add(Component.literal(" ").append(Component.translatable("item.species.spectralibur.desc.jousting_spectre.2", souls / 2).withStyle(style)));
                else list.add(Component.literal(" ").append(Component.translatable("item.species.spectralibur.desc.jousting_spectre.1", souls / 2).withStyle(style)));
            }
            if (souls / 5 > 0) {
                if (souls >= 10) list.add(Component.literal(" ").append(Component.translatable("item.species.spectralibur.desc.hulking_spectre.2", souls / 5).withStyle(style)));
                else list.add(Component.literal(" ").append(Component.translatable("item.species.spectralibur.desc.hulking_spectre.1", souls / 5).withStyle(style)));
            }

        } else {
            list.add(Component.translatable("item.species.spectralibur.desc").withStyle(style));
        }
    }
}
