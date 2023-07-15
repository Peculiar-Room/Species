package com.ninni.species.registry;

import com.ninni.species.client.model.entity.*;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;

import static com.ninni.species.Species.MOD_ID;

@Environment(EnvType.CLIENT)
public interface SpeciesEntityModelLayers {

    ModelLayerLocation WRAPTOR = main("wraptor", WraptorEntityModel::getLayerDefinition);
    ModelLayerLocation DEEPFISH = main("deepfish", DeepfishEntityModel::getLayerDefinition);
    ModelLayerLocation ROOMBUG = main("roombug", RoombugEntityModel::getLayerDefinition);
    ModelLayerLocation BIRT = main("birt", BirtEntityModel::getLayerDefinition);
    ModelLayerLocation LIMPET = main("limpet", LimpetEntityModel::getLayerDefinition);

    private static ModelLayerLocation register(String id, String name, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        ModelLayerLocation layer = new ModelLayerLocation(new ResourceLocation(MOD_ID, id), name);
        EntityModelLayerRegistry.registerModelLayer(layer, provider);
        return layer;
    }

    private static ModelLayerLocation main(String id, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        return register(id, "main", provider);
    }
}
