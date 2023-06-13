package com.ninni.species.item;

import com.ninni.species.Species;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;

import static com.ninni.species.item.SpeciesItems.*;

public class SpeciesCreativeModeTab {

    public static final CreativeModeTab ITEM_GROUP = register("item_group", FabricItemGroup.builder().icon(WRAPTOR_EGG::getDefaultInstance).title(Component.translatable("species.item_group")).displayItems((featureFlagSet, output) -> {
                output.accept(WRAPTOR_SPAWN_EGG);
                output.accept(WRAPTOR_EGG);
                output.accept(CRACKED_WRAPTOR_EGG);

                output.accept(DEEPFISH_SPAWN_EGG);
                output.accept(DEEPFISH_BUCKET);

                output.accept(ROOMBUG_SPAWN_EGG);

                output.accept(BIRT_SPAWN_EGG);
                output.accept(BIRT_EGG);
                output.accept(BIRT_DWELLING);
                output.accept(MUSIC_DISC_DIAL);

                output.accept(LIMPET_SPAWN_EGG);
            }).build()
    );

    private static CreativeModeTab register(String id, CreativeModeTab tab) {
        return Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, new ResourceLocation(Species.MOD_ID, id), tab);
    }
}
