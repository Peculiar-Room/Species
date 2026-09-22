package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.util.ObfuscationReflectionHelper;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class SpeciesPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(BuiltInRegistries.POTION, Species.MOD_ID);

    public static final DeferredHolder<Potion, Potion> BLOODLUST = POTIONS.register("bloodlust", registryName -> new Potion(registryName.getPath(), new MobEffectInstance(SpeciesStatusEffects.BLOODLUST, 20 * 60 * 60)));

    //public static void addMix(Potion input, Item reactant, Potion result) {
    //    try {
    //        ADD_MIX.invoke(null, input, reactant, result);
    //    } catch (IllegalAccessException | InvocationTargetException e) {
    //        throw new IllegalStateException("Failed to add mix for " + BuiltInRegistries.POTION.getKey(result) + " from " + BuiltInRegistries.ITEM.getKey(reactant), e);
    //    }
    //}
}
