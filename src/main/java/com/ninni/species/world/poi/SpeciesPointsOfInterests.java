package com.ninni.species.world.poi;

import com.google.common.collect.ImmutableSet;
import com.ninni.species.block.SpeciesBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.util.Set;

import static com.ninni.species.Species.MOD_ID;

@SuppressWarnings("unused")
public class SpeciesPointsOfInterests {

    public static final PoiType BIRT_DWELLING = register("birt_dwelling", getAllStatesOf(SpeciesBlocks.BIRT_DWELLING), 0, 1);

    private static Set<BlockState> getAllStatesOf(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }

    private static PoiType register(String id, Set<BlockState> workStationStates, int ticketCount, int searchDistance) {
        return PointOfInterestHelper.register(new ResourceLocation(MOD_ID, id), ticketCount, searchDistance, workStationStates);
    }
}
