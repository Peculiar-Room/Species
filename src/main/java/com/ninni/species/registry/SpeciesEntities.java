package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.entity.mob.update_1.*;
import com.ninni.species.server.entity.mob.update_2.*;
import com.ninni.species.server.entity.mob.update_3.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Species.MOD_ID);

    public static final RegistryObject<EntityType<Wraptor>> WRAPTOR = ENTITY_TYPES.register(
            "wraptor", () ->
            EntityType.Builder.of(Wraptor::new, MobCategory.MONSTER)
                    .sized(0.8F, 2.2F)
                    .clientTrackingRange(8)
                    .build(new ResourceLocation(Species.MOD_ID, "wraptor").toString())
    );

    public static final RegistryObject<EntityType<Deepfish>> DEEPFISH = ENTITY_TYPES.register(
            "deepfish", () ->
            EntityType.Builder.of(Deepfish::new, MobCategory.UNDERGROUND_WATER_CREATURE)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(Species.MOD_ID, "deepfish").toString())
    );

    public static final RegistryObject<EntityType<Stackatick>> STACKATICK = ENTITY_TYPES.register(
            "stackatick", () ->
            EntityType.Builder.of(Stackatick::new, MobCategory.CREATURE)
                    .sized(1F, 1.05F)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(Species.MOD_ID, "stackatick").toString())
    );

    public static final RegistryObject<EntityType<Birt>> BIRT = ENTITY_TYPES.register(
            "birt", () ->
            EntityType.Builder.of(Birt::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.6F)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(Species.MOD_ID, "birt").toString())
    );

    public static final RegistryObject<EntityType<BirtEgg>> BIRT_EGG = ENTITY_TYPES.register(
            "birt_egg", () ->
            EntityType.Builder.<BirtEgg>of(BirtEgg::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .build(new ResourceLocation(Species.MOD_ID, "birt_egg").toString())
    );

    public static final RegistryObject<EntityType<Limpet>> LIMPET = ENTITY_TYPES.register(
            "limpet", () ->
            EntityType.Builder.of(Limpet::new, MobCategory.MONSTER)
                    .sized(0.75F, 1.25F)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(Species.MOD_ID, "limpet").toString())
    );

    public static final RegistryObject<EntityType<Treeper>> TREEPER = ENTITY_TYPES.register(
            "treeper", () ->
            EntityType.Builder.of(Treeper::new, MobCategory.CREATURE)
                    .sized(1.9F, 7.0F)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(Species.MOD_ID, "treeper").toString())
    );

    public static final RegistryObject<EntityType<Trooper>> TROOPER = ENTITY_TYPES.register(
            "trooper", () ->
            EntityType.Builder.of(Trooper::new, MobCategory.CREATURE)
                    .sized(0.7F, 1.2F)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(Species.MOD_ID, "trooper").toString())
    );

    public static final RegistryObject<EntityType<Goober>> GOOBER = ENTITY_TYPES.register(
            "goober", () ->
            EntityType.Builder.of(Goober::new, MobCategory.CREATURE)
                    .sized(1.5F, 2.2F)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(Species.MOD_ID, "goober").toString())
    );

    public static final RegistryObject<EntityType<Cruncher>> CRUNCHER = ENTITY_TYPES.register(
            "cruncher", () ->
            EntityType.Builder.of(Cruncher::new, MobCategory.CREATURE)
                    .sized(2.6F, 4.2F)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(Species.MOD_ID, "cruncher").toString())
    );

    public static final RegistryObject<EntityType<Mammutilation>> MAMMUTILATION = ENTITY_TYPES.register(
            "mammutilation", () ->
            EntityType.Builder.of(Mammutilation::new, MobCategory.CREATURE)
                    .sized(2.6F, 3.8F)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(Species.MOD_ID, "mammutilation").toString())
    );

    public static final RegistryObject<EntityType<Springling>> SPRINGLING = ENTITY_TYPES.register(
            "springling", () ->
            EntityType.Builder.of(Springling::new, MobCategory.CREATURE)
                    .sized(0.8F, 1.3F)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(Species.MOD_ID, "springling").toString())
    );

    public static final RegistryObject<EntityType<CruncherPellet>> CRUNCHER_PELLET = ENTITY_TYPES.register(
            "cruncher_pellet", () ->
            EntityType.Builder.<CruncherPellet>of(CruncherPellet::new, MobCategory.MISC)
                    .sized(0.98F, 0.98F)
                    .clientTrackingRange(10)
                    .updateInterval(20)
                    .build(new ResourceLocation(Species.MOD_ID, "cruncher_pellet").toString())
    );

    public static final RegistryObject<EntityType<GooberGoo>> GOOBER_GOO = ENTITY_TYPES.register(
            "goober_goo", () ->
            EntityType.Builder.<GooberGoo>of(GooberGoo::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .build(new ResourceLocation(Species.MOD_ID, "goober_goo").toString())
    );

    public static final RegistryObject<EntityType<Ghoul>> GHOUL = ENTITY_TYPES.register(
            "ghoul", () ->
                    EntityType.Builder.of(Ghoul::new, MobCategory.MONSTER)
                            .sized(0.8F, 1.5F)
                            .clientTrackingRange(30)
                            .build(new ResourceLocation(Species.MOD_ID, "ghoul").toString())
    );

    public static final RegistryObject<EntityType<Quake>> QUAKE = ENTITY_TYPES.register(
            "quake", () ->
                    EntityType.Builder.of(Quake::new, MobCategory.MONSTER)
                            .sized(1.8F, 2.5F)
                            .clientTrackingRange(10)
                            .build(new ResourceLocation(Species.MOD_ID, "quake").toString())
    );

    public static final RegistryObject<EntityType<DeflectorDummy>> DEFLECTOR_DUMMY = ENTITY_TYPES.register(
            "deflector_dummy", () ->
                    EntityType.Builder.of(DeflectorDummy::new, MobCategory.MISC)
                            .sized(0.8F, 1.9F)
                            .clientTrackingRange(10)
                            .build(new ResourceLocation(Species.MOD_ID, "deflector_dummy").toString())
    );

    public static final RegistryObject<EntityType<Bewereager>> BEWEREAGER = ENTITY_TYPES.register(
            "bewereager", () ->
                    EntityType.Builder.of(Bewereager::new, MobCategory.MONSTER)
                            .sized(0.8F, 1.8F)
                            .clientTrackingRange(10)
                            .build(new ResourceLocation(Species.MOD_ID, "bewereager").toString())
    );

    public static final RegistryObject<EntityType<Spectre>> SPECTRE = ENTITY_TYPES.register(
            "spectre", () ->
                    EntityType.Builder.of(Spectre::new, MobCategory.MONSTER)
                            .sized(0.6F, 1.5F)
                            .clientTrackingRange(10)
                            .build(new ResourceLocation(Species.MOD_ID, "spectre").toString())
    );

    public static final RegistryObject<EntityType<Wicked>> WICKED = ENTITY_TYPES.register(
            "wicked", () ->
                    EntityType.Builder.of(Wicked::new, MobCategory.MONSTER)
                            .sized(0.7F, 1.5F)
                            .clientTrackingRange(10)
                            .build(new ResourceLocation(Species.MOD_ID, "wicked").toString())
    );

    public static final RegistryObject<EntityType<WickedFireball>> WICKED_FIREBALL = ENTITY_TYPES.register(
            "wicked_fireball", () ->
                    EntityType.Builder.<WickedFireball>of(WickedFireball::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(4)
                            .build(new ResourceLocation(Species.MOD_ID, "wicked_fireball").toString())
    );

    public static final RegistryObject<EntityType<WickedSwapperProjectile>> WICKED_SWAPPER = ENTITY_TYPES.register(
            "wicked_swapper", () ->
                    EntityType.Builder.<WickedSwapperProjectile>of(WickedSwapperProjectile::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(4)
                            .build(new ResourceLocation(Species.MOD_ID, "wicked_swapper").toString())
    );

    public static final RegistryObject<EntityType<LeafHanger>> LEAF_HANGER = ENTITY_TYPES.register(
            "leaf_hanger", () ->
                    EntityType.Builder.of(LeafHanger::new, MobCategory.WATER_CREATURE)
                            .sized(0.7F, 1.5F)
                            .clientTrackingRange(10)
                            .build(new ResourceLocation(Species.MOD_ID, "leaf_hanger").toString())
    );

    public static final RegistryObject<EntityType<CliffHanger>> CLIFF_HANGER = ENTITY_TYPES.register(
            "cliff_hanger", () ->
                    EntityType.Builder.of(CliffHanger::new, MobCategory.MONSTER)
                            .sized(0.7F, 1.5F)
                            .clientTrackingRange(10)
                            .build(new ResourceLocation(Species.MOD_ID, "cliff_hanger").toString())
    );

    public static final RegistryObject<EntityType<Coil>> COIL = ENTITY_TYPES.register(
            "coil", () ->
                    EntityType.Builder.<Coil>of(Coil::new, MobCategory.MISC)
                            .sized(0.25F, 0.25F)
                            .clientTrackingRange(256)
                            .updateInterval(1)
                            .build(new ResourceLocation(Species.MOD_ID, "coil").toString())
    );

    public static final RegistryObject<EntityType<Harpoon>> HARPOON = ENTITY_TYPES.register(
            "harpoon", () ->
                    EntityType.Builder.<Harpoon>of(Harpoon::new, MobCategory.MISC)
                            .sized(0.25f, 0.25f)
                            .clientTrackingRange(64)
                            .updateInterval(1)
                            .setShouldReceiveVelocityUpdates(true)
                            .build(new ResourceLocation(Species.MOD_ID, "harpoon").toString())
    );
}
