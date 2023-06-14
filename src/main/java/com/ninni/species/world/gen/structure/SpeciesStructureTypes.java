package com.ninni.species.world.gen.structure;

import com.ninni.species.Species;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesStructureTypes {

    public static final DeferredRegister<StructureType<?>> STRUCTURES = DeferredRegister.create(Registries.STRUCTURE_TYPE, Species.MOD_ID);

    public static final RegistryObject<StructureType<WraptorCoopStructure>> WRAPTOR_COOP = STRUCTURES.register("wraptor_coop", () -> () -> WraptorCoopStructure.CODEC);

}
