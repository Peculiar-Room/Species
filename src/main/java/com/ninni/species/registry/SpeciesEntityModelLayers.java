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
    ModelLayerLocation STACKATICK = main("stackatick");
    ModelLayerLocation BIRT = main("birt");
    ModelLayerLocation LIMPET = main("limpet");
    ModelLayerLocation TREEPER = main("treeper");
    ModelLayerLocation TROOPER = main("trooper");
    ModelLayerLocation GOOBER = main("goober");
    ModelLayerLocation GOOBER_GOO = main("goober_goo");
    ModelLayerLocation CRUNCHER = main("cruncher");
    ModelLayerLocation MAMMUTILATION = main("mammutilation");
    ModelLayerLocation SPRINGLING = main("springling");
    ModelLayerLocation GHOUL = main("ghoul");
    ModelLayerLocation GHOUL_HEAD = main("ghoul_head");
    ModelLayerLocation QUAKE = main("quake");
    ModelLayerLocation QUAKE_HEAD = main("quake_head");
    ModelLayerLocation DEFLECTOR_DUMMY = main("deflector_dummy");
    ModelLayerLocation BEWEREAGER = main("bewereager");
    ModelLayerLocation SPECTRE = main("spectre");
    ModelLayerLocation SABLE_SPECTRE = main("sable_spectre");
    ModelLayerLocation JOUSTING_SPECTRE = main("jousting_spectre");
    ModelLayerLocation WICKED = main("wicked");
    ModelLayerLocation WICKED_FIREBALL = main("wicked_fireball");
    ModelLayerLocation WICKED_CANDLE = main("wicked_candle");
    ModelLayerLocation BEWEREAGER_HEAD = main("bewereager_head");
    ModelLayerLocation LEAF_HANGER = main("leaf_hanger");
    ModelLayerLocation CLIFF_HANGER = main("cliff_hanger");
    ModelLayerLocation COIL_KNOT = main("coil_knot");
    ModelLayerLocation COIL = main("coil");

    private static ModelLayerLocation register(String id, String name) {
        return new ModelLayerLocation(new ResourceLocation(MOD_ID, id), name);
    }

    private static ModelLayerLocation main(String id) {
        return register(id, "main");
    }
}
