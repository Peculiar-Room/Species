//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.decoration.PaintingVariant;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SpeciesPaintingVariants {
    public static final ResourceKey<PaintingVariant> THE_COMPOSITION = ResourceKey.create(Registries.PAINTING_VARIANT, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "the_composition"));

    public static final ArrayList<ResourceKey<PaintingVariant>> PAINTING_VARIANTS = new ArrayList<>();

    public static void addPaintings() {
        PAINTING_VARIANTS.add(THE_COMPOSITION);
    }
}
