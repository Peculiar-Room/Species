package com.ninni.species;

import com.google.common.reflect.Reflection;
import com.ninni.species.registry.*;
import com.ninni.species.criterion.SpeciesCriterion;
import com.ninni.species.entity.BirtEgg;
import com.ninni.species.structure.SpeciesStructurePieceTypes;
import com.ninni.species.structure.SpeciesStructureSets;
import com.ninni.species.world.gen.features.SpeciesPlacedFeatures;
import com.ninni.species.world.gen.features.SpeciesTreeDecorators;
import com.ninni.species.world.gen.structure.SpeciesStructureTypes;
import com.ninni.species.world.gen.structure.SpeciesStructures;
import com.ninni.species.world.poi.SpeciesPointsOfInterests;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.minecraft.Util;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class Species implements ModInitializer {
	public static final String MOD_ID = "species";

	@Override
	public void onInitialize() {
		SpeciesCriterion.init();
		Reflection.initialize(
			SpeciesBlocks.class,
			SpeciesBlockEntities.class,
			SpeciesCreativeModeTabs.class,
			SpeciesPointsOfInterests.class,
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
		SpeciesTreeDecorators.init();
		SpeciesNetwork.commonInit();

		BiomeModifications.addFeature(BiomeSelectors.tag(SpeciesTags.BIRT_TREE_SPAWNS_IN), GenerationStep.Decoration.VEGETAL_DECORATION, SpeciesPlacedFeatures.BIRTED_BIRCH_TREES);
		BiomeModifications.addFeature(BiomeSelectors.tag(SpeciesTags.ORE_FROZEN_HAIR_SPAWNS_IN), GenerationStep.Decoration.UNDERGROUND_ORES, SpeciesPlacedFeatures.ORE_FROZEN_HAIR);
		BiomeModifications.addFeature(BiomeSelectors.tag(SpeciesTags.ORE_FROZEN_MEAT_SPAWNS_IN), GenerationStep.Decoration.UNDERGROUND_ORES, SpeciesPlacedFeatures.ORE_FROZEN_MEAT);

		DispenserBlock.registerBehavior(SpeciesItems.BIRT_EGG, new AbstractProjectileDispenseBehavior() {
			@Override
			protected Projectile getProjectile(Level world, Position position, ItemStack stack) {
				return Util.make(new BirtEgg(world, position.x(), position.y(), position.z()), entity -> entity.setItem(stack));
			}
		});
	}

	private void addFeature(ResourceKey<PlacedFeature> placedFeatureRegistryKey) {
		BiomeModifications.addFeature(BiomeSelectors.tag(SpeciesTags.BIRT_TREE_SPAWNS_IN), GenerationStep.Decoration.VEGETAL_DECORATION, placedFeatureRegistryKey);
	}
}
