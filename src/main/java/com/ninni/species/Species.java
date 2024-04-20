package com.ninni.species;

import com.google.common.reflect.Reflection;
import com.ninni.species.criterion.SpeciesCriterion;
import com.ninni.species.data.CruncherPelletManager;
import com.ninni.species.data.GooberGooManager;
import com.ninni.species.entity.BirtEgg;
import com.ninni.species.registry.SpeciesBlockEntities;
import com.ninni.species.registry.SpeciesBlocks;
import com.ninni.species.registry.SpeciesCreativeModeTabs;
import com.ninni.species.registry.SpeciesDamageTypes;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesEntityDataSerializers;
import com.ninni.species.registry.SpeciesFeatures;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesMemoryModuleTypes;
import com.ninni.species.registry.SpeciesNetwork;
import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.registry.SpeciesSensorTypes;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.registry.SpeciesStatusEffects;
import com.ninni.species.registry.SpeciesTags;
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
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.Util;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.levelgen.GenerationStep;

public class Species implements ModInitializer {
	public static final String MOD_ID = "species";
	public static final ResourceLocation OPEN_CRUNCHER_SCREEN = new ResourceLocation(MOD_ID, "open_cruncher_screen");

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
			SpeciesDamageTypes.class,
			SpeciesStatusEffects.class,
			SpeciesParticles.class,
			SpeciesEntities.class,
			SpeciesStructures.class,
			SpeciesStructureTypes.class,
			SpeciesStructureSets.class,
			SpeciesStructurePieceTypes.class
		);
		SpeciesEntityDataSerializers.init();
		SpeciesFeatures.init();
		SpeciesSensorTypes.init();
		SpeciesMemoryModuleTypes.init();
		SpeciesTreeDecorators.init();
		SpeciesNetwork.commonInit();

		BiomeModifications.addFeature(BiomeSelectors.tag(SpeciesTags.BIRT_TREE_SPAWNS_IN), GenerationStep.Decoration.VEGETAL_DECORATION, SpeciesPlacedFeatures.BIRTED_BIRCH_TREES);
		BiomeModifications.addFeature(BiomeSelectors.tag(SpeciesTags.MAMMUTILATION_REMNANT_SPAWNS_IN), GenerationStep.Decoration.UNDERGROUND_DECORATION, SpeciesPlacedFeatures.MAMMUTILATION_REMNANT);

		DispenserBlock.registerBehavior(SpeciesItems.BIRT_EGG, new AbstractProjectileDispenseBehavior() {
			@Override
			protected Projectile getProjectile(Level world, Position position, ItemStack stack) {
				return Util.make(new BirtEgg(world, position.x(), position.y(), position.z()), entity -> entity.setItem(stack));
			}
		});

		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new CruncherPelletManager());
		ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(new GooberGooManager());
	}

}
