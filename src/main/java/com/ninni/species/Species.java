package com.ninni.species;

import com.mojang.logging.LogUtils;
import com.ninni.species.client.ClientProxy;
import com.ninni.species.registry.*;
import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.server.disguise.DisguiseBehaviors;
import com.ninni.species.server.disguise.SpeciesIMCHandler;
import com.ninni.species.server.disguise.cosmetic.DisguiseCosmeticRegistry;
import com.ninni.species.server.disguise.cosmetic.DisguiseCosmeticsDemo;
import com.ninni.species.server.disguise.data.DataDrivenCosmetics;
import com.ninni.species.server.disguise.data.DisguiseDataReloadListener;
import com.ninni.species.server.disguise.demo.ContextualActionDemo;
import com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry;
import com.ninni.species.server.events.ForgeEvents;
import com.ninni.species.server.events.ModEvents;
import com.ninni.species.server.world.poi.SpeciesPointOfInterestTypes;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(Species.MOD_ID)
public class Species {
	public static final String MOD_ID = "species";
	public static final Logger LOGGER = LogUtils.getLogger();
	public static final List<Runnable> CALLBACKS = new ArrayList<>();
	public static CommonProxy PROXY = DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> CommonProxy::new);

	public Species() {
		IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		IEventBus eventBus = MinecraftForge.EVENT_BUS;
		modEventBus.addListener(this::clientSetup);
		modEventBus.addListener(this::commonSetup);
		// IMC handler fires after FMLCommonSetupEvent — built-in registries populate first.
		modEventBus.addListener(SpeciesIMCHandler::onIMC);

		SpeciesBlocks.BLOCKS.register(modEventBus);
		SpeciesBlockEntities.BLOCK_ENTITY_TYPES.register(modEventBus);
		SpeciesBiomeModifiers.BIOME_MODIFIER_SERIALIZERS.register(modEventBus);
		SpeciesCreativeModeTabs.CREATIVE_MODE_TABAS.register(modEventBus);
		SpeciesStatusEffects.MOB_EFFECTS.register(modEventBus);
		SpeciesEntityDataSerializers.ENTITY_DATA_SERIALIZERS.register(modEventBus);
		SpeciesEntities.ENTITY_TYPES.register(modEventBus);
		SpeciesFeatures.FEATURES.register(modEventBus);
		SpeciesItems.ITEMS.register(modEventBus);
		SpeciesPotions.POTIONS.register(modEventBus);
		SpeciesSoundEvents.SOUND_EVENTS.register(modEventBus);
		SpeciesStructureTypes.STRUCTURES.register(modEventBus);
		SpeciesStructurePieceTypes.STRUCTURE_PIECE_TYPES.register(modEventBus);
		SpeciesMemoryModuleTypes.MEMORY_MODULE_TYPES.register(modEventBus);
		SpeciesSensorTypes.SENSOR_TYPES.register(modEventBus);
		SpeciesParticles.PARTICLE_TYPES.register(modEventBus);
		SpeciesPointOfInterestTypes.POI_TYPES.register(modEventBus);
		SpeciesTreeDecorators.TREE_DECORATOR_TYPE.register(modEventBus);
		SpeciesBannerPatterns.BANNER_PATTERNS.register(modEventBus);
		SpeciesVillagerTypes.VILLAGER_TYPES.register(modEventBus);
		SpeciesEnchantments.ENCHANTMENTS.register(modEventBus);
		SpeciesPaintingVariants.PAINTING_VARIANTS.register(modEventBus);
		SpeciesRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
		SpeciesMenus.MENUS.register(modEventBus);
		PROXY.init();
		eventBus.register(new ModEvents());
		eventBus.register(new ForgeEvents());
		eventBus.register(this);

		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, SpeciesConfig.SERVER_SPEC);
	}

	private void commonSetup(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			PROXY.commonSetup();
			DisguiseBehaviors.registerDefaults();
			DisguiseTopologyRegistry.registerDefaults();
			DisguiseCosmeticRegistry.registerDefaults();
			DisguiseCosmeticsDemo.registerDemo();
			ContextualActionDemo.registerDemo();
			// Data-driven layer (datapack JSON via DisguiseDataReloadListener); per-type entries win.
			DisguiseCosmeticRegistry.registerGlobal(
					DataDrivenCosmetics.INSTANCE);
		});
	}

	/** Reload listener for datapack-driven disguise overrides; one shared instance for client+server. */
	public static final DisguiseDataReloadListener DISGUISE_DATA_LISTENER =
			new DisguiseDataReloadListener();

	public void clientSetup(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> PROXY.clientSetup());
	}

}
