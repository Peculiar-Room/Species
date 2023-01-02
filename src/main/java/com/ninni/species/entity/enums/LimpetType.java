package com.ninni.species.entity.enums;

import net.fabricmc.yarn.constants.MiningLevels;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;

import java.util.Arrays;
import java.util.Comparator;

public enum LimpetType {
    NO_SHELL(Items.AIR, 0, MiningLevels.HAND, SoundEvents.BLOCK_STONE_BREAK),
    SHELL(Items.AIR, 1, MiningLevels.HAND, SoundEvents.BLOCK_STONE_BREAK),
    AMETHYST(Items.AMETHYST_SHARD, 2, MiningLevels.WOOD, SoundEvents.BLOCK_AMETHYST_CLUSTER_BREAK),
    LAPIS(Items.LAPIS_LAZULI, 3, MiningLevels.STONE, SoundEvents.BLOCK_STONE_BREAK),
    EMERALD(Items.EMERALD, 4, MiningLevels.IRON, SoundEvents.BLOCK_STONE_BREAK),
    DIAMOND(Items.DIAMOND, 5, MiningLevels.IRON, SoundEvents.BLOCK_STONE_BREAK);

    public static final LimpetType[] TYPES = Arrays.stream(values()).sorted(Comparator.comparingInt(LimpetType::getId)).toArray(LimpetType[]::new);
    private final int id;
    private final Item item;
    private final int pickaxeLevel;
    private final SoundEvent miningSound;

    LimpetType(Item item, int id, int pickaxeLevel, SoundEvent miningSound) {
        this.id = id;
        this.item = item;
        this.pickaxeLevel = pickaxeLevel;
        this.miningSound = miningSound;
    }

    public SoundEvent getMiningSound() {
        return miningSound;
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
}
