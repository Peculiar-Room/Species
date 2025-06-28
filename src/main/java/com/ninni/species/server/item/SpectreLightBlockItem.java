package com.ninni.species.server.item;

import com.ninni.species.server.block.entity.SpectreLightBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class SpectreLightBlockItem extends BlockItem implements DyeableLeatherItem {
    public SpectreLightBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        CompoundTag compoundTag = BlockItem.getBlockEntityData(itemStack);

        if (compoundTag != null && compoundTag.contains("color", 99) && compoundTag.getInt("color") != 0x7CF2F5) {
            int rem;
            int decimal = compoundTag.getInt("color");
            StringBuilder hex= new StringBuilder();
            char[] hexchars ={'0','1','2','3','4','5','6','7','8','9','A','B','C','D','E','F'};
            while(decimal>0) {
                rem=decimal%16;
                hex.insert(0, hexchars[rem]);
                decimal=decimal/16;
            }

            list.add(Component.translatable("item.species.spectre_light.color").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
                    .append(Component.translatable("#" + hex).withStyle(Style.EMPTY.withColor(compoundTag.getInt("color")))));
        } else {
            list.add(Component.translatable("item.species.spectre_light.dyeable").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
        }
        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }

    public boolean hasCustomColor(ItemStack itemStack) {
        CompoundTag compoundTag = BlockItem.getBlockEntityData(itemStack);
        return compoundTag != null && compoundTag.contains("color", 99) && compoundTag.getInt("color") != 0x7CF2F5;
    }

    public int getColor(ItemStack itemStack) {
        CompoundTag compoundTag = BlockItem.getBlockEntityData(itemStack);
        return compoundTag != null && compoundTag.contains("color", 99) ? compoundTag.getInt("color") : 0x7CF2F5;
    }

    public void clearColor(ItemStack itemStack) {
        itemStack.getOrCreateTagElement("BlockEntityTag").putInt("color", 0x7CF2F5);
    }

    public void setColor(ItemStack itemStack, int i) {
        itemStack.getOrCreateTagElement("BlockEntityTag").putInt("color", i);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Level level = useOnContext.getLevel();
        BlockPos blockPos = useOnContext.getClickedPos();
        BlockState state = useOnContext.getLevel().getBlockState(blockPos);
        ItemStack itemStack = useOnContext.getPlayer().getItemInHand(useOnContext.getHand());

        if (state.is(Blocks.WATER_CAULDRON) && this.hasCustomColor(itemStack)) {
            ItemStack itemStack2 = itemStack.copy();
            this.clearColor(itemStack2);
            useOnContext.getPlayer().setItemInHand(useOnContext.getHand(), itemStack2);
            LayeredCauldronBlock.lowerFillLevel(level.getBlockState(blockPos), level, blockPos);

            return InteractionResult.SUCCESS;
        }

        return super.useOn(useOnContext);
    }

    @Override
    protected boolean updateCustomBlockEntityTag(BlockPos blockPos, Level level, @Nullable Player player, ItemStack itemStack, BlockState blockState) {
        BlockEntity blockEntity;
        CompoundTag compoundTag = BlockItem.getBlockEntityData(itemStack);
        if (compoundTag != null && (blockEntity = level.getBlockEntity(blockPos)) != null) {
            if (blockEntity instanceof SpectreLightBlockEntity blockEntity1) {
                blockEntity1.color = compoundTag.getInt("color");
            }
        }
        return super.updateCustomBlockEntityTag(blockPos, level, player, itemStack, blockState);
    }
}
