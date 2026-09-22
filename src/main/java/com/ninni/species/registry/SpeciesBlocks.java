package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.block.*;
import com.ninni.species.server.block.property.SpeciesNoteBlockInstrument;
import com.ninni.species.server.block.property.SpeciesProperties;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;
import net.minecraft.world.level.material.PushReaction;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.ToIntFunction;

public class SpeciesBlocks {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Species.MOD_ID);

    //UPDATE 1
    public static final DeferredBlock<Block> WRAPTOR_EGG = BLOCKS.register("wraptor_egg", () -> new WraptorEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(10.0f, 1200.0f).sound(SpeciesSoundEvents.WRAPTOR_EGG_BLOCK).lightLevel(state -> 7)));
    public static final DeferredBlock<Block> BIRT_DWELLING = BLOCKS.register("birt_dwelling", () -> new BirtDwellingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(2.0f).sound(SoundType.WOOD)));
    public static final DeferredBlock<Block> BIRTDAY_CAKE = BLOCKS.register("birtday_cake", () -> new BirtdayCakeBlock(BlockBehaviour.Properties.of().strength(10.0f, 1200.0f).sound(SpeciesSoundEvents.BIRTDAY_CAKE).lightLevel(state -> state.getValue(BirtdayCakeBlock.LIT) ? 11 : 0)));

    //UPDATE 2
    public static final DeferredBlock<Block> RED_SUSPICIOUS_SAND = BLOCKS.register("red_suspicious_sand", () -> new BrushableBlock(Blocks.RED_SAND, SoundEvents.BRUSH_SAND, SoundEvents.BRUSH_SAND_COMPLETED, BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).instrument(NoteBlockInstrument.SNARE).strength(0.25f).sound(SoundType.SUSPICIOUS_SAND).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> BONE_BARK = BLOCKS.register("bone_bark", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BONE_BLOCK)));
    public static final DeferredBlock<Block> BONE_VERTEBRA = BLOCKS.register("bone_vertebra", () -> new RotatedPillarBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BONE_BLOCK)));
    public static final DeferredBlock<Block> BONE_SPIKE = BLOCKS.register("bone_spike", () -> new BoneSpikeBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BONE_BLOCK)));

    public static final DeferredBlock<Block> TROOPER = BLOCKS.register("trooper", () -> new TrooperBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SPRUCE_SAPLING)));
    public static final DeferredBlock<Block> POTTED_TROOPER = BLOCKS.register("potted_trooper", () -> new PottedTrooperBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.POTTED_SPRUCE_SAPLING)));

    public static final DeferredBlock<Block> PETRIFIED_EGG = BLOCKS.register("petrified_egg", () -> new PetrifiedEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_GREEN).strength(0.5f).sound(SoundType.METAL).noOcclusion()));
    public static final DeferredBlock<Block> ALPHACENE_MOSS_BLOCK = BLOCKS.register("alphacene_moss_block", () -> new Block(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_BLOCK).sound(SpeciesSoundEvents.ALPHACENE_MOSS)));
    public static final DeferredBlock<Block> ALPHACENE_MOSS_CARPET = BLOCKS.register("alphacene_moss_carpet", () -> new CarpetBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.MOSS_CARPET).sound(SpeciesSoundEvents.ALPHACENE_MOSS)));
    public static final DeferredBlock<Block> ALPHACENE_GRASS_BLOCK = BLOCKS.register("alphacene_grass_block", () -> new AlphaceneGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.GRASS_BLOCK).sound(SpeciesSoundEvents.ALPHACENE_GRASS)));
    public static final DeferredBlock<Block> ALPHACENE_GRASS = BLOCKS.register("alphacene_grass", () -> new AlphaceneTallGrassBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.SHORT_GRASS).sound(SpeciesSoundEvents.ALPHACENE_FOLIAGE)));
    public static final DeferredBlock<Block> ALPHACENE_TALL_GRASS = BLOCKS.register("alphacene_tall_grass", () -> new DoublePlantBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.TALL_GRASS).sound(SpeciesSoundEvents.ALPHACENE_MOSS)));
    public static final DeferredBlock<Block> ALPHACENE_MUSHROOM = BLOCKS.register("alphacene_mushroom", () -> new AlphaceneMushroomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM).lightLevel(state -> 1).emissiveRendering((blockState, blockGetter, blockPos) -> true)));
    public static final DeferredBlock<Block> ALPHACENE_MUSHROOM_BLOCK = BLOCKS.register("alphacene_mushroom_block", () -> new HugeMushroomBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM_BLOCK).lightLevel(state -> 1).emissiveRendering((blockState, blockGetter, blockPos) -> true)));
    public static final DeferredBlock<Block> ALPHACENE_MUSHROOM_GROWTH = BLOCKS.register("alphacene_mushroom_growth", () -> new AlphaceneMushroomGrowthBlock(BlockBehaviour.Properties.ofFullCopy(Blocks.BROWN_MUSHROOM).lightLevel(state -> 1).emissiveRendering((blockState, blockGetter, blockPos) -> true)));

    public static final DeferredBlock<Block> CRUNCHER_EGG = BLOCKS.register("cruncher_egg", () -> new CruncherEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_PURPLE).randomTicks().strength(0.5f).sound(SoundType.METAL).noOcclusion()));
    public static final DeferredBlock<Block> CRUNCHER_PELLET = BLOCKS.register("cruncher_pellet", () -> new CruncherPelletBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5F).sound(SoundType.SCULK)));

    public static final DeferredBlock<Block> FROZEN_MEAT = BLOCKS.register("frozen_meat", () -> new RotatedPillarBlock(BlockBehaviour.Properties.of().mapColor(MapColor.TERRACOTTA_RED).strength(0.5f).sound(SpeciesSoundEvents.FROZEN_MEAT)));
    public static final DeferredBlock<Block> FROZEN_HAIR = BLOCKS.register("frozen_hair", () -> new Block(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_BROWN).strength(0.5f).sound(SpeciesSoundEvents.FROZEN_HAIR)));
    public static final DeferredBlock<Block> ICHOR = BLOCKS.register("ichor", () -> new IchorBlock(BlockBehaviour.Properties.of().instabreak().sound(SoundType.SLIME_BLOCK).noCollission().pushReaction(PushReaction.DESTROY).noLootTable().noOcclusion()));

    public static final DeferredBlock<Block> SPRINGLING_EGG = BLOCKS.register("springling_egg", () -> new SpringlingEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_YELLOW).strength(0.5f).sound(SoundType.METAL).noOcclusion()));

    //UPDATE 3

    public static final DeferredBlock<Block> GHOUL_HEAD = BLOCKS.register("ghoul_head", () -> new MobHeadBlock(MobHeadBlock.Types.GHOUL, BlockBehaviour.Properties.of().instrument(SpeciesNoteBlockInstrument.GHOUL.get()).strength(1.0F).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> GHOUL_WALL_HEAD = BLOCKS.register("ghoul_wall_head", () -> new WallMobHeadBlock(MobHeadBlock.Types.GHOUL, BlockBehaviour.Properties.of().dropsLike(GHOUL_HEAD.get()).strength(1.0F).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> KINETIC_CORE = BLOCKS.register("kinetic_core", () -> new KineticCoreBlock(BlockBehaviour.Properties.of().strength(1.5f).instrument(SpeciesNoteBlockInstrument.QUAKE_SYNTH.get()).sound(SoundType.ANVIL).requiresCorrectToolForDrops().noOcclusion()));
    public static final DeferredBlock<Block> QUAKE_HEAD = BLOCKS.register("quake_head", () -> new MobHeadBlock(MobHeadBlock.Types.QUAKE, BlockBehaviour.Properties.of().instrument(SpeciesNoteBlockInstrument.QUAKE.get()).strength(1.0F).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> QUAKE_WALL_HEAD = BLOCKS.register("quake_wall_head", () -> new WallMobHeadBlock(MobHeadBlock.Types.QUAKE, BlockBehaviour.Properties.of().dropsLike(QUAKE_HEAD.get()).strength(1.0F).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> SPECTRALIBUR = BLOCKS.register("spectralibur", () -> new SpectraliburBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(75.0F, 1200.0F).sound(SoundType.METAL).pushReaction(PushReaction.IGNORE).noOcclusion()));
    public static final DeferredBlock<Block> SPECTRALIBUR_PEDESTAL = BLOCKS.register("spectralibur_pedestal", () -> new SpectraliburPedestalBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_LIGHT_GRAY).strength(75.0F, 1200.0F).sound(SoundType.METAL).lightLevel(litBlockEmission(10, SpeciesProperties.ACTIVE)).noOcclusion()));
    public static final DeferredBlock<Block> SPECLIGHT = BLOCKS.register("speclight", () -> new SpeclightBlock(BlockBehaviour.Properties.of().instabreak().noOcclusion().pushReaction(PushReaction.DESTROY).sound(SpeciesSoundEvents.SPECLIGHT).lightLevel(litBlockEmission(8, BlockStateProperties.POWERED))));
    public static final DeferredBlock<Block> CHAINDELIER = BLOCKS.register("chaindelier", () -> new ChaindelierBlock(BlockBehaviour.Properties.of().requiresCorrectToolForDrops().strength(5.0F, 6.0F).sound(SpeciesSoundEvents.SPECLIGHT).noOcclusion().pushReaction(PushReaction.DESTROY).lightLevel(value -> 13)));
    public static final DeferredBlock<Block> HOPELIGHT = BLOCKS.register("hopelight", () -> new HopelightBlock(BlockBehaviour.Properties.of().instabreak().sound(SpeciesSoundEvents.HOPELIGHT).noOcclusion().noCollission().pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> WICKED_CANDLE = BLOCKS.register("wicked_candle", () -> new MobHeadBlock(MobHeadBlock.Types.WICKED, BlockBehaviour.Properties.of().instrument(SpeciesNoteBlockInstrument.WICKED.get()).strength(1.0F).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> WICKED_WALL_CANDLE = BLOCKS.register("wicked_wall_candle", () -> new WallMobHeadBlock(MobHeadBlock.Types.WICKED, BlockBehaviour.Properties.of().dropsLike(WICKED_CANDLE.get()).strength(1.0F).pushReaction(PushReaction.DESTROY)));

    public static final DeferredBlock<Block> CRANKTRAP = BLOCKS.register("cranktrap", () -> new CranktrapBlock(BlockBehaviour.Properties.of().strength(1.5f).instrument(NoteBlockInstrument.IRON_XYLOPHONE).sound(SpeciesSoundEvents.CRANKTRAP).requiresCorrectToolForDrops().noCollission().noOcclusion()));
    public static final DeferredBlock<Block> BEWEREAGER_HEAD = BLOCKS.register("bewereager_head", () -> new MobHeadBlock(MobHeadBlock.Types.BEWEREAGER, BlockBehaviour.Properties.of().instrument(SpeciesNoteBlockInstrument.BEWEREAGER.get()).strength(1.0F).pushReaction(PushReaction.DESTROY)));
    public static final DeferredBlock<Block> BEWEREAGER_WALL_HEAD = BLOCKS.register("bewereager_wall_head", () -> new WallMobHeadBlock(MobHeadBlock.Types.BEWEREAGER, BlockBehaviour.Properties.of().dropsLike(BEWEREAGER_HEAD.get()).strength(1.0F).pushReaction(PushReaction.DESTROY)));


    private static ToIntFunction<BlockState> litBlockEmission(int i, BooleanProperty property) {
        return (state) -> state.getValue(property) ? i : 0;
    }
}
