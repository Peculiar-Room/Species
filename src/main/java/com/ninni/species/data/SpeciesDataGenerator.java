package com.ninni.species.data;

import com.ninni.species.Species;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesDataGenerator {

    private SpeciesDataGenerator() {
    }

    @SubscribeEvent
    public static void onGatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        boolean server = event.includeServer();
        generator.addProvider(server, (DataProvider.Factory<SpeciesDatapackBuiltinEntriesProvider>) output ->
                new SpeciesDatapackBuiltinEntriesProvider(output, event.getLookupProvider())
        );
    }

}
