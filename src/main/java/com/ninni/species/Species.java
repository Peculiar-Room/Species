package com.ninni.species;

import com.ninni.species.registry.SpeciesBiomeModifiers;
import com.ninni.species.registry.SpeciesBlocks;
import com.ninni.species.registry.SpeciesBlockEntities;
import com.ninni.species.client.particles.SpeciesParticles;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesStatusEffects;
import com.ninni.species.events.MiscEvents;
import com.ninni.species.events.MobEvents;
import com.ninni.species.registry.SpeciesCreativeModeTabs;
import com.ninni.species.registry.SpeciesEntityDataSerializers;
import com.ninni.species.registry.SpeciesFeatures;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesMemoryModuleTypes;
import com.ninni.species.registry.SpeciesNetwork;
import com.ninni.species.registry.SpeciesSensorTypes;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.registry.SpeciesStructurePieceTypes;
import com.ninni.species.registry.SpeciesTreeDecorators;
import com.ninni.species.registry.SpeciesStructureTypes;
import com.ninni.species.world.poi.SpeciesPointOfInterestTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(Species.MOD_ID)
public class Species {
	public static final String MOD_ID = "species";

	public Species() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus eventBus = MinecraftForge.EVENT_BUS;
		modEventBus.addListener(this::commonSetup);

		SpeciesBlocks.BLOCKS.register(modEventBus);
		SpeciesBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
		SpeciesBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
		SpeciesCreativeModeTabs.CREATIVE_MODE_TABAS.register(modEventBus);
		SpeciesStatusEffects.MOB_EFFECTS.register(modEventBus);
		SpeciesEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(modEventBus);
		SpeciesEntities.ENTITY_TYPES.register(modEventBus);
		SpeciesFeatures.FEATURES.register(modEventBus);
		SpeciesItems.ITEMS.register(modEventBus);
		SpeciesSoundEvents.SOUND_EVENTS.register(modEventBus);
		SpeciesStructureTypes.STRUCTURES.register(modEventBus);
		SpeciesStructurePieceTypes.STRUCTURE_PIECE_TYPES.register(modEventBus);
		SpeciesMemoryModuleTypes.MEMORY_MODULE_TYPES.register(modEventBus);
		SpeciesSensorTypes.SENSOR_TYPES.register(modEventBus);
		SpeciesParticles.PARTICLE_TYPES.register(modEventBus);
		SpeciesPointOfInterestTypes.POI_TYPES.register(modEventBus);
		SpeciesTreeDecorators.TREE_DECORATOR_TYPE.register(modEventBus);

		eventBus.register(this);
		eventBus.register(new MobEvents());
		eventBus.register(new MiscEvents());
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(SpeciesNetwork::init);
	}

}
