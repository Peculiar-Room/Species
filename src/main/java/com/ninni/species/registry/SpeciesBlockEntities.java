package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.block.entity.*;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.fml.common.EventBusSubscriber;

public class SpeciesBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, Species.MOD_ID);

    //Wave 1
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BirtDwellingBlockEntity>> BIRT_DWELLING = BLOCK_ENTITY_TYPES.register("birt_dwelling", () -> BlockEntityType.Builder.of(BirtDwellingBlockEntity::new, SpeciesBlocks.BIRT_DWELLING.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<BirtdayCakeBlockEntity>> BIRTDAY_CAKE = BLOCK_ENTITY_TYPES.register("birtday_cake", () -> BlockEntityType.Builder.of(BirtdayCakeBlockEntity::new, SpeciesBlocks.BIRTDAY_CAKE.get()).build(null));

    //Wave 2
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CruncherPelletBlockEntity>> CRUNCHER_PELLET = BLOCK_ENTITY_TYPES.register("cruncher_pellet", () -> BlockEntityType.Builder.of(CruncherPelletBlockEntity::new, SpeciesBlocks.CRUNCHER_PELLET.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<CruncherEggBlockEntity>> CRUNCHER_EGG = BLOCK_ENTITY_TYPES.register("cruncher_egg", () -> BlockEntityType.Builder.of(CruncherEggBlockEntity::new, SpeciesBlocks.CRUNCHER_EGG.get()).build(null));

    //Wave 3
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<MobHeadBlockEntity>> MOB_HEAD = BLOCK_ENTITY_TYPES.register("mob_head", () -> BlockEntityType.Builder.of(MobHeadBlockEntity::new, SpeciesBlocks.GHOUL_HEAD.get(), SpeciesBlocks.GHOUL_WALL_HEAD.get(), SpeciesBlocks.WICKED_CANDLE.get(), SpeciesBlocks.WICKED_WALL_CANDLE.get(), SpeciesBlocks.QUAKE_HEAD.get(), SpeciesBlocks.QUAKE_WALL_HEAD.get(), SpeciesBlocks.BEWEREAGER_HEAD.get(), SpeciesBlocks.BEWEREAGER_WALL_HEAD.get()).build(null));

    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpectraliburBlockEntity>> SPECTRALIBUR = BLOCK_ENTITY_TYPES.register("spectralibur", () -> BlockEntityType.Builder.of(SpectraliburBlockEntity::new, SpeciesBlocks.SPECTRALIBUR.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<SpeclightBlockEntity>> SPECLIGHT = BLOCK_ENTITY_TYPES.register("speclight", () -> BlockEntityType.Builder.of(SpeclightBlockEntity::new, SpeciesBlocks.SPECLIGHT.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<ChaindelierBlockEntity>> CHAINDELIER = BLOCK_ENTITY_TYPES.register("chaindelier", () -> BlockEntityType.Builder.of(ChaindelierBlockEntity::new, SpeciesBlocks.CHAINDELIER.get()).build(null));
    public static final DeferredHolder<BlockEntityType<?>, BlockEntityType<HopelightBlockEntity>> HOPELIGHT = BLOCK_ENTITY_TYPES.register("hopelight", () -> BlockEntityType.Builder.of(HopelightBlockEntity::new, SpeciesBlocks.HOPELIGHT.get()).build(null));

}
