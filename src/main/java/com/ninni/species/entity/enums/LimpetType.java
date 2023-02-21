package com.ninni.species.entity.enums;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.util.Arrays;
import java.util.Comparator;

public enum LimpetType {
    NO_SHELL(Items.BONE_MEAL, 0, -1, SoundEvents.STONE_BREAK, SoundEvents.STONE_PLACE),
    SHELL(Items.BONE_MEAL, 1, -1, SoundEvents.STONE_BREAK, SoundEvents.STONE_PLACE),
    COAL(Items.COAL, 2, 3, SoundEvents.STONE_BREAK, SoundEvents.STONE_PLACE),
    AMETHYST(Items.AMETHYST_SHARD, 3, 0, SoundEvents.AMETHYST_CLUSTER_BREAK, SoundEvents.AMETHYST_CLUSTER_PLACE),
    LAPIS(Items.LAPIS_LAZULI, 4, 1, SoundEvents.STONE_BREAK, SoundEvents.STONE_PLACE),
    EMERALD(Items.EMERALD, 5, 2, SoundEvents.STONE_BREAK, SoundEvents.STONE_PLACE),
    DIAMOND(Items.DIAMOND, 6, 2, SoundEvents.STONE_BREAK, SoundEvents.STONE_PLACE);

    public static final LimpetType[] TYPES = Arrays.stream(values()).sorted(Comparator.comparingInt(LimpetType::getId)).toArray(LimpetType[]::new);
    private final int id;
    private final Item item;
    private final int pickaxeLevel;
    private final SoundEvent miningSound;
    private final SoundEvent placingSound;

    LimpetType(Item item, int id, int pickaxeLevel, SoundEvent miningSound, SoundEvent placingSound) {
        this.id = id;
        this.item = item;
        this.pickaxeLevel = pickaxeLevel;
        this.miningSound = miningSound;
        this.placingSound = placingSound;
    }

    public SoundEvent getMiningSound() {
        return miningSound;
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
}
