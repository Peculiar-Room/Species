package com.ninni.species.entity;

import com.ninni.species.Species;
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

    public static final RegistryObject<EntityType<WraptorEntity>> WRAPTOR = ENTITY_TYPES.register(
            "wraptor", () ->
            EntityType.Builder.of(WraptorEntity::new, MobCategory.MONSTER)
                    .sized(1.2F, 2F)
                    .clientTrackingRange(8).build(new ResourceLocation(Species.MOD_ID, "wraptor").toString())
    );

    public static final RegistryObject<EntityType<DeepfishEntity>> DEEPFISH = ENTITY_TYPES.register(
            "deepfish", () ->
            EntityType.Builder.of(DeepfishEntity::new, MobCategory.UNDERGROUND_WATER_CREATURE)
                    .sized(0.5F, 0.5F)
                    .clientTrackingRange(10).build(new ResourceLocation(Species.MOD_ID, "deepfish").toString())
    );

    public static final RegistryObject<EntityType<RoombugEntity>> ROOMBUG = ENTITY_TYPES.register(
            "roombug", () ->
            EntityType.Builder.of(RoombugEntity::new, MobCategory.CREATURE)
                    .sized(1.375F, 0.375F)
                    .clientTrackingRange(10).build(new ResourceLocation(Species.MOD_ID, "roombug").toString())
    );

    public static final RegistryObject<EntityType<BirtEntity>> BIRT = ENTITY_TYPES.register(
            "birt", () ->
            EntityType.Builder.of(BirtEntity::new, MobCategory.CREATURE)
                    .sized(0.6F, 0.6F)
                    .clientTrackingRange(10).build(new ResourceLocation(Species.MOD_ID, "birt").toString())
    );

    public static final RegistryObject<EntityType<BirtEggEntity>> BIRT_EGG = ENTITY_TYPES.register(
            "birt_egg", () ->
            EntityType.Builder.<BirtEggEntity>of(BirtEggEntity::new, MobCategory.MISC)
                    .sized(0.25f, 0.25f)
                    .clientTrackingRange(4).build(new ResourceLocation(Species.MOD_ID, "birt_egg").toString())
    );

    public static final RegistryObject<EntityType<LimpetEntity>> LIMPET = ENTITY_TYPES.register(
            "limpet", () ->
            EntityType.Builder.of(LimpetEntity::new, MobCategory.CREATURE)
                    .sized(0.75F, 1.25F)
                    .clientTrackingRange(10).build(new ResourceLocation(Species.MOD_ID, "limpet").toString())
    );

}
