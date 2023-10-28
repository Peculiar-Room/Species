package com.ninni.species.item;

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

public class SpeciesSpawnEgg extends SpawnEggItem {
    public SpeciesDevelopers developer;

    public SpeciesSpawnEgg(EntityType<? extends Mob> entityType, int i, int j, SpeciesDevelopers developer, Properties properties) {
        super(entityType, i, j, properties);
        this.developer = developer;
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable(""));
        list.add(Component.translatable("species.developer.made_by", developer.getName()).withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable(developer.getName()).withStyle(developer.getFormatting()));

        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}
