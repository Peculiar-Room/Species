package com.ninni.species.server.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesRecipeSerializers;
import it.unimi.dsi.fastutil.ints.IntArrayList;
import it.unimi.dsi.fastutil.ints.IntList;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.FireworkExplosion;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;

import java.util.List;

public class MobHeadFireworkStarRecipe extends CustomRecipe {
    private static final Ingredient SHAPE_INGREDIENT = Ingredient.of(
            SpeciesItems.QUAKE_HEAD.get(),
            SpeciesItems.GHOUL_HEAD.get(),
            SpeciesItems.BEWEREAGER_HEAD.get(),
            SpeciesItems.WICKED_CANDLE.get()
    );
    private static final java.util.Map<Item, FireworkExplosion.Shape> SHAPE_BY_ITEM = Util.make(Maps.newHashMap(), (map) -> {
        map.put(SpeciesItems.QUAKE_HEAD.get(), FireworkExplosion.Shape.CREEPER);
        map.put(SpeciesItems.GHOUL_HEAD.get(), FireworkExplosion.Shape.CREEPER);
        map.put(SpeciesItems.BEWEREAGER_HEAD.get(), FireworkExplosion.Shape.CREEPER);
        map.put(SpeciesItems.WICKED_CANDLE.get(), FireworkExplosion.Shape.CREEPER);

    });
    private static final Ingredient TRAIL_INGREDIENT = Ingredient.of(Items.DIAMOND);
    private static final Ingredient FLICKER_INGREDIENT = Ingredient.of(Items.GLOWSTONE_DUST);
    private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);

    public MobHeadFireworkStarRecipe(ResourceLocation name, CraftingBookCategory category) {
        super(category);
    }


    public boolean matches(CraftingInput craftingInput, Level level) {
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;

        for(int i = 0; i < craftingInput.size(); ++i) {
            ItemStack itemstack = craftingInput.getItem(i);
            if (!itemstack.isEmpty()) {
                if (SHAPE_INGREDIENT.test(itemstack)) {
                    if (flag2) {
                        return false;
                    }

                    flag2 = true;
                } else if (FLICKER_INGREDIENT.test(itemstack)) {
                    if (flag4) {
                        return false;
                    }

                    flag4 = true;
                } else if (TRAIL_INGREDIENT.test(itemstack)) {
                    if (flag3) {
                        return false;
                    }

                    flag3 = true;
                } else if (GUNPOWDER_INGREDIENT.test(itemstack)) {
                    if (flag) {
                        return false;
                    }

                    flag = true;
                } else {
                    if (!(itemstack.getItem() instanceof DyeItem)) {
                        return false;
                    }

                    flag1 = true;
                }
            }
        }

        return flag && flag1;
    }

    public ItemStack assemble(CraftingInput craftingInput, HolderLookup.Provider provider) {
        FireworkExplosion.Shape fireworkexplosion$shape = FireworkExplosion.Shape.SMALL_BALL;
        boolean flag = false;
        boolean flag1 = false;
        IntList intlist = new IntArrayList();

        for(int i = 0; i < craftingInput.size(); ++i) {
            ItemStack itemstack = craftingInput.getItem(i);
            if (!itemstack.isEmpty()) {
                if (SHAPE_INGREDIENT.test(itemstack)) {
                    fireworkexplosion$shape = SHAPE_BY_ITEM.get(itemstack.getItem());
                } else if (FLICKER_INGREDIENT.test(itemstack)) {
                    flag = true;
                } else if (TRAIL_INGREDIENT.test(itemstack)) {
                    flag1 = true;
                } else if (itemstack.getItem() instanceof DyeItem) {
                    intlist.add(((DyeItem)itemstack.getItem()).getDyeColor().getFireworkColor());
                }
            }
        }

        ItemStack itemstack1 = new ItemStack(Items.FIREWORK_STAR);
        itemstack1.set(DataComponents.FIREWORK_EXPLOSION, new FireworkExplosion(fireworkexplosion$shape, intlist, IntList.of(), flag1, flag));
        return itemstack1;
    }

    public boolean canCraftInDimensions(int p_43885_, int p_43886_) {
        return p_43885_ * p_43886_ >= 2;
    }

    public ItemStack getResultItem(HolderLookup.Provider registries) {
        return new ItemStack(Items.FIREWORK_STAR);
    }

    public RecipeSerializer<?> getSerializer() {
        return SpeciesRecipeSerializers.SPECIES_MOB_HEAD_FIREWORK_STAR.get();
    }
}
