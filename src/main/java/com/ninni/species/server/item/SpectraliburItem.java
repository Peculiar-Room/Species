package com.ninni.species.server.item;

import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.registry.SpeciesCriterion;
import com.ninni.species.server.entity.mob.update_3.Spectre;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.ItemAttributeModifiers;
import net.minecraft.world.item.component.Tool;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.common.ItemAbilities;
import net.neoforged.neoforge.common.ItemAbility;

import java.util.List;

public class SpectraliburItem extends Item {

    public SpectraliburItem(Properties properties) {
        super(properties.component(DataComponents.TOOL, createToolProperties()));
    }

    public static ItemAttributeModifiers createAttributes() {
        return ItemAttributeModifiers.builder()
            .add(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_ID, 8F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
            .add(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_ID, -2.4F, AttributeModifier.Operation.ADD_VALUE), EquipmentSlotGroup.MAINHAND)
            .build();
    }

    public static Tool createToolProperties() {
        return new Tool(List.of(Tool.Rule.minesAndDrops(List.of(Blocks.COBWEB), 15.0F), Tool.Rule.overrideSpeed(BlockTags.SWORD_EFFICIENT, 1.5F)), 1.0F, 2);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack mainHandItem = player.getMainHandItem();
        var tag = mainHandItem.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (mainHandItem.is(this) && mainHandItem.has(DataComponents.CUSTOM_DATA) && tag.contains("Souls") && tag.getInt("Souls") > 0) {
            player.startUsingItem(hand);
            player.playSound(SpeciesSoundEvents.SPECTRALIBUR_START_CHARGING.get(), 1,1);
            return InteractionResultHolder.consume(mainHandItem);
        }
        return InteractionResultHolder.pass(player.getItemInHand(hand));
        //return super.use(level, player, hand);
    }

    @Override
    public void onUseTick(Level level, LivingEntity livingEntity, ItemStack stack, int time) {
        super.onUseTick(level, livingEntity, stack, time);
        if (time < stack.getUseDuration(livingEntity)) {
            var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (time % 10 == 0 && stack.has(DataComponents.CUSTOM_DATA) && tag.contains("Souls") && tag.getInt("Souls") > 0) {
                tag.putInt("Souls", Math.max(tag.getInt("Souls") - 1, 0));
                tag.putInt("UsingSouls", tag.getInt("UsingSouls") + 1);
                livingEntity.playSound(SpeciesSoundEvents.SPECTRALIBUR_USE_SOUL.get(), 1,0.75F + tag.getInt("UsingSouls") * 0.1F);

                CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeUsed) {
        //super.releaseUsing(stack, level, livingEntity, timeUsed);
        spawnSpectres(stack, level, livingEntity);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        super.onStopUsing(stack, entity, count);
        spawnSpectres(stack, entity.level(), entity);
    }

    public void spawnSpectres(ItemStack stack, Level level, LivingEntity livingEntity) {
        var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (stack.has(DataComponents.CUSTOM_DATA) && tag.contains("UsingSouls") && tag.getInt("UsingSouls") > 0) {
            int usingSouls = tag.getInt("UsingSouls");
            if (level instanceof ServerLevel serverLevel && livingEntity instanceof Player player) {
                if (usingSouls == 1 || usingSouls == 3) Spectre.spawnSpectre(serverLevel, player, player.getOnPos().above(2), Spectre.Type.SPECTRE, true);
                if (usingSouls == 2 || usingSouls == 3 || usingSouls == 4) Spectre.spawnSpectre(serverLevel, player, player.getOnPos().above(2), Spectre.Type.JOUSTING_SPECTRE, true);
                if (usingSouls == 4) Spectre.spawnSpectre(serverLevel, player, player.getOnPos().above(2), Spectre.Type.JOUSTING_SPECTRE, true);
                if (usingSouls == 5) Spectre.spawnSpectre(serverLevel, player, player.getOnPos().above(2), Spectre.Type.HULKING_SPECTRE, true);
                Vec3 pos = livingEntity.position();
                serverLevel.sendParticles(SpeciesParticles.SPECTRALIBUR.get(), pos.x,pos.y + 0.01, pos.z, 1,0, 0, 0, 0);
            }
            if (livingEntity instanceof ServerPlayer serverPlayer) SpeciesCriterion.SUMMON_SPECTRE.get().trigger(serverPlayer);
            livingEntity.playSound(SpeciesSoundEvents.SPECTRALIBUR_RELEASE_SPECTRE.get(), 1,1);
            if (tag.getInt("Souls") == 0) {
                tag.remove("Souls");
            }
            tag.remove("UsingSouls");

            CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
        }
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (stack.has(DataComponents.CUSTOM_DATA) && tag.contains("Souls") && tag.getInt("Souls") > 0) {
            return (tag.getInt("Souls") * 10) + 10;
        }
        return 0;
    }

    @Override
    public boolean canPerformAction(ItemStack stack, ItemAbility itemAbility) {
        return ItemAbilities.DEFAULT_SWORD_ACTIONS.contains(itemAbility);
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

    @Override
    public UseAnim getUseAnimation(ItemStack stack) {
        return UseAnim.TOOT_HORN;
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
        super.appendHoverText(stack, context, list, tooltipFlag);

        Style style = Style.EMPTY.withColor(0x44B4D1);

        var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (stack.has(DataComponents.CUSTOM_DATA) && tag.contains("Souls") && tag.getInt("Souls") > 0) {
            list.add(Component.literal(" "));
            int souls = tag.getInt("Souls");
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
