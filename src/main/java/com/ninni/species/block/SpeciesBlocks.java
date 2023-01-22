package com.ninni.species.block;

import com.ninni.species.sound.SpeciesBlockSoundGroup;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;

import static com.ninni.species.Species.MOD_ID;

public class SpeciesBlocks {
    public static final Block WRAPTOR_EGG = register("wraptor_egg", new WraptorEggBlock(FabricBlockSettings.of(Material.GRASS, MaterialColor.COLOR_ORANGE).requiresTool().strength(10.0f, 1200.0f).sounds(SpeciesBlockSoundGroup.WRAPTOR_EGG).luminance(state -> 7)));
    public static final Block BIRT_DWELLING = register("birt_dwelling", new BirtDwellingBlock(FabricBlockSettings.of(Material.WOOD, MaterialColor.SAND).strength(2.0f).sounds(SoundType.WOOD)));

    private static Block register(String id, Block block) {
        return Registry.register(Registry.BLOCK, new ResourceLocation(MOD_ID, id), block);
    }
}
