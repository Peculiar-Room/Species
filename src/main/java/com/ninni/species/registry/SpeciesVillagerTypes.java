package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

public class SpeciesVillagerTypes {

    public static final DeferredRegister<VillagerType> VILLAGER_TYPES = DeferredRegister.create(Registries.VILLAGER_TYPE, Species.MOD_ID);

    public static final RegistryObject<VillagerType> CURED_BEWEREAGER = VILLAGER_TYPES.register("cured_bewereager", () ->  new VillagerType("cured_bewereager"));
}
