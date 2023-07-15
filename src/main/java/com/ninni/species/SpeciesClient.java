package com.ninni.species;

import com.google.common.reflect.Reflection;
import com.ninni.species.client.renderer.*;
import com.ninni.species.registry.SpeciesBlocks;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.client.particles.BirtdParticle;
import com.ninni.species.client.particles.SnoringParticle;
import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.client.renderer.LimpetRenderer;
import com.ninni.species.registry.SpeciesEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;

public class SpeciesClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        Reflection.initialize(SpeciesEntityModelLayers.class);
        EntityRendererRegistry.register(SpeciesEntities.WRAPTOR, WraptorRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.DEEPFISH, DeepfishRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.ROOMBUG, RoombugRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.BIRT, BirtRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.BIRT_EGG, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.LIMPET, LimpetRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.TREEPER, TreeperRenderer::new);

        ParticleFactoryRegistry.getInstance().register(SpeciesParticles.SNORING, SnoringParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SpeciesParticles.BIRTD, BirtdParticle.Factory::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
                SpeciesBlocks.BIRT_DWELLING
        );
    }
}
