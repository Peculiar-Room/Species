package com.ninni.species.init;

import com.ninni.species.Species;
import net.minecraft.resources.ResourceLocation;

public class SpeciesLootTables {
    public static final ResourceLocation WRAPTOR_COOP_CHEST = create("chests/wraptor_coop_chest");
    public static final ResourceLocation PALEONTOLOGY_DIG_SITE_COMMON = create("archaeology/paleontology_dig_site/common");
    public static final ResourceLocation PALEONTOLOGY_DIG_SITE_RARE = create("archaeology/paleontology_dig_site/rare");
    public static final ResourceLocation PALEONTOLOGY_DIG_SITE_EPIC = create("archaeology/paleontology_dig_site/epic");

    private static ResourceLocation create(String id) {
        return new ResourceLocation(Species.MOD_ID, id);
    }
}
