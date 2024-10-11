package com.ninni.species.events;

import com.ninni.species.Species;
import com.ninni.species.client.model.entity.BirtEntityModel;
import com.ninni.species.client.model.entity.CruncherModel;
import com.ninni.species.client.model.entity.DeepfishEntityModel;
import com.ninni.species.client.model.entity.GooberGooModel;
import com.ninni.species.client.model.entity.GooberModel;
import com.ninni.species.client.model.entity.LimpetEntityModel;
import com.ninni.species.client.model.entity.MammutilationModel;
import com.ninni.species.client.model.entity.RoombugEntityModel;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.client.model.entity.SpringlingModel;
import com.ninni.species.client.model.entity.TreeperModel;
import com.ninni.species.client.model.entity.TrooperModel;
import com.ninni.species.client.model.entity.WraptorEntityModel;
import com.ninni.species.client.particles.AscendingDustParticle;
import com.ninni.species.client.particles.IchorBottleParticle;
import com.ninni.species.client.particles.IchorParticle;
import com.ninni.species.client.particles.PelletDripParticle;
import com.ninni.species.client.particles.RotatingParticle;
import com.ninni.species.client.particles.SnoringParticle;
import com.ninni.species.client.particles.SpeciesParticles;
import com.ninni.species.client.particles.TreeperLeafParticle;
import com.ninni.species.client.renderer.BirtRenderer;
import com.ninni.species.client.renderer.CruncherRenderer;
import com.ninni.species.client.renderer.DeepfishRenderer;
import com.ninni.species.client.renderer.GooberRenderer;
import com.ninni.species.client.renderer.LimpetRenderer;
import com.ninni.species.client.renderer.MammutilationRenderer;
import com.ninni.species.client.renderer.RoombugRenderer;
import com.ninni.species.client.renderer.SpringlingRenderer;
import com.ninni.species.client.renderer.TreeperRenderer;
import com.ninni.species.client.renderer.TrooperRenderer;
import com.ninni.species.client.renderer.WraptorRenderer;
import com.ninni.species.client.renderer.entity.GooberGooRenderer;
import com.ninni.species.entity.Springling;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesKeyMappings;
import com.ninni.species.registry.SpeciesNetwork;
import com.ninni.species.network.UpdateSpringlingDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        IEventBus eventBus = MinecraftForge.EVENT_BUS;
        eventBus.addListener((TickEvent.ClientTickEvent clientTickEvent) -> {
            Minecraft client = Minecraft.getInstance();
            Player player = client.player;
            if (player != null && player.getVehicle() instanceof Springling springling) {
                float extendedAmount = springling.getExtendedAmount();
                if (!SpeciesKeyMappings.RETRACT_KEY.isDown() && !springling.level().getBlockState(player.blockPosition().above(2)).isSolid() && SpeciesKeyMappings.EXTEND_KEY.isDown()) {
                    SpeciesNetwork.INSTANCE.sendToServer(new UpdateSpringlingDataPacket(0.1F, extendedAmount < springling.getMaxExtendedAmount()));
                }
                if (SpeciesKeyMappings.RETRACT_KEY.isDown() && !SpeciesKeyMappings.EXTEND_KEY.isDown()) {
                    SpeciesNetwork.INSTANCE.sendToServer(new UpdateSpringlingDataPacket(-0.25F, extendedAmount > 0));
                }
            }
        });
    }

    @SubscribeEvent
    public static void registerCreativeModeTab(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> key = event.getTabKey();
        MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = event.getEntries();
        if (key == CreativeModeTabs.NATURAL_BLOCKS) {
            entries.putAfter(new ItemStack(Items.HAY_BLOCK), new ItemStack(SpeciesItems.BIRT_DWELLING.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.SNIFFER_EGG), new ItemStack(SpeciesItems.WRAPTOR_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            entries.putAfter(new ItemStack(Items.MUSIC_DISC_RELIC), new ItemStack(SpeciesItems.MUSIC_DISC_DIAL.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            entries.putAfter(new ItemStack(Items.MUSIC_DISC_RELIC), new ItemStack(SpeciesItems.MUSIC_DISC_DIAL.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.PUFFERFISH_BUCKET), new ItemStack(SpeciesItems.DEEPFISH_BUCKET.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.COMBAT) {
            entries.putAfter(new ItemStack(Items.EGG), new ItemStack(SpeciesItems.BIRT_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.INGREDIENTS) {
            entries.putAfter(new ItemStack(Items.EGG), new ItemStack(SpeciesItems.BIRT_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.FOOD_AND_DRINKS) {
            entries.putAfter(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), new ItemStack(SpeciesItems.CRACKED_WRAPTOR_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.SPAWN_EGGS) {
            entries.putAfter(new ItemStack(Items.WITHER_SKELETON_SPAWN_EGG), new ItemStack(SpeciesItems.WRAPTOR_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.LLAMA_SPAWN_EGG), new ItemStack(SpeciesItems.LIMPET_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.DROWNED_SPAWN_EGG), new ItemStack(SpeciesItems.DEEPFISH_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.BEE_SPAWN_EGG), new ItemStack(SpeciesItems.BIRT_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.RABBIT_SPAWN_EGG), new ItemStack(SpeciesItems.ROOMBUG_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

    }

    @SubscribeEvent
    public static void registerParticleTypes(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SpeciesParticles.SNORING.get(), SnoringParticle.Factory::new);
        event.registerSpriteSet(SpeciesParticles.BIRTD.get(), RotatingParticle.BirtdFactory::new);
        event.registerSpriteSet(SpeciesParticles.FOOD.get(), RotatingParticle.FoodFactory::new);
        event.registerSpriteSet(SpeciesParticles.ASCENDING_DUST.get(), AscendingDustParticle.Factory::new);
        event.registerSpriteSet(SpeciesParticles.TREEPER_LEAF.get(), TreeperLeafParticle.Factory::new);
        event.registerSpriteSet(SpeciesParticles.YOUTH_POTION.get(), IchorBottleParticle.Factory::new);
        event.registerSpriteSet(SpeciesParticles.ICHOR.get(), IchorParticle.Factory::new);
        event.registerSpriteSet(SpeciesParticles.DRIPPING_PELLET_DRIP.get(), PelletDripParticle.PelletDripHangProvider::new);
        event.registerSpriteSet(SpeciesParticles.FALLING_PELLET_DRIP.get(), PelletDripParticle.PelletDripFallProvider::new);
        event.registerSpriteSet(SpeciesParticles.LANDING_PELLET_DRIP.get(), PelletDripParticle.PelletDripLandProvider::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SpeciesEntities.WRAPTOR.get(), WraptorRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.ROOMBUG.get(), RoombugRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.DEEPFISH.get(), DeepfishRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.BIRT.get(), BirtRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.BIRT_EGG.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.LIMPET.get(), LimpetRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.TREEPER.get(), TreeperRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.TROOPER.get(), TrooperRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.GOOBER.get(), GooberRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.GOOBER_GOO.get(), GooberGooRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.CRUNCHER.get(), CruncherRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.MAMMUTILATION.get(), MammutilationRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.SPRINGLING.get(), SpringlingRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.MAMMUTILATION_ICHOR.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.CRUNCHER_PELLET.get(), FallingBlockRenderer::new);
    }

    @SubscribeEvent
    public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SpeciesEntityModelLayers.LIMPET, LimpetEntityModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.DEEPFISH, DeepfishEntityModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.ROOMBUG, RoombugEntityModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.BIRT, BirtEntityModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.WRAPTOR, WraptorEntityModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.TREEPER, TreeperModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.TROOPER, TrooperModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.GOOBER, GooberModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.GOOBER_GOO, GooberGooModel::createLayer);
        event.registerLayerDefinition(SpeciesEntityModelLayers.CRUNCHER, CruncherModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.MAMMUTILATION, MammutilationModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.SPRINGLING, SpringlingModel::getLayerDefinition);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(SpeciesKeyMappings.EXTEND_KEY);
        event.register(SpeciesKeyMappings.RETRACT_KEY);
    }

}
