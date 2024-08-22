package com.ninni.species.registry;

import com.ninni.species.block.*;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;

import static com.ninni.species.Species.MOD_ID;

public class SpeciesBlocks {
    public static final Block WRAPTOR_EGG = register("wraptor_egg", new WraptorEggBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(10.0f, 1200.0f).mapColor(DyeColor.ORANGE).sound(SpeciesBlockSoundGroup.WRAPTOR_EGG).lightLevel(state -> 7)));

    public static final Block BIRT_DWELLING = register("birt_dwelling", new BirtDwellingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(2.0f).sound(SoundType.WOOD)));

    public static final Block RED_SUSPICIOUS_SAND = register("red_suspicious_sand", new BrushableBlock(Blocks.RED_SAND, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.SNARE).strength(0.25f).sound(SoundType.SUSPICIOUS_SAND).pushReaction(PushReaction.DESTROY), SoundEvents.BRUSH_SAND, SoundEvents.BRUSH_SAND_COMPLETED));
    public static final Block BONE_BARK = register("bone_bark", new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK)));
    public static final Block BONE_VERTEBRA = register("bone_vertebra", new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK)));
    public static final Block BONE_SPIKE = register("bone_spike", new BoneSpikeBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK)));

    public static final Block TROOPER = register("trooper", new TrooperBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_SAPLING)));
    public static final Block POTTED_TROOPER = register("potted_trooper", new PottedTrooperBlock(BlockBehaviour.Properties.copy(Blocks.POTTED_SPRUCE_SAPLING)));

    public static final Block PETRIFIED_EGG = register("petrified_egg", new PetrifiedEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5f).sound(SoundType.METAL).noOcclusion()));
    public static final Block ALPHACENE_MOSS_BLOCK = register("alphacene_moss_block", new Block(BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK)));
    public static final Block ALPHACENE_MOSS_CARPET = register("alphacene_moss_carpet", new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET)));
    public static final Block ALPHACENE_GRASS_BLOCK = register("alphacene_grass_block", new AlphaceneGrassBlock(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK)));
    public static final Block ALPHACENE_GRASS = register("alphacene_grass", new AlphaceneTallGrassBlock(BlockBehaviour.Properties.copy(Blocks.GRASS)));
    public static final Block ALPHACENE_TALL_GRASS = register("alphacene_tall_grass", new DoublePlantBlock(BlockBehaviour.Properties.copy(Blocks.TALL_GRASS)));
    public static final Block ALPHACENE_MUSHROOM = register("alphacene_mushroom", new AlphaceneMushroomBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM).lightLevel(state -> 1).emissiveRendering((blockState, blockGetter, blockPos) -> true)));
    public static final Block ALPHACENE_MUSHROOM_BLOCK = register("alphacene_mushroom_block", new HugeMushroomBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM_BLOCK).lightLevel(state -> 1).emissiveRendering((blockState, blockGetter, blockPos) -> true)));
    public static final Block ALPHACENE_MUSHROOM_GROWTH = register("alphacene_mushroom_growth", new AlphaceneMushroomGrowthBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM).lightLevel(state -> 1).emissiveRendering((blockState, blockGetter, blockPos) -> true)));

    public static final Block CRUNCHER_EGG = register("cruncher_egg", new CruncherEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).randomTicks().strength(0.5f).sound(SoundType.METAL).noOcclusion()));
    public static final Block CRUNCHER_PELLET = register("cruncher_pellet", new CruncherPelletBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.SCULK)));

    public static final Block FROZEN_MEAT = register("frozen_meat", new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(0.5f).sound(SpeciesSoundEvents.FROZEN_MEAT)));
    public static final Block FROZEN_HAIR = register("frozen_hair", new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SpeciesSoundEvents.FROZEN_HAIR)));
    public static final Block ICHOR = register("ichor", new IchorBlock(BlockBehaviour.Properties.of().instabreak().sound(SoundType.SLIME_BLOCK).noCollission().pushReaction(PushReaction.DESTROY).noLootTable().noOcclusion()));

    public static final Block SPRINGLING_EGG = register("springling_egg", new SpringlingEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5f).sound(SoundType.METAL).noOcclusion()));

    private static Block register(String id, Block block) {
        return Registry.register(BuiltInRegistries.BLOCK, new ResourceLocation(MOD_ID, id), block);
    }
}
