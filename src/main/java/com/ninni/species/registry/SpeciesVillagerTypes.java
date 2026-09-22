package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.entity.npc.VillagerType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpeciesVillagerTypes {

    public static final DeferredRegister<VillagerType> VILLAGER_TYPES = DeferredRegister.create(Registries.VILLAGER_TYPE, Species.MOD_ID);

    public static final DeferredHolder<VillagerType, VillagerType> CURED_BEWEREAGER = VILLAGER_TYPES.register("cured_bewereager", () ->  new VillagerType("cured_bewereager"));
}
