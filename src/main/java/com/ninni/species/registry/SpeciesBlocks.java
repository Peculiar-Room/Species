package com.ninni.species.registry;

import com.ninni.species.block.*;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.MapColor;

import static com.ninni.species.Species.MOD_ID;

public class SpeciesBlocks {
    public static final Block WRAPTOR_EGG = register("wraptor_egg", new WraptorEggBlock(FabricBlockSettings.create().requiresTool().strength(10.0f, 1200.0f).mapColor(DyeColor.ORANGE).sounds(SpeciesBlockSoundGroup.WRAPTOR_EGG).luminance(state -> 7)));
    public static final Block BIRT_DWELLING = register("birt_dwelling", new BirtDwellingBlock(FabricBlockSettings.create().mapColor(MapColor.SAND).strength(2.0f).sounds(SoundType.WOOD)));

    public static final Block TREEPER_SAPLING = register("treeper_sapling", new TreeperSaplingBlock(FabricBlockSettings.copyOf(Blocks.SPRUCE_SAPLING)));

    public static final Block PETRIFIED_EGG = register("petrified_egg", new PetrifiedEggBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_GREEN).strength(0.5f).sound(SoundType.METAL).noOcclusion()));

    public static final Block CRUNCHER_EGG = register("cruncher_egg", new CruncherEggBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_PURPLE).strength(0.5f).sound(SoundType.METAL).noOcclusion()));

    public static final Block FROZEN_MEAT = register("frozen_meat", new RotatedPillarBlock(FabricBlockSettings.create().mapColor(MapColor.TERRACOTTA_RED).strength(0.5f).sound(SoundType.SCULK)));
    public static final Block FROZEN_HAIR = register("frozen_hair", new Block(FabricBlockSettings.create().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SoundType.WOOL)));

    public static final Block SPRINGLING_EGG = register("springling_egg", new SpringlingEggBlock(FabricBlockSettings.create().mapColor(MapColor.COLOR_YELLOW).strength(0.5f).sound(SoundType.METAL).noOcclusion()));

    private static Block register(String id, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(MOD_ID, id), block);
    }
}
