package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SpeciesPaintingVariants {
    public static final DeferredRegister<PaintingVariant> PAINTING_VARIANTS = DeferredRegister.create(ForgeRegistries.PAINTING_VARIANTS, Species.MOD_ID);

    public static final RegistryObject<PaintingVariant> THE_COMPOSITION = PAINTING_VARIANTS.register("the_composition", () -> new PaintingVariant(48, 64));

}
