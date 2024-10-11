package com.ninni.species.world.poi;

import com.google.common.collect.ImmutableSet;
import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesBlocks;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.Set;

@Mod.EventBusSubscriber(modid = Species.MOD_ID)
public class SpeciesPointOfInterestTypes {

    public static final DeferredRegister<PoiType> POI_TYPES = DeferredRegister.create(ForgeRegistries.POI_TYPES, Species.MOD_ID);

    public static final RegistryObject<PoiType> BIRT_DWELLING = POI_TYPES.register("birt_dwelling", () -> new PoiType(getBlockStates(SpeciesBlocks.BIRT_DWELLING.get()), 0, 1));

    private static Set<BlockState> getBlockStates(Block block) {
        return ImmutableSet.copyOf(block.getStateDefinition().getPossibleStates());
    }
}
