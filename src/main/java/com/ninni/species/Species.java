package com.ninni.species;

import com.google.common.reflect.Reflection;
import com.ninni.species.criterion.SpeciesCriterion;
import com.ninni.species.data.CruncherPelletManager;
import com.ninni.species.data.GooberGooManager;
import com.ninni.species.entity.BirtEgg;
import com.ninni.species.registry.*;
import com.ninni.species.world.gen.features.SpeciesPlacedFeatures;
import com.ninni.species.world.gen.features.SpeciesTreeDecorators;
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

import java.util.UUID;

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
		SpeciesNetwork.init();

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

		SpeciesDevelopers.developerUUIDS.put(UUID.fromString("2d173722-de6b-4bb8-b21b-b2843cfe395d"), SpeciesDevelopers.SpeciesDeveloperNames.NINNI);
		SpeciesDevelopers.developerUUIDS.put(UUID.fromString("f1fb25f4-60c4-4e21-b33c-59f0a2daf4b1"), SpeciesDevelopers.SpeciesDeveloperNames.REDA);
		SpeciesDevelopers.developerUUIDS.put(UUID.fromString("4a463319-625c-4b86-a4e7-8b700f023a60"), SpeciesDevelopers.SpeciesDeveloperNames.NOON);
		SpeciesDevelopers.developerUUIDS.put(UUID.fromString("603d30f1-77a1-4b88-b8c5-624a02feabcc"), SpeciesDevelopers.SpeciesDeveloperNames.BORNULHU);
		//SpeciesDevelopers.developerUUIDS.put(UUID.fromString(""), SpeciesDevelopers.SpeciesDeveloperNames.GLADOS); Glados does not own minecraft
		SpeciesDevelopers.developerUUIDS.put(UUID.fromString("81499a26-ba39-430e-8009-29ee87351c20"), SpeciesDevelopers.SpeciesDeveloperNames.ORCINUS);
		SpeciesDevelopers.developerUUIDS.put(UUID.fromString("0c22615f-a189-4f4e-85ae-79fd80c353c8"), SpeciesDevelopers.SpeciesDeveloperNames.VAKY);
		SpeciesDevelopers.developerUUIDS.put(UUID.fromString("aca529a2-1166-41aa-b304-209f06831998"), SpeciesDevelopers.SpeciesDeveloperNames.TAZZ);
		SpeciesDevelopers.developerUUIDS.put(UUID.fromString("f6dffbc0-746a-41fe-b0c1-20f9a596795a"), SpeciesDevelopers.SpeciesDeveloperNames.BUNTEN);
		SpeciesDevelopers.developerUUIDS.put(UUID.fromString("4f00e7fc-b325-4f16-88cf-80cd78733646"), SpeciesDevelopers.SpeciesDeveloperNames.EXCLAIM);
	}

}
