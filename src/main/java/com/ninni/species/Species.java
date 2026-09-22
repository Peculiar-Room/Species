package com.ninni.species;

import com.mojang.logging.LogUtils;
import com.ninni.species.client.ClientProxy;
import com.ninni.species.client.events.ClientEvents;
import com.ninni.species.registry.*;
import com.ninni.species.server.criterion.SpeciesCriteriaTriggers;
import com.ninni.species.server.events.ForgeEvents;
import com.ninni.species.server.events.ModEvents;
import com.ninni.species.server.world.poi.SpeciesPointOfInterestTypes;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;
import net.neoforged.neoforge.common.NeoForge;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(Species.MOD_ID)
public class Species {
	public static final String MOD_ID = "species";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final List<Runnable> CALLBACKS = new ArrayList<>();
	public static final CommonProxy PROXY = (FMLEnvironment.dist == Dist.CLIENT ? new ClientProxy() : new CommonProxy());

	public Species(IEventBus modEventBus, ModContainer modContainer) {
		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::commonSetup);

		SpeciesBlocks.BLOCKS.register(modEventBus);
		SpeciesBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
		SpeciesBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
		SpeciesCreativeModeTabs.CREATIVE_MODE_TABS.register(modEventBus);
		SpeciesStatusEffects.MOB_EFFECTS.register(modEventBus);
		SpeciesEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(modEventBus);
		SpeciesEntities.ENTITY_TYPES.register(modEventBus);
		SpeciesFeatures.FEATURES.register(modEventBus);
		SpeciesSoundEvents.SOUND_EVENTS.register(modEventBus);
		SpeciesItems.ITEMS.register(modEventBus);
		SpeciesPotions.POTIONS.register(modEventBus);
		SpeciesStructureTypes.STRUCTURES.register(modEventBus);
		SpeciesStructurePieceTypes.STRUCTURE_PIECE_TYPES.register(modEventBus);
		SpeciesMemoryModuleTypes.MEMORY_MODULE_TYPES.register(modEventBus);
		SpeciesSensorTypes.SENSOR_TYPES.register(modEventBus);
		SpeciesParticles.PARTICLE_TYPES.register(modEventBus);
		SpeciesPointOfInterestTypes.POI_TYPES.register(modEventBus);
		SpeciesTreeDecorators.TREE_DECORATOR_TYPE.register(modEventBus);
		SpeciesVillagerTypes.VILLAGER_TYPES.register(modEventBus);
		SpeciesPaintingVariants.addPaintings();
		SpeciesRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
		SpeciesDataComponents.DATA_COMPONENTS.register(modEventBus);
		SpeciesMenus.MENUS.register(modEventBus);
		SpeciesCriterion.TRIGGER_TYPES.register(modEventBus);

		if (FMLEnvironment.dist == Dist.CLIENT) {
			modContainer.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
			modEventBus.addListener(ClientEvents::onRegisterSpectatorShaders);
			modEventBus.addListener(ClientEvents::registerKeys);
			modEventBus.addListener(ClientEvents::registerEntityLayers);
			modEventBus.addListener(ClientEvents::registerEntityRenderers);
			modEventBus.addListener(ClientEvents::registerBlockEntityRenderers);
			modEventBus.addListener(ClientEvents::registerParticleTypes);
			modEventBus.addListener(ClientEvents::registerItemColors);
			modEventBus.addListener(ClientEvents::registerCreativeModeTab);
			IEventBus forgeBus = NeoForge.EVENT_BUS;
			forgeBus.addListener(ClientEvents::clientTick);
			forgeBus.addListener(ClientEvents::preRenderGuiOverlay);
			forgeBus.addListener(ClientEvents::computeCameraAngles);
			forgeBus.addListener(ClientEvents::postRenderGuiOverlay);
		}

		//modEventBus.register(new ModEvents());
		//modEventBus.register(new ForgeEvents());
		//modEventBus.register(this);
		PROXY.init();
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> PROXY.commonSetup());
	}

	public void clientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> PROXY.clientSetup());
	}

}
