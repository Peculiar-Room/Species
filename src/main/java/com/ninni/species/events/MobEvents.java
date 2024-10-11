package com.ninni.species.events;

import com.ninni.species.Species;
import com.ninni.species.entity.Birt;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.entity.Deepfish;
import com.ninni.species.entity.Goober;
import com.ninni.species.entity.Limpet;
import com.ninni.species.entity.Mammutilation;
import com.ninni.species.entity.Roombug;
import com.ninni.species.entity.Springling;
import com.ninni.species.entity.Treeper;
import com.ninni.species.entity.Trooper;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.entity.Wraptor;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.event.entity.EntityAttributeCreationEvent;
import net.minecraftforge.event.entity.SpawnPlacementRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MobEvents {

    @SubscribeEvent
    public static void registerSpawnPlacements(SpawnPlacementRegisterEvent event) {
        event.register(SpeciesEntities.WRAPTOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Wraptor::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.DEEPFISH.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Deepfish::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.ROOMBUG.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, Roombug::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.BIRT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, Birt::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.LIMPET.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Limpet::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.TREEPER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Treeper::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.TROOPER.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Trooper::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.GOOBER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, Goober::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.CRUNCHER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, Cruncher::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.MAMMUTILATION.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Mammutilation::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.SPRINGLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Springling::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(SpeciesEntities.WRAPTOR.get(), Wraptor.createWraptorAttributes().build());
        event.put(SpeciesEntities.DEEPFISH.get(), Deepfish.createDeepfishAttributes().build());
        event.put(SpeciesEntities.ROOMBUG.get(), Roombug.createRoombugAttributes().build());
        event.put(SpeciesEntities.BIRT.get(), Birt.createBirtAttributes().build());
        event.put(SpeciesEntities.LIMPET.get(), Limpet.createLimpetAttributes().build());
        event.put(SpeciesEntities.TREEPER.get(), Treeper.createAttributes().build());
        event.put(SpeciesEntities.TROOPER.get(), Trooper.createAttributes().build());
        event.put(SpeciesEntities.GOOBER.get(), Goober.createAttributes().build());
        event.put(SpeciesEntities.CRUNCHER.get(), Cruncher.createAttributes().build());
        event.put(SpeciesEntities.MAMMUTILATION.get(), Mammutilation.createAttributes().build());
        event.put(SpeciesEntities.SPRINGLING.get(), Springling.createAttributes().build());
    }

}
