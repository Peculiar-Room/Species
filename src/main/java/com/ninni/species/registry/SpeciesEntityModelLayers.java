package com.ninni.species.registry;

import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static com.ninni.species.Species.MOD_ID;

@OnlyIn(Dist.CLIENT)
public interface SpeciesEntityModelLayers {

    ModelLayerLocation WRAPTOR = main("wraptor");
    ModelLayerLocation DEEPFISH = main("deepfish");
    ModelLayerLocation ROOMBUG = main("roombug");
    ModelLayerLocation BIRT = main("birt");
    ModelLayerLocation LIMPET = main("limpet");
    ModelLayerLocation TREEPER = main("treeper");
    ModelLayerLocation TROOPER = main("trooper");
    ModelLayerLocation GOOBER = main("goober");
    ModelLayerLocation GOOBER_GOO = main("goober_goo");
    ModelLayerLocation CRUNCHER = main("cruncher");
    ModelLayerLocation MAMMUTILATION = main("mammutilation");
    ModelLayerLocation SPRINGLING = main("springling");

    private static ModelLayerLocation register(String id, String name) {
        return new ModelLayerLocation(new ResourceLocation(MOD_ID, id), name);
    }

    private static ModelLayerLocation main(String id) {
        return register(id, "main");
    }
}
