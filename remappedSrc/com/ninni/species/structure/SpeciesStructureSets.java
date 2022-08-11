package com.ninni.species.structure;

import com.ninni.species.world.gen.structure.SpeciesStructures;
import net.minecraft.structure.StructureSet;
import net.minecraft.util.registry.RegistryEntry;
import net.minecraft.world.gen.chunk.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.gen.chunk.placement.SpreadType;

import static net.minecraft.structure.StructureSets.*;

public class SpeciesStructureSets {
    public static final RegistryEntry<StructureSet> WRAPTOR_COOPS = register(SpeciesStructureSetKeys.WRAPTOR_COOPS, SpeciesStructures.WRAPTOR_COOP, new RandomSpreadStructurePlacement(32, 8, SpreadType.LINEAR, 867700449));
}
