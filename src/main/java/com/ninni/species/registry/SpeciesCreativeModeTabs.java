package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpeciesCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Species.MOD_ID);

    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> SPECIES = CREATIVE_MODE_TABS.register("species", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.species.species")).icon(SpeciesItems.LOGO.get()::getDefaultInstance)
            .displayItems((itemDisplayParameters, output) -> {
                SpeciesItems.ITEMS.getEntries().forEach(itemRegistryObject ->  {
                    if (itemRegistryObject.get().getDefaultInstance().is(SpeciesItems.LOGO.get())) return;
                    if (itemRegistryObject.get().getDefaultInstance().is(SpeciesItems.TAB.get())) return;
                    if (itemRegistryObject.get().getDefaultInstance().is(SpeciesItems.V1.get())) return;
                    if (itemRegistryObject.get().getDefaultInstance().is(SpeciesItems.V2.get())) return;
                    if (itemRegistryObject.get().getDefaultInstance().is(SpeciesItems.V3.get())) return;
                    output.accept(itemRegistryObject.get());
                });
            })
            .build());

}
