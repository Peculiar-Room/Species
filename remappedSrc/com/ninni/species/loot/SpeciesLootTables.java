package com.ninni.species.loot;

import com.ninni.species.Species;
import net.minecraft.util.Identifier;

public class SpeciesLootTables {
    public static final Identifier WRAPTOR_COOP_CHEST = create("chests/wraptor_coop_chest");

    private static Identifier create(String id) {
        return new Identifier(Species.MOD_ID, id);
    }
}
