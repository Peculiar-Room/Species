package com.ninni.species.registry;

import com.ninni.species.block.entity.BirtDwellingBlockEntity;
import com.ninni.species.block.entity.CruncherEggBlockEntity;
import com.ninni.species.block.entity.CruncherPelletBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.ninni.species.Species.MOD_ID;

public class SpeciesBlockEntities {
    public static final BlockEntityType<BirtDwellingBlockEntity> BIRT_DWELLING = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(MOD_ID, "birt_dwelling"), FabricBlockEntityTypeBuilder.create(BirtDwellingBlockEntity::new, SpeciesBlocks.BIRT_DWELLING).build(null));
    public static final BlockEntityType<CruncherPelletBlockEntity> CRUNCHER_PELLET = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(MOD_ID, "cruncher_pellet"), FabricBlockEntityTypeBuilder.create(CruncherPelletBlockEntity::new, SpeciesBlocks.CRUNCHER_PELLET).build(null));
    public static final BlockEntityType<CruncherEggBlockEntity> CRUNCHER_EGG = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(MOD_ID, "cruncher_egg"), FabricBlockEntityTypeBuilder.create(CruncherEggBlockEntity::new, SpeciesBlocks.CRUNCHER_EGG).build(null));
}
