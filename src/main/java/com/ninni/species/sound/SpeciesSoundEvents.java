package com.ninni.species.sound;

import com.ninni.species.Species;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class SpeciesSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Species.MOD_ID);

    public static final RegistryObject<SoundEvent> ENTITY_WRAPTOR_DEATH = register("entity.wraptor.death");
    public static final RegistryObject<SoundEvent> ENTITY_WRAPTOR_HURT = register("entity.wraptor.hurt");
    public static final RegistryObject<SoundEvent> ENTITY_WRAPTOR_IDLE = register("entity.wraptor.idle");
    public static final RegistryObject<SoundEvent> ENTITY_WRAPTOR_AGGRO = register("entity.wraptor.aggro");
    public static final RegistryObject<SoundEvent> ENTITY_WRAPTOR_AGITATED = register("entity.wraptor.agitated");
    public static final RegistryObject<SoundEvent> ENTITY_WRAPTOR_ATTACK = register("entity.wraptor.attack");
    public static final RegistryObject<SoundEvent> ENTITY_WRAPTOR_SHEAR = register("entity.wraptor.shear");
    public static final RegistryObject<SoundEvent> ENTITY_WRAPTOR_STEP = register("entity.wraptor.step");
    public static final RegistryObject<SoundEvent> ENTITY_WRAPTOR_FEATHER_LOSS = register("entity.wraptor.feather_loss");
    public static final RegistryObject<SoundEvent> ENTITY_WRAPTOR_EGG = register("entity.wraptor.egg");

    public static final RegistryObject<SoundEvent> BLOCK_WRAPTOR_EGG_BREAK = register("block.wraptor_egg.break");
    public static final RegistryObject<SoundEvent> BLOCK_WRAPTOR_EGG_CRACK = register("block.wraptor_egg.crack");
    public static final RegistryObject<SoundEvent> BLOCK_WRAPTOR_EGG_HATCH = register("block.wraptor_egg.hatch");

    public static final RegistryObject<SoundEvent> ITEM_CRACKED_WRAPTOR_EGG_SLURP = register("item.cracked_wraptor_egg.slurp");

    public static final RegistryObject<SoundEvent> ENTITY_DEEPFISH_DEATH = register("entity.deepfish.death");
    public static final RegistryObject<SoundEvent> ENTITY_DEEPFISH_HURT = register("entity.deepfish.hurt");
    public static final RegistryObject<SoundEvent> ENTITY_DEEPFISH_IDLE = register("entity.deepfish.idle");
    public static final RegistryObject<SoundEvent> ENTITY_DEEPFISH_FLOP = register("entity.deepfish.flop");

    public static final RegistryObject<SoundEvent> ENTITY_ROOMBUG_DEATH = register("entity.roombug.death");
    public static final RegistryObject<SoundEvent> ENTITY_ROOMBUG_HURT = register("entity.roombug.hurt");
    public static final RegistryObject<SoundEvent> ENTITY_ROOMBUG_IDLE = register("entity.roombug.idle");
    public static final RegistryObject<SoundEvent> ENTITY_ROOMBUG_SNORING = register("entity.roombug.snoring");
    public static final RegistryObject<SoundEvent> ENTITY_ROOMBUG_EAT = register("entity.roombug.eat");
    public static final RegistryObject<SoundEvent> ENTITY_ROOMBUG_STEP = register("entity.roombug.step");
    public static final RegistryObject<SoundEvent> ENTITY_ROOMBUG_GOOFY_AAH_STEP = register("entity.roombug.goofy_aah_step");

    public static final RegistryObject<SoundEvent> ENTITY_BIRT_DEATH = register("entity.birt.death");
    public static final RegistryObject<SoundEvent> ENTITY_BIRT_HURT = register("entity.birt.hurt");
    public static final RegistryObject<SoundEvent> ENTITY_BIRT_IDLE = register("entity.birt.idle");
    public static final RegistryObject<SoundEvent> ENTITY_BIRT_FLY = register("entity.birt.fly");
    public static final RegistryObject<SoundEvent> ENTITY_BIRT_MESSAGE = register("entity.birt.message");

    public static final RegistryObject<SoundEvent> BLOCK_BIRT_DWELLING_COLLECT = register("block.birt_dwelling.collect");
    public static final RegistryObject<SoundEvent> BLOCK_BIRT_DWELLING_ENTER = register("block.birt_dwelling.enter");
    public static final RegistryObject<SoundEvent> BLOCK_BIRT_DWELLING_EXIT = register("block.birt_dwelling.exit");
    public static final RegistryObject<SoundEvent> BLOCK_BIRT_DWELLING_WORK = register("block.birt_dwelling.work");

    public static final RegistryObject<SoundEvent> ENTITY_BIRTD = register("entity.birtd");

    public static final RegistryObject<SoundEvent> ITEM_BIRT_EGG_THROW = register("item.birt_egg.throw");
    public static final RegistryObject<SoundEvent> ITEM_BIRT_EGG_HIT = register("item.birt_egg.hit");

    public static final RegistryObject<SoundEvent> MUSIC_DISC_DIAL = register("music.disc.dial");

    public static final RegistryObject<SoundEvent> ENTITY_LIMPET_DEATH = register("entity.limpet.death");
    public static final RegistryObject<SoundEvent> ENTITY_LIMPET_HURT = register("entity.limpet.hurt");
    public static final RegistryObject<SoundEvent> ENTITY_LIMPET_IDLE = register("entity.limpet.idle");
    public static final RegistryObject<SoundEvent> ENTITY_LIMPET_STEP = register("entity.limpet.step");
    public static final RegistryObject<SoundEvent> ENTITY_LIMPET_DEFLECT = register("entity.limpet.deflect");

    private static RegistryObject<SoundEvent> register(String name) {
        ResourceLocation id = new ResourceLocation(Species.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
