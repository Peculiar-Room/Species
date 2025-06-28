package com.ninni.species.server.data;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesRecipeSerializers;
import net.minecraft.Util;
import net.minecraft.core.RegistryAccess;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.CustomRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.Level;

import java.util.List;

public class MobHeadFireworkStarRecipe extends CustomRecipe {
    private static final Ingredient SHAPE_INGREDIENT = Ingredient.of(
            SpeciesItems.QUAKE_HEAD.get(),
            SpeciesItems.GHOUL_HEAD.get(),
            SpeciesItems.BEWEREAGER_HEAD.get(),
            SpeciesItems.WICKED_CANDLE.get()
    );
    private static final java.util.Map<Item, FireworkRocketItem.Shape> SHAPE_BY_ITEM = Util.make(Maps.newHashMap(), (map) -> {
        map.put(SpeciesItems.QUAKE_HEAD.get(), FireworkRocketItem.Shape.CREEPER);
        map.put(SpeciesItems.GHOUL_HEAD.get(), FireworkRocketItem.Shape.CREEPER);
        map.put(SpeciesItems.BEWEREAGER_HEAD.get(), FireworkRocketItem.Shape.CREEPER);
        map.put(SpeciesItems.WICKED_CANDLE.get(), FireworkRocketItem.Shape.CREEPER);
    });
    private static final Ingredient TRAIL_INGREDIENT = Ingredient.of(Items.DIAMOND);
    private static final Ingredient FLICKER_INGREDIENT = Ingredient.of(Items.GLOWSTONE_DUST);
    private static final Ingredient GUNPOWDER_INGREDIENT = Ingredient.of(Items.GUNPOWDER);

    public MobHeadFireworkStarRecipe(ResourceLocation name, CraftingBookCategory category) {
        super(name, category);
    }


    public boolean matches(CraftingContainer p_43895_, Level p_43896_) {
        boolean flag = false;
        boolean flag1 = false;
        boolean flag2 = false;
        boolean flag3 = false;
        boolean flag4 = false;

        for(int i = 0; i < p_43895_.getContainerSize(); ++i) {
            ItemStack itemstack = p_43895_.getItem(i);
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

    public ItemStack assemble(CraftingContainer p_43893_, RegistryAccess p_266692_) {
        ItemStack itemstack = new ItemStack(Items.FIREWORK_STAR);
        CompoundTag compoundtag = itemstack.getOrCreateTagElement("Explosion");
        FireworkRocketItem.Shape fireworkrocketitem$shape = FireworkRocketItem.Shape.SMALL_BALL;
        List<Integer> list = Lists.newArrayList();

        for(int i = 0; i < p_43893_.getContainerSize(); ++i) {
            ItemStack itemstack1 = p_43893_.getItem(i);
            if (!itemstack1.isEmpty()) {
                if (SHAPE_INGREDIENT.test(itemstack1)) {
                    FireworkRocketItem.Shape shape = SHAPE_BY_ITEM.get(itemstack1.getItem());
                    if (shape != null) fireworkrocketitem$shape = shape;
                } else if (FLICKER_INGREDIENT.test(itemstack1)) {
                    compoundtag.putBoolean("Flicker", true);
                } else if (TRAIL_INGREDIENT.test(itemstack1)) {
                    compoundtag.putBoolean("Trail", true);
                } else if (itemstack1.getItem() instanceof DyeItem) {
                    list.add(((DyeItem)itemstack1.getItem()).getDyeColor().getFireworkColor());
                }
            }
        }

        compoundtag.putIntArray("Colors", list);
        fireworkrocketitem$shape.save(compoundtag);
        return itemstack;
    }

    public boolean canCraftInDimensions(int p_43885_, int p_43886_) {
        return p_43885_ * p_43886_ >= 2;
    }

    public ItemStack getResultItem(RegistryAccess p_266932_) {
        return new ItemStack(Items.FIREWORK_STAR);
    }

    public RecipeSerializer<?> getSerializer() {
        return SpeciesRecipeSerializers.SPECIES_MOB_HEAD_FIREWORK_STAR.get();
    }
}
