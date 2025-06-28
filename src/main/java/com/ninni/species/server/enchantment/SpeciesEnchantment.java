package com.ninni.species.server.enchantment;

import com.ninni.species.registry.SpeciesEnchantments;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;

public class SpeciesEnchantment extends Enchantment {
    private final boolean discoverable;
    private final boolean treasure;
    private final boolean curse;
    private final boolean tradeable;
    private final boolean allowedOnBooks;
    private final int maxLevel;
    private final int minCost;

    public SpeciesEnchantment(int maxLevel, int minCost, Rarity rarity, EnchantmentCategory enchantmentCategory, boolean discoverable, boolean treasure, boolean curse, boolean tradeable, boolean allowedOnBooks, EquipmentSlot... equipmentSlots) {
        super(rarity, enchantmentCategory, equipmentSlots);
        this.minCost = minCost;
        this.maxLevel = maxLevel;
        this.discoverable = discoverable;
        this.treasure = treasure;
        this.curse = curse;
        this.tradeable = tradeable;
        this.allowedOnBooks = allowedOnBooks;
    }

    protected boolean checkCompatibility(Enchantment enchantment) {
        return this != enchantment && SpeciesEnchantments.areCompatible(this, enchantment);
    }

    @Override
    public int getMaxLevel() {
        return maxLevel;
    }

    @Override
    public boolean isCurse() {
        return curse;
    }

    @Override
    public boolean isAllowedOnBooks() {
        return allowedOnBooks;
    }

    @Override
    public boolean isDiscoverable() {
        return discoverable;
    }

    @Override
    public boolean isTreasureOnly() {
        return treasure;
    }

    @Override
    public boolean isTradeable() {
        return tradeable;
    }

    @Override
    public int getMinCost(int i) {
        return 1 + (i - 1) * minCost;
    }

    @Override
    public int getMaxCost(int i) {
        return super.getMinCost(i) + 30;
    }
}
