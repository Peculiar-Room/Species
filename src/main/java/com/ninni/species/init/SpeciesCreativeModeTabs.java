package com.ninni.species.init;

import com.ninni.species.Species;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesCreativeModeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABAS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, Species.MOD_ID);

    public static final RegistryObject<CreativeModeTab> SPECIES = CREATIVE_MODE_TABAS.register("species", () -> CreativeModeTab.builder().title(Component.translatable("itemGroup.species.species")).icon(SpeciesItems.BIRT_EGG.get()::getDefaultInstance).displayItems((itemDisplayParameters, output) -> {
        SpeciesItems.ITEMS.getEntries().stream().map(RegistryObject::get).forEach(output::accept);
    }).build());

}
