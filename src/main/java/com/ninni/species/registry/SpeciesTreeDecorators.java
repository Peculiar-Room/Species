package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.world.gen.features.BirtDwellingLogDecorator;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpeciesTreeDecorators {
    public static final DeferredRegister<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = DeferredRegister.create(BuiltInRegistries.TREE_DECORATOR_TYPE, Species.MOD_ID);

    public static final DeferredHolder<TreeDecoratorType<?>, TreeDecoratorType<BirtDwellingLogDecorator>> BIRT_DWELLING = TREE_DECORATOR_TYPE.register("birt_dwelling", () -> new TreeDecoratorType<>(BirtDwellingLogDecorator.CODEC));

}
