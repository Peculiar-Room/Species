package com.ninni.species.block.entity;

import com.ninni.species.block.SpeciesBlocks;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.ninni.species.Species.MOD_ID;

public class SpeciesBlockEntities {
    public static final BlockEntityType<BirtDwellingBlockEntity> BIRT_DWELLING = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MOD_ID, "birt_dwelling"), FabricBlockEntityTypeBuilder.create(BirtDwellingBlockEntity::new, SpeciesBlocks.BIRT_DWELLING).build(null));
}
