package com.ninni.species;

import com.ninni.species.init.SpeciesBlocks;
import com.ninni.species.init.SpeciesBlockEntities;
import com.ninni.species.client.particles.SpeciesParticles;
import com.ninni.species.init.SpeciesEntities;
import com.ninni.species.entity.effect.SpeciesStatusEffects;
import com.ninni.species.events.MiscEvents;
import com.ninni.species.events.MobEvents;
import com.ninni.species.init.SpeciesCreativeModeTabs;
import com.ninni.species.init.SpeciesEntityDataSerializers;
import com.ninni.species.init.SpeciesFeatures;
import com.ninni.species.init.SpeciesItems;
import com.ninni.species.init.SpeciesMemoryModuleTypes;
import com.ninni.species.init.SpeciesNetwork;
import com.ninni.species.init.SpeciesSensorTypes;
import com.ninni.species.init.SpeciesSoundEvents;
import com.ninni.species.init.SpeciesStructurePieceTypes;
import com.ninni.species.init.SpeciesTreeDecorators;
import com.ninni.species.init.SpeciesStructureTypes;
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
