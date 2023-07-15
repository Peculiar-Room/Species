package com.ninni.species.registry;

import com.ninni.species.block.entity.BirtDwellingBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;

import static com.ninni.species.Species.MOD_ID;

public class SpeciesBlockEntities {
    public static final BlockEntityType<BirtDwellingBlockEntity> BIRT_DWELLING = Registry.register(BuiltInRegistries.BLOCK_ENTITY_TYPE, new ResourceLocation(MOD_ID, "birt_dwelling"), FabricBlockEntityTypeBuilder.create(BirtDwellingBlockEntity::new, SpeciesBlocks.BIRT_DWELLING).build(null));
}
