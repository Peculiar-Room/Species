package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.world.gen.structure.LibraStructure;
import com.ninni.species.server.world.gen.structure.PaleontologyDigSiteStructure;
import com.ninni.species.server.world.gen.structure.SpectraliburChamberStructure;
import com.ninni.species.server.world.gen.structure.WraptorCoopStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpeciesStructureTypes {

    public static final DeferredRegister<StructureType<?>> STRUCTURES = DeferredRegister.create(Registries.STRUCTURE_TYPE, Species.MOD_ID);

    public static final DeferredHolder<StructureType<?>, StructureType<WraptorCoopStructure>> WRAPTOR_COOP = STRUCTURES.register("wraptor_coop", () -> () -> WraptorCoopStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<PaleontologyDigSiteStructure>> PALEONTOLOGY_DIG_SITE = STRUCTURES.register("paleontology_dig_site", () -> () -> PaleontologyDigSiteStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<LibraStructure>> LIBRA = STRUCTURES.register("libra", () -> () -> LibraStructure.CODEC);
    public static final DeferredHolder<StructureType<?>, StructureType<SpectraliburChamberStructure>> SPECTRALIBUR_CHAMBER = STRUCTURES.register("spectralibur_chamber", () -> () -> SpectraliburChamberStructure.CODEC);

}
