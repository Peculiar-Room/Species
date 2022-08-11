package com.ninni.species;

import com.google.common.reflect.Reflection;
import com.ninni.species.client.model.entity.SpeciesEntityModelLayers;
import com.ninni.species.client.renderer.WraptorEntityRenderer;
import com.ninni.species.entity.SpeciesEntities;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;

public class SpeciesClient implements ClientModInitializer {

    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void onInitializeClient() {
        Reflection.initialize(SpeciesEntityModelLayers.class);
        EntityRendererRegistry.register(SpeciesEntities.WRAPTOR, WraptorEntityRenderer::new);
    }
}
