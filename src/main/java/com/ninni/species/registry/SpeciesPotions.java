package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesPotions {
    public static final DeferredRegister<Potion> POTIONS = DeferredRegister.create(ForgeRegistries.POTIONS, Species.MOD_ID);
    private static final Method ADD_MIX = ObfuscationReflectionHelper.findMethod(PotionBrewing.class, "m_43513_", Potion.class, Item.class, Potion.class);

    public static final RegistryObject<Potion> BLOODLUST = POTIONS.register("bloodlust", () -> new Potion(new MobEffectInstance(SpeciesStatusEffects.BLOODLUST.get(), 20 * 60 * 60)));

    public static void register() {
        addMix(Potions.AWKWARD, SpeciesItems.GHOUL_TONGUE.get(), BLOODLUST.get());
    }

    public static void addMix(Potion input, Item reactant, Potion result) {
        try {
            ADD_MIX.invoke(null, input, reactant, result);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to add mix for " + ForgeRegistries.POTIONS.getKey(result) + " from " + ForgeRegistries.ITEMS.getKey(reactant), e);
        }
    }
}
