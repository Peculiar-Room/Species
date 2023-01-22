package com.ninni.species.structure;

import com.ninni.species.world.gen.structure.SpeciesStructures;
import net.minecraft.core.Holder;
import net.minecraft.data.worldgen.StructureSets;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadType;

public class SpeciesStructureSets {
    public static final Holder<StructureSet> WRAPTOR_COOPS = StructureSets.register(SpeciesStructureSetKeys.WRAPTOR_COOPS, SpeciesStructures.WRAPTOR_COOP, new RandomSpreadStructurePlacement(32, 8, RandomSpreadType.LINEAR, 867700449));
}
