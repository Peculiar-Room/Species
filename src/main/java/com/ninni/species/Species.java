package com.ninni.species;

import com.google.common.reflect.Reflection;
import com.ninni.species.block.SpeciesBlocks;
import com.ninni.species.block.entity.SpeciesBlockEntities;
import com.ninni.species.client.particles.SpeciesParticles;
import com.ninni.species.entity.BirtEggEntity;
import com.ninni.species.entity.SpeciesEntities;
import com.ninni.species.entity.effect.SpeciesStatusEffects;
import com.ninni.species.item.SpeciesItems;
import com.ninni.species.sound.SpeciesSoundEvents;
import com.ninni.species.structure.SpeciesStructurePieceTypes;
import com.ninni.species.structure.SpeciesStructureSets;
import com.ninni.species.tag.SpeciesTags;
import com.ninni.species.world.gen.features.SpeciesFeatures;
import com.ninni.species.world.gen.features.SpeciesTreeDecorators;
import com.ninni.species.world.gen.structure.SpeciesStructureTypes;
import com.ninni.species.world.gen.structure.SpeciesStructures;
import com.ninni.species.world.poi.SpeciesPointsOfInterests;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectors;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.Util;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;

public class Species implements ModInitializer {
	public static final String MOD_ID = "species";
	public static final CreativeModeTab ITEM_GROUP = FabricItemGroupBuilder.build(new ResourceLocation(MOD_ID, "item_group"), () -> new ItemStack(SpeciesItems.WRAPTOR_EGG));

	@SuppressWarnings("UnstableApiUsage")
	@Override
	public void onInitialize() {
		Reflection.initialize(
			SpeciesBlocks.class,
			SpeciesBlockEntities.class,
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
		SpeciesFeatures.init();
		SpeciesFeatures.BIRTED_BIRCH_TREES.unwrapKey().ifPresent(this::addFeature);

		DispenserBlock.registerBehavior(SpeciesItems.BIRT_EGG, new AbstractProjectileDispenseBehavior() {
			@Override
			protected Projectile getProjectile(Level world, Position position, ItemStack stack) {
				return Util.make(new BirtEggEntity(world, position.x(), position.y(), position.z()), entity -> entity.setItem(stack));
			}
		});
	}

	private void addFeature(ResourceKey<PlacedFeature> placedFeatureRegistryKey) {
		BiomeModifications.addFeature(BiomeSelectors.tag(SpeciesTags.BIRT_TREE_SPAWNS_IN), GenerationStep.Decoration.VEGETAL_DECORATION, placedFeatureRegistryKey);
	}
}
