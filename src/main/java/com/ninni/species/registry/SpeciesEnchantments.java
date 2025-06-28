package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.enchantment.SpeciesEnchantment;
import com.ninni.species.server.item.CrankbowItem;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentCategory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SpeciesEnchantments {
    public static final DeferredRegister<Enchantment> ENCHANTMENTS = DeferredRegister.create(ForgeRegistries.ENCHANTMENTS, Species.MOD_ID);

    public static final EnchantmentCategory CRANKBOW = EnchantmentCategory.create("crankbow", (item -> item instanceof CrankbowItem));
    public static final RegistryObject<Enchantment> CAPACITY = ENCHANTMENTS.register("capacity", () -> new SpeciesEnchantment(2, 12, Enchantment.Rarity.COMMON, CRANKBOW, true, false, false, true, true, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> SPARING = ENCHANTMENTS.register("sparing", () -> new SpeciesEnchantment(3, 12, Enchantment.Rarity.COMMON, CRANKBOW, true, false, false, true, true, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> QUICK_CRANK = ENCHANTMENTS.register("quick_crank", () -> new SpeciesEnchantment(3, 8, Enchantment.Rarity.COMMON, CRANKBOW, true, false, false, true, true, EquipmentSlot.MAINHAND));
    public static final RegistryObject<Enchantment> SCATTERSHOT = ENCHANTMENTS.register("scattershot", () -> new SpeciesEnchantment(1, 18, Enchantment.Rarity.COMMON, CRANKBOW, false, true, false, true, true, EquipmentSlot.MAINHAND));

    public static boolean areCompatible(Enchantment enchantment1, Enchantment enchantment2) {
        if (enchantment1 == CAPACITY.get() && enchantment2 == SPARING.get()) return false;
        if (enchantment1 == SPARING.get() && enchantment2 == CAPACITY.get()) return false;

        if (enchantment1 == SCATTERSHOT.get() && enchantment2 == QUICK_CRANK.get()) return false;
        if (enchantment1 == QUICK_CRANK.get() && enchantment2 == SCATTERSHOT.get()) return false;

        return true;
    }

}