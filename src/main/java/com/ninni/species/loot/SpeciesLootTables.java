package com.ninni.species.loot;

import com.ninni.species.Species;
import net.minecraft.resources.ResourceLocation;

public class SpeciesLootTables {
    public static final ResourceLocation WRAPTOR_COOP_CHEST = create("chests/wraptor_coop_chest");

    private static ResourceLocation create(String id) {
        return new ResourceLocation(Species.MOD_ID, id);
    }
}
