package com.ninni.species.world.gen.structure;

import com.ninni.species.Species;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import net.minecraft.world.gen.structure.Structure;

public class SpeciesStructureKeys {
    public static final RegistryKey<Structure> WRAPTOR_COOP = of("wraptor_coop");

    private static RegistryKey<Structure> of(String id) {
        return RegistryKey.of(Registry.STRUCTURE_KEY, new Identifier(Species.MOD_ID, id));
    }
}
