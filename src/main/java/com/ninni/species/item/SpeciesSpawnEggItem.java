package com.ninni.species.item;

import com.ninni.species.SpeciesDevelopers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.ForgeSpawnEggItem;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class SpeciesSpawnEggItem extends ForgeSpawnEggItem {
    public SpeciesDevelopers.SpeciesDeveloperNames developer;

    public SpeciesSpawnEggItem(Supplier<? extends EntityType<? extends Mob>> type, int backgroundColor, int highlightColor, SpeciesDevelopers.SpeciesDeveloperNames developer, Properties props) {
        super(type, backgroundColor, highlightColor, props);
        this.developer = developer;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.literal(""));
        list.add(Component.translatable("species.developer.made_by", developer.getName()).withStyle(ChatFormatting.GRAY));
        list.add(Component.literal(developer.getContributionLevel().getContributionLevelName()).withStyle(ChatFormatting.GRAY).append(Component.translatable(developer.getName()).withStyle(developer.getFormatting())));

        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}
