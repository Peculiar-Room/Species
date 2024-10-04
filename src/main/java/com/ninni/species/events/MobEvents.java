package com.ninni.species.events;

import com.ninni.species.Species;
import com.ninni.species.entity.BirtEntity;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.entity.DeepfishEntity;
import com.ninni.species.entity.Goober;
import com.ninni.species.entity.LimpetEntity;
import com.ninni.species.entity.Mammutilation;
import com.ninni.species.entity.RoombugEntity;
import com.ninni.species.entity.Springling;
import com.ninni.species.entity.Treeper;
import com.ninni.species.entity.Trooper;
import com.ninni.species.init.SpeciesEntities;
import com.ninni.species.entity.WraptorEntity;
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
        event.register(SpeciesEntities.WRAPTOR.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, WraptorEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.DEEPFISH.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, DeepfishEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.ROOMBUG.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, RoombugEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.BIRT.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, BirtEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.LIMPET.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, LimpetEntity::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.TREEPER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Treeper::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.TROOPER.get(), SpawnPlacements.Type.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Trooper::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.GOOBER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, Goober::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.CRUNCHER.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, Cruncher::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.MAMMUTILATION.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Mammutilation::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
        event.register(SpeciesEntities.SPRINGLING.get(), SpawnPlacements.Type.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Springling::canSpawn, SpawnPlacementRegisterEvent.Operation.OR);
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(SpeciesEntities.WRAPTOR.get(), WraptorEntity.createWraptorAttributes().build());
        event.put(SpeciesEntities.DEEPFISH.get(), DeepfishEntity.createDeepfishAttributes().build());
        event.put(SpeciesEntities.ROOMBUG.get(), RoombugEntity.createRoombugAttributes().build());
        event.put(SpeciesEntities.BIRT.get(), BirtEntity.createBirtAttributes().build());
        event.put(SpeciesEntities.LIMPET.get(), LimpetEntity.createLimpetAttributes().build());
        event.put(SpeciesEntities.TREEPER.get(), Treeper.createAttributes().build());
        event.put(SpeciesEntities.TROOPER.get(), Trooper.createAttributes().build());
        event.put(SpeciesEntities.GOOBER.get(), Goober.createAttributes().build());
        event.put(SpeciesEntities.CRUNCHER.get(), Cruncher.createAttributes().build());
        event.put(SpeciesEntities.MAMMUTILATION.get(), Mammutilation.createAttributes().build());
        event.put(SpeciesEntities.SPRINGLING.get(), Springling.createAttributes().build());
    }

}
