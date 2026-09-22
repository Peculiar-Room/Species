package com.ninni.species.server.item;

import com.ninni.species.server.block.entity.SpectreLightBlockEntity;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.cauldron.CauldronInteraction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LayeredCauldronBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Locale;

public class SpectreLightBlockItem extends BlockItem {
    private final DyedItemColor defaultColor = new DyedItemColor(0x7CF2F5, false);

    public SpectreLightBlockItem(Block block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
        var color = itemStack.getOrDefault(DataComponents.DYED_COLOR, defaultColor);
        if (color.rgb() != 8188661) {
            list.add(Component.translatable("item.species.spectre_light.color").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
                    .append(Component.translatable(String.format(Locale.ROOT, "#%06X", color.rgb())).withStyle(Style.EMPTY.withColor(color.rgb()))));
        } else {
            list.add(Component.translatable("item.species.spectre_light.dyeable").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY).withItalic(true)));
        }
        super.appendHoverText(itemStack, context, list, tooltipFlag);
    }

    public boolean hasCustomColor(ItemStack itemStack) {
        return itemStack.getOrDefault(DataComponents.DYED_COLOR, defaultColor).rgb() != 8188661;
    }

    public void clearColor(ItemStack itemStack) {
        itemStack.set(DataComponents.DYED_COLOR, defaultColor);
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
        var color = itemStack.getOrDefault(DataComponents.DYED_COLOR, defaultColor);
        if ((blockEntity = level.getBlockEntity(blockPos)) != null) {
            if (blockEntity instanceof SpectreLightBlockEntity blockEntity1) {
                blockEntity1.dyeColor = color;
            }
        }
        return super.updateCustomBlockEntityTag(blockPos, level, player, itemStack, blockState);
    }
}
