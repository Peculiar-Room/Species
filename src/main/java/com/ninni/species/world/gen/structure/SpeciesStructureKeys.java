package com.ninni.species.world.gen.structure;

import com.ninni.species.Species;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.Structure;

public class SpeciesStructureKeys {
    public static final ResourceKey<Structure> WRAPTOR_COOP = of("wraptor_coop");

    private static ResourceKey<Structure> of(String id) {
        return ResourceKey.create(Registries.STRUCTURE, new ResourceLocation(Species.MOD_ID, id));
    }
}
