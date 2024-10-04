package com.ninni.species.init;

import com.ninni.species.Species;
import com.ninni.species.world.gen.features.BirtDwellingLogDecorator;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesTreeDecorators {
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = DeferredRegister.create(ForgeRegistries.TREE_DECORATOR_TYPES, Species.MOD_ID);

    public static final RegistryObject<TreeDecoratorType<BirtDwellingLogDecorator>> BIRT_DWELLING = TREE_DECORATOR_TYPE.register("birt_dwelling", () -> new TreeDecoratorType<>(BirtDwellingLogDecorator.CODEC));

}
