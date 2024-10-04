package com.ninni.species.init;

import com.ninni.species.Species;
import com.ninni.species.block.AlphaceneGrassBlock;
import com.ninni.species.block.AlphaceneMushroomBlock;
import com.ninni.species.block.AlphaceneMushroomGrowthBlock;
import com.ninni.species.block.AlphaceneTallGrassBlock;
import com.ninni.species.block.BirtDwellingBlock;
import com.ninni.species.block.BoneSpikeBlock;
import com.ninni.species.block.CruncherEggBlock;
import com.ninni.species.block.CruncherPelletBlock;
import com.ninni.species.block.IchorBlock;
import com.ninni.species.block.PetrifiedEggBlock;
import com.ninni.species.block.PottedTrooperBlock;
import com.ninni.species.block.SpringlingEggBlock;
import com.ninni.species.block.TrooperBlock;
import com.ninni.species.block.WraptorEggBlock;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.CarpetBlock;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.HugeMushroomBlock;
import net.minecraft.world.level.block.RotatedPillarBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Species.MOD_ID);

    public static final RegistryObject<Block> WRAPTOR_EGG = BLOCKS.register("wraptor_egg", () -> new WraptorEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(10.0f, 1200.0f).sound(SpeciesSoundEvents.WRAPTOR_EGG).lightLevel(state -> 7)));
    public static final RegistryObject<Block> BIRT_DWELLING = BLOCKS.register("birt_dwelling", () -> new BirtDwellingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(2.0f).sound(SoundType.WOOD)));

    public static final RegistryObject<Block> RED_SUSPICIOUS_SAND = BLOCKS.register("red_suspicious_sand", () -> new BrushableBlock(Blocks.RED_SAND, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.SNARE).strength(0.25f).sound(SoundType.SUSPICIOUS_SAND).pushReaction(PushReaction.DESTROY), SoundEvents.BRUSH_SAND, SoundEvents.BRUSH_SAND_COMPLETED));
    public static final RegistryObject<Block> BONE_BARK = BLOCKS.register("bone_bark", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK)));
    public static final RegistryObject<Block> BONE_VERTEBRA = BLOCKS.register("bone_vertebra", () -> new RotatedPillarBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK)));
    public static final RegistryObject<Block> BONE_SPIKE = BLOCKS.register("bone_spike", () -> new BoneSpikeBlock(BlockBehaviour.Properties.copy(Blocks.BONE_BLOCK)));

    public static final RegistryObject<Block> TROOPER = BLOCKS.register("trooper", () -> new TrooperBlock(BlockBehaviour.Properties.copy(Blocks.SPRUCE_SAPLING)));
    public static final RegistryObject<Block> POTTED_TROOPER = BLOCKS.register("potted_trooper", () -> new PottedTrooperBlock(BlockBehaviour.Properties.copy(Blocks.POTTED_SPRUCE_SAPLING)));

    public static final RegistryObject<Block> PETRIFIED_EGG = BLOCKS.register("petrified_egg", () -> new PetrifiedEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5f).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryObject<Block> ALPHACENE_MOSS_BLOCK = BLOCKS.register("alphacene_moss_block", () -> new Block(BlockBehaviour.Properties.copy(Blocks.MOSS_BLOCK).sound(SpeciesSoundEvents.ALPHACENE_MOSS)));
    public static final RegistryObject<Block> ALPHACENE_MOSS_CARPET = BLOCKS.register("alphacene_moss_carpet", () -> new CarpetBlock(BlockBehaviour.Properties.copy(Blocks.MOSS_CARPET).sound(SpeciesSoundEvents.ALPHACENE_MOSS)));
    public static final RegistryObject<Block> ALPHACENE_GRASS_BLOCK = BLOCKS.register("alphacene_grass_block", () -> new AlphaceneGrassBlock(BlockBehaviour.Properties.copy(Blocks.GRASS_BLOCK).sound(SpeciesSoundEvents.ALPHACENE_GRASS)));
    public static final RegistryObject<Block> ALPHACENE_GRASS = BLOCKS.register("alphacene_grass", () -> new AlphaceneTallGrassBlock(BlockBehaviour.Properties.copy(Blocks.GRASS).sound(SpeciesSoundEvents.ALPHACENE_FOLIAGE)));
    public static final RegistryObject<Block> ALPHACENE_TALL_GRASS = BLOCKS.register("alphacene_tall_grass", () -> new DoublePlantBlock(BlockBehaviour.Properties.copy(Blocks.TALL_GRASS).sound(SpeciesSoundEvents.ALPHACENE_MOSS)));
    public static final RegistryObject<Block> ALPHACENE_MUSHROOM = BLOCKS.register("alphacene_mushroom", () -> new AlphaceneMushroomBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM).lightLevel(state -> 1).emissiveRendering((blockState, blockGetter, blockPos) -> true)));
    public static final RegistryObject<Block> ALPHACENE_MUSHROOM_BLOCK = BLOCKS.register("alphacene_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM_BLOCK).lightLevel(state -> 1).emissiveRendering((blockState, blockGetter, blockPos) -> true)));
    public static final RegistryObject<Block> ALPHACENE_MUSHROOM_GROWTH = BLOCKS.register("alphacene_mushroom_growth", () -> new AlphaceneMushroomGrowthBlock(BlockBehaviour.Properties.copy(Blocks.BROWN_MUSHROOM).lightLevel(state -> 1).emissiveRendering((blockState, blockGetter, blockPos) -> true)));

    public static final RegistryObject<Block> CRUNCHER_EGG = BLOCKS.register("cruncher_egg", () -> new CruncherEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).randomTicks().strength(0.5f).sound(SoundType.METAL).noOcclusion()));
    public static final RegistryObject<Block> CRUNCHER_PELLET = BLOCKS.register("cruncher_pellet", () -> new CruncherPelletBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.SCULK)));

    public static final RegistryObject<Block> FROZEN_MEAT = BLOCKS.register("frozen_meat", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(0.5f).sound(SpeciesSoundEvents.FROZEN_MEAT)));
    public static final RegistryObject<Block> FROZEN_HAIR = BLOCKS.register("frozen_hair", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SpeciesSoundEvents.FROZEN_HAIR)));
    public static final RegistryObject<Block> ICHOR = BLOCKS.register("ichor", () -> new IchorBlock(BlockBehaviour.Properties.of().instabreak().sound(SoundType.SLIME_BLOCK).noCollission().pushReaction(PushReaction.DESTROY).noLootTable().noOcclusion()));

    public static final RegistryObject<Block> SPRINGLING_EGG = BLOCKS.register("springling_egg", () -> new SpringlingEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5f).sound(SoundType.METAL).noOcclusion()));


}
