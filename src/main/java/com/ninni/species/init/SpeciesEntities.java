package com.ninni.species.init;

import com.ninni.species.Species;
import com.ninni.species.entity.BirtEggEntity;
import com.ninni.species.entity.BirtEntity;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.entity.CruncherPellet;
import com.ninni.species.entity.DeepfishEntity;
import com.ninni.species.entity.Goober;
import com.ninni.species.entity.GooberGoo;
import com.ninni.species.entity.LimpetEntity;
import com.ninni.species.entity.Mammutilation;
import com.ninni.species.entity.MammutilationIchor;
import com.ninni.species.entity.RoombugEntity;
import com.ninni.species.entity.Springling;
import com.ninni.species.entity.Treeper;
import com.ninni.species.entity.Trooper;
import com.ninni.species.entity.WraptorEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesEntities {

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, Species.MOD_ID);

    public static final RegistryObject<EntityType<WraptorEntity>> WRAPTOR = ENTITY_TYPES.register(
            "wraptor", () ->
            EntityType.Builder.of(WraptorEntity::new, MobCategory.MONSTER)
                    .sized(1.2F, 2F)
                    .clientTrackingRange(8)
                    .build(new ResourceLocation(Species.MOD_ID, "wraptor").toString())
    );

    public static final RegistryObject<EntityType<DeepfishEntity>> DEEPFISH = ENTITY_TYPES.register(
            "deepfish", () ->
            EntityType.Builder.of(DeepfishEntity::new, MobCategory.UNDERGROUND_WATER_CREATURE)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(Species.MOD_ID, "deepfish").toString())
    );

    public static final RegistryObject<EntityType<RoombugEntity>> ROOMBUG = ENTITY_TYPES.register(
            "roombug", () ->
            EntityType.Builder.of(RoombugEntity::new, MobCategory.CREATURE)
                    .sized(1.375F, 0.375F)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(Species.MOD_ID, "roombug").toString())
    );

    public static final RegistryObject<EntityType<BirtEntity>> BIRT = ENTITY_TYPES.register(
            "birt", () ->
            EntityType.Builder.of(BirtEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.6F)
                    .clientTrackingRange(10)
                    .build(new ResourceLocation(Species.MOD_ID, "birt").toString())
    );

    public static final RegistryObject<EntityType<BirtEggEntity>> BIRT_EGG = ENTITY_TYPES.register(
            "birt_egg", () ->
            EntityType.Builder.<BirtEggEntity>of(BirtEggEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4)
                    .build(new ResourceLocation(Species.MOD_ID, "birt_egg").toString())
    );

    public static final RegistryObject<EntityType<LimpetEntity>> LIMPET = ENTITY_TYPES.register(
            "limpet", () ->
            EntityType.Builder.of(LimpetEntity::new, MobCategory.CREATURE)
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

    public static final RegistryObject<EntityType<MammutilationIchor>> MAMMUTILATION_ICHOR = ENTITY_TYPES.register(
            "mammutilation_ichor", () ->
            EntityType.Builder.<MammutilationIchor>of(MammutilationIchor::new, MobCategory.MISC)
                    .sized(0.25F, 0.25F)
                    .clientTrackingRange(4)
                    .build(new ResourceLocation(Species.MOD_ID, "mammutilation_ichor").toString())
    );

}
