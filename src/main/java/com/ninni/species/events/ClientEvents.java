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
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {

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
