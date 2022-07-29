package com.ninni.species.structure;

import com.ninni.species.Species;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;

public class SpeciesStructureSetKeys {
    public static final RegistryKey<StructureSet> WRAPTOR_COOPS = of("wraptor_coops");

    private static RegistryKey<StructureSet> of(String id) {
        return RegistryKey.of(Registry.STRUCTURE_SET_KEY, new Identifier(Species.MOD_ID, id));
    }
}
