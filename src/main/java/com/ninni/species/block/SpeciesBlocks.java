package com.ninni.species.block;

import com.ninni.species.Species;
import com.ninni.species.sound.SpeciesBlockSoundGroup;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesBlocks {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, Species.MOD_ID);

    public static final RegistryObject<Block> WRAPTOR_EGG = BLOCKS.register("wraptor_egg", () -> new WraptorEggBlock(BlockBehaviour.Properties.of().mapColor(MapColor.COLOR_ORANGE).requiresCorrectToolForDrops().strength(10.0f, 1200.0f).sound(SpeciesBlockSoundGroup.WRAPTOR_EGG).lightLevel(state -> 7)));
    public static final RegistryObject<Block> BIRT_DWELLING = BLOCKS.register("birt_dwelling", () -> new BirtDwellingBlock(BlockBehaviour.Properties.of().mapColor(MapColor.SAND).strength(2.0f).sound(SoundType.WOOD)));

}
