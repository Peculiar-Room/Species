package com.ninni.species.world.poi;

import com.google.common.collect.ImmutableSet;
import com.ninni.species.block.SpeciesBlocks;
import net.fabricmc.fabric.api.object.builder.v1.world.poi.PointOfInterestHelper;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.Identifier;
import net.minecraft.world.poi.PointOfInterestType;

import java.util.Set;

import static com.ninni.species.Species.MOD_ID;

@SuppressWarnings("unused")
public class SpeciesPointsOfInterests {

    public static final PointOfInterestType BIRT_DWELLING = register("birt_dwelling", getAllStatesOf(SpeciesBlocks.BIRT_DWELLING), 0, 1);

    private static Set<BlockState> getAllStatesOf(Block block) {
        return ImmutableSet.copyOf(block.getStateManager().getStates());
    }

    private static PointOfInterestType register(String id, Set<BlockState> workStationStates, int ticketCount, int searchDistance) {
        return PointOfInterestHelper.register(new Identifier(MOD_ID, id), ticketCount, searchDistance, workStationStates);
    }
}
