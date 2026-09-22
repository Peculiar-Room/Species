package com.ninni.species.server.item;

import com.ninni.species.SpeciesDevelopers;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Supplier;

public class SpeciesSpawnEggItem extends SpawnEggItem {
    public SpeciesDevelopers.SpeciesDeveloperNames developer;

    public SpeciesSpawnEggItem(EntityType<? extends Mob> defaultType, int backgroundColor, int highlightColor, SpeciesDevelopers.SpeciesDeveloperNames developer, Properties props) {
        super(defaultType, backgroundColor, highlightColor, props);
        this.developer = developer;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.literal(""));
        list.add(Component.translatable("species.developer.made_by").withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("species.developer.contribution_level." + developer.getContributionLevel().getContributionLevelName()).withStyle(developer.getFormatting()).append(Component.translatable(developer.getName()).withStyle(developer.getFormatting())));

        super.appendHoverText(itemStack, context, list, tooltipFlag);
    }
}
