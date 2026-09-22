package com.ninni.species.server.world.poi;

import com.google.common.collect.ImmutableSet;
import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesBlocks;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.Set;

public class SpeciesPointOfInterestTypes {

    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(BuiltInRegistries.POINT_OF_INTEREST_TYPE, Species.MOD_ID);

    public static final DeferredHolder<PoiType, PoiType> BIRT_DWELLING = POI_TYPES.register("birt_dwelling", () -> new PoiType(getBlockStates(SpeciesBlocks.BIRT_DWELLING.get()), 0, 1));

    private static Set<BlockState> getBlockStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }
}
