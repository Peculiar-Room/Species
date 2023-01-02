package com.ninni.species.entity.enums;

import net.minecraft.item.Item;
import net.minecraft.item.Items;

import java.util.Arrays;
import java.util.Comparator;

public enum LimpetType {
    DEFAULT(Items.AIR, 0),
    AMETHYST(Items.AMETHYST_SHARD, 1),
    LAPIS(Items.LAPIS_LAZULI, 2),
    EMERALD(Items.EMERALD, 3),
    DIAMOND(Items.DIAMOND, 4);

    public static final LimpetType[] TYPES = Arrays.stream(values()).sorted(Comparator.comparingInt(LimpetType::getId)).toArray(LimpetType[]::new);
    private final int id;
    private final Item item;

    LimpetType(Item item, int id) {
        this.id = id;
        this.item = item;
    }

    public int getId() {
        return id;
    }

    public Item getItem() {
        return item;
    }
}
