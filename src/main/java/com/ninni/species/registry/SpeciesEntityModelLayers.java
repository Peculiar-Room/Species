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

    ModelLayerLocation WRAPTOR = main("wraptor", WraptorModel::getLayerDefinition);
    ModelLayerLocation DEEPFISH = main("deepfish", DeepfishModel::getLayerDefinition);
    ModelLayerLocation ROOMBUG = main("roombug", RoombugModel::getLayerDefinition);
    ModelLayerLocation BIRT = main("birt", BirtModel::getLayerDefinition);
    ModelLayerLocation LIMPET = main("limpet", LimpetModel::getLayerDefinition);
    ModelLayerLocation TREEPER = main("treeper", TreeperModel::getLayerDefinition);
    ModelLayerLocation TREEPER_SAPLING = main("treeper_sapling", TreeperSaplingModel::getLayerDefinition);

    private static ModelLayerLocation register(String id, String name, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        ModelLayerLocation layer = new ModelLayerLocation(new ResourceLocation(MOD_ID, id), name);
        EntityModelLayerRegistry.registerModelLayer(layer, provider);
        return layer;
    }

    private static ModelLayerLocation main(String id, EntityModelLayerRegistry.TexturedModelDataProvider provider) {
        return register(id, "main", provider);
    }
}
