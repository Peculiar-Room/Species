package com.ninni.species.mixin;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(BrushableBlockEntity.class)
public interface BrushableBlockEntityAccessor {
    @Accessor
    Direction getHitDirection();

    @Accessor("hitDirection")
    void setHitDirection(Direction hitDirection);

    @Accessor("brushCountResetsAtTick")
    void setBrushCountResetsAtTick(long brushCountResetsAtTick);

    @Accessor
    long getCoolDownEndsAtTick();

    @Accessor("coolDownEndsAtTick")
    void setCoolDownEndsAtTick(long coolDownEndsAtTick);

    @Accessor
    int getBrushCount();

    @Accessor("brushCount")
    void setBrushCount(int brushCount);

    @Accessor
    ItemStack getItem();

    @Accessor("item")
    void setItem(ItemStack stack);

    @Accessor("lootTable")
    void setLootTable(ResourceLocation lootTable);

    @Accessor
    long getLootTableSeed();

}
