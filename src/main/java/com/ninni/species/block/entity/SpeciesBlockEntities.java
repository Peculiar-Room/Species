package com.ninni.species.block.entity;

import com.ninni.species.Species;
import com.ninni.species.block.SpeciesBlocks;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, Species.MOD_ID);

    public static final RegistryObject<BlockEntityType<BirtDwellingBlockEntity>> BIRT_DWELLING = BLOCK_ENTITY_TYPES.register("birt_dwelling", () -> BlockEntityType.Builder.of(BirtDwellingBlockEntity::new, SpeciesBlocks.BIRT_DWELLING.get()).build(null));

}
