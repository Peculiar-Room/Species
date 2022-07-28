package com.ninni.species.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.MapColor;
import net.minecraft.block.Material;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.ninni.species.Species.*;

public class SpeciesBlocks {
    public static final Block WRAPTOR_EGG = register("wraptor_egg", new WraptorEggBlock(FabricBlockSettings.of(Material.SOLID_ORGANIC, MapColor.ORANGE).requiresTool().strength(10.0f, 1200.0f).sounds(BlockSoundGroup.SHROOMLIGHT).nonOpaque()));

    private static Block register(String id, Block block) {
        return Registry.register(Registry.BLOCK, new Identifier(MOD_ID, id), block);
    }
}
