package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.block.entity.*;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Species.MOD_ID);

    //Wave 1
    public static final RegistryObject<BlockEntityType<BirtDwellingBlockEntity>> BIRT_DWELLING = BLOCK_ENTITY_TYPES.register("birt_dwelling", () -> BlockEntityType.Builder.of(BirtDwellingBlockEntity::new, SpeciesBlocks.BIRT_DWELLING.get()).build(null));

    //Wave 2
    public static final RegistryObject<BlockEntityType<CruncherPelletBlockEntity>> CRUNCHER_PELLET = BLOCK_ENTITY_TYPES.register("cruncher_pellet", () -> BlockEntityType.Builder.of(CruncherPelletBlockEntity::new, SpeciesBlocks.CRUNCHER_PELLET.get()).build(null));
    public static final RegistryObject<BlockEntityType<CruncherEggBlockEntity>> CRUNCHER_EGG = BLOCK_ENTITY_TYPES.register("cruncher_egg", () -> BlockEntityType.Builder.of(CruncherEggBlockEntity::new, SpeciesBlocks.CRUNCHER_EGG.get()).build(null));

    //Wave 3
    public static final RegistryObject<BlockEntityType<MobHeadBlockEntity>> MOB_HEAD = BLOCK_ENTITY_TYPES.register("mob_head", () -> BlockEntityType.Builder.of(MobHeadBlockEntity::new, SpeciesBlocks.GHOUL_HEAD.get(), SpeciesBlocks.GHOUL_WALL_HEAD.get(), SpeciesBlocks.WICKED_CANDLE.get(), SpeciesBlocks.WICKED_WALL_CANDLE.get(), SpeciesBlocks.QUAKE_HEAD.get(), SpeciesBlocks.QUAKE_WALL_HEAD.get(), SpeciesBlocks.BEWEREAGER_HEAD.get(), SpeciesBlocks.BEWEREAGER_WALL_HEAD.get()).build(null));

    public static final RegistryObject<BlockEntityType<SpectraliburBlockEntity>> SPECTRALIBUR = BLOCK_ENTITY_TYPES.register("spectralibur", () -> BlockEntityType.Builder.of(SpectraliburBlockEntity::new, SpeciesBlocks.SPECTRALIBUR.get()).build(null));
    public static final RegistryObject<BlockEntityType<SpeclightBlockEntity>> SPECLIGHT = BLOCK_ENTITY_TYPES.register("speclight", () -> BlockEntityType.Builder.of(SpeclightBlockEntity::new, SpeciesBlocks.SPECLIGHT.get()).build(null));
    public static final RegistryObject<BlockEntityType<ChaindelierBlockEntity>> CHAINDELIER = BLOCK_ENTITY_TYPES.register("chaindelier", () -> BlockEntityType.Builder.of(ChaindelierBlockEntity::new, SpeciesBlocks.CHAINDELIER.get()).build(null));
    public static final RegistryObject<BlockEntityType<HopelightBlockEntity>> HOPELIGHT = BLOCK_ENTITY_TYPES.register("hopelight", () -> BlockEntityType.Builder.of(HopelightBlockEntity::new, SpeciesBlocks.HOPELIGHT.get()).build(null));

}
