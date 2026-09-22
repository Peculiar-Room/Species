package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.data.MobHeadFireworkStarRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.SimpleCraftingRecipeSerializer;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpeciesRecipeSerializers {
    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Species.MOD_ID);

    public static final DeferredHolder<RecipeSerializer<?>, RecipeSerializer<?>> SPECIES_MOB_HEAD_FIREWORK_STAR = RECIPE_SERIALIZERS.register("species_mob_head_firework_star", (location) -> new SimpleCraftingRecipeSerializer<>((category) -> new MobHeadFireworkStarRecipe(location, category)));
}
