package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.world.gen.structure.LibraStructure;
import com.ninni.species.server.world.gen.structure.PaleontologyDigSiteStructure;
import com.ninni.species.server.world.gen.structure.SpectraliburChamberStructure;
import com.ninni.species.server.world.gen.structure.WraptorCoopStructure;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesStructureTypes {

    public static final DeferredRegister<StructureType<?>> STRUCTURES = DeferredRegister.create(Registries.STRUCTURE_TYPE, Species.MOD_ID);

    public static final RegistryObject<StructureType<WraptorCoopStructure>> WRAPTOR_COOP = STRUCTURES.register("wraptor_coop", () -> () -> WraptorCoopStructure.CODEC);
    public static final RegistryObject<StructureType<PaleontologyDigSiteStructure>> PALEONTOLOGY_DIG_SITE = STRUCTURES.register("paleontology_dig_site", () -> () -> PaleontologyDigSiteStructure.CODEC);
    public static final RegistryObject<StructureType<LibraStructure>> LIBRA = STRUCTURES.register("libra", () -> () -> LibraStructure.CODEC);
    public static final RegistryObject<StructureType<SpectraliburChamberStructure>> SPECTRALIBUR_CHAMBER = STRUCTURES.register("spectralibur_chamber", () -> () -> SpectraliburChamberStructure.CODEC);

}
