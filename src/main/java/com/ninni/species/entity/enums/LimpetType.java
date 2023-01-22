package com.ninni.species.entity.enums;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.Comparator;

public enum LimpetType {
    NO_SHELL(Items.AIR, 0, -1, SoundEvents.STONE_BREAK),
    SHELL(Items.AIR, 1, -1, SoundEvents.STONE_BREAK),
    AMETHYST(Items.AMETHYST_SHARD, 2, 0, SoundEvents.AMETHYST_CLUSTER_BREAK),
    LAPIS(Items.LAPIS_LAZULI, 3, 1, SoundEvents.STONE_BREAK),
    EMERALD(Items.EMERALD, 4, 2, SoundEvents.STONE_BREAK),
    DIAMOND(Items.DIAMOND, 5, 2, SoundEvents.STONE_BREAK);

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
