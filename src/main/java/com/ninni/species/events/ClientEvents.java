package com.ninni.species.events;

import com.ninni.species.Species;
import com.ninni.species.client.model.entity.BirtEntityModel;
import com.ninni.species.client.model.entity.DeepfishEntityModel;
import com.ninni.species.client.model.entity.LimpetEntityModel;
import com.ninni.species.client.model.entity.RoombugEntityModel;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.client.model.entity.WraptorEntityModel;
import com.ninni.species.client.particles.BirtdParticle;
import com.ninni.species.client.particles.SnoringParticle;
import com.ninni.species.client.particles.SpeciesParticles;
import com.ninni.species.client.renderer.BirtEntityRenderer;
import com.ninni.species.client.renderer.DeepfishEntityRenderer;
import com.ninni.species.client.renderer.LimpetEntityRenderer;
import com.ninni.species.client.renderer.RoombugEntityRenderer;
import com.ninni.species.client.renderer.WraptorEntityRenderer;
import com.ninni.species.entity.SpeciesEntities;
import com.ninni.species.item.SpeciesItems;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.antlr.v4.runtime.atn.SemanticContext;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

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
        event.registerSpriteSet(SpeciesParticles.BIRTD.get(), BirtdParticle.Factory::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SpeciesEntities.WRAPTOR.get(), WraptorEntityRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.ROOMBUG.get(), RoombugEntityRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.DEEPFISH.get(), DeepfishEntityRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.BIRT.get(), BirtEntityRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.BIRT_EGG.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.LIMPET.get(), LimpetEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SpeciesEntityModelLayers.LIMPET, LimpetEntityModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.DEEPFISH, DeepfishEntityModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.ROOMBUG, RoombugEntityModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.BIRT, BirtEntityModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.WRAPTOR, WraptorEntityModel::getLayerDefinition);
    }

}
