package com.ninni.species;

import com.google.common.reflect.Reflection;
import com.ninni.species.block.SpeciesBlocks;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.client.particles.BirtdParticle;
import com.ninni.species.client.particles.SnoringParticle;
import com.ninni.species.client.particles.SpeciesParticles;
import com.ninni.species.client.renderer.*;
import com.ninni.species.entity.SpeciesEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;

public class SpeciesClient implements ClientModInitializer {

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onInitializeClient() {
        Reflection.initialize(SpeciesEntityModelLayers.class);
        EntityRendererRegistry.register(SpeciesEntities.WRAPTOR, WraptorEntityRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.DEEPFISH, DeepfishEntityRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.ROOMBUG, RoombugEntityRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.BIRT, BirtEntityRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.BIRT_EGG, FlyingItemEntityRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.LIMPET, LimpetEntityRenderer::new);

        ParticleFactoryRegistry.getInstance().register(SpeciesParticles.SNORING, SnoringParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SpeciesParticles.BIRTD, BirtdParticle.Factory::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutout(),
                SpeciesBlocks.BIRT_DWELLING
        );
    }
}
