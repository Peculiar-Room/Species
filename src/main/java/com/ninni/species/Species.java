package com.ninni.species;

import com.google.common.reflect.Reflection;
import com.ninni.species.block.SpeciesBlocks;
import com.ninni.species.client.particles.SpeciesParticles;
import com.ninni.species.entity.effect.SpeciesStatusEffects;
import com.ninni.species.entity.SpeciesEntities;
import com.ninni.species.item.SpeciesItems;
import com.ninni.species.sound.SpeciesSoundEvents;
import com.ninni.species.structure.SpeciesStructurePieceTypes;
import com.ninni.species.structure.SpeciesStructureSets;
import com.ninni.species.world.gen.structure.SpeciesStructureTypes;
import com.ninni.species.world.gen.structure.SpeciesStructures;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

public class Species implements ModInitializer {
	public static final String MOD_ID = "species";
	public static final ItemGroup ITEM_GROUP = FabricItemGroupBuilder.build(new Identifier(MOD_ID, "item_group"), () -> new ItemStack(SpeciesItems.WRAPTOR_EGG));

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void onInitialize() {
		Reflection.initialize(
			SpeciesBlocks.class,
			SpeciesItems.class,
			SpeciesSoundEvents.class,
			SpeciesStatusEffects.class,
			SpeciesParticles.class,
			SpeciesEntities.class,
			SpeciesStructures.class,
			SpeciesStructureTypes.class,
			SpeciesStructureSets.class,
			SpeciesStructurePieceTypes.class
		);
	}
}
