package com.ninni.species.entity.enums;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.Comparator;

public enum LimpetType {
    NO_SHELL(Items.BONE_MEAL, 0, -1, SoundEvents.STONE_BREAK, SoundEvents.STONE_PLACE, 10),
    SHELL(Items.BONE_MEAL, 1, -1, SoundEvents.STONE_BREAK, SoundEvents.STONE_PLACE, 10),
    COAL(Items.COAL, 2, 0, SoundEvents.STONE_BREAK, SoundEvents.STONE_PLACE, 24),
    AMETHYST(Items.AMETHYST_SHARD, 3, 0, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundEvents.AMETHYST_CLUSTER_PLACE, 8),
    LAPIS(Items.LAPIS_LAZULI, 4, 1, SoundEvents.STONE_BREAK, SoundEvents.STONE_PLACE, 16),
    EMERALD(Items.EMERALD, 5, 2, SoundEvents.STONE_BREAK, SoundEvents.STONE_PLACE, 8),
    DIAMOND(Items.DIAMOND, 6, 2, SoundEvents.STONE_BREAK, SoundEvents.STONE_PLACE, 6);

    public static final LimpetType[] TYPES = Arrays.stream(values()).sorted(Comparator.comparingInt(LimpetType::getId)).toArray(LimpetType[]::new);
    private final int id;
    private final Item item;
    private final int pickaxeLevel;
    private final SoundEvent additionalBreakSound;
    private final SoundEvent placingSound;
    private final int maxCount;

    LimpetType(Item item, int id, int pickaxeLevel, SoundEvent additionalBreakSound, SoundEvent placingSound, int maxCount) {
        this.id = id;
        this.item = item;
        this.pickaxeLevel = pickaxeLevel;
        this.additionalBreakSound = additionalBreakSound;
        this.placingSound = placingSound;
        this.maxCount = maxCount;
    }

    public SoundEvent getAdditionalBreakSound() {
        return additionalBreakSound;
    }

    public SoundEvent getPlacingSound() {
        return placingSound;
    }

    public int getPickaxeLevel() {
        return pickaxeLevel;
    }

    public int getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }

    public int getMaxCount() {
        return maxCount;
    }
}
