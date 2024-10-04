package com.ninni.species.init;

import com.ninni.species.Species;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.minecraftforge.common.util.ForgeSoundType;
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

    public static final RegistryObject<SoundEvent> WRAPTOR_EGG_BREAK = register("block.wraptor_egg.break");
    public static final RegistryObject<SoundEvent> WRAPTOR_EGG_CRACK = register("block.wraptor_egg.crack");
    public static final RegistryObject<SoundEvent> WRAPTOR_EGG_HATCH = register("block.wraptor_egg.hatch");

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

    public static final RegistryObject<SoundEvent> MUSIC_DISC_LAPIDARIAN = register("music.disc.lapidarian");

    public static final RegistryObject<SoundEvent> GOOBER_DEATH = register("entity.goober.death");
    public static final RegistryObject<SoundEvent> GOOBER_HURT = register("entity.goober.hurt");
    public static final RegistryObject<SoundEvent> GOOBER_IDLE = register("entity.goober.idle");
    public static final RegistryObject<SoundEvent> GOOBER_IDLE_RESTING = register("entity.goober.idle_resting");
    public static final RegistryObject<SoundEvent> GOOBER_SNEEZE = register("entity.goober.sneeze");
    public static final RegistryObject<SoundEvent> GOOBER_LAY_DOWN = register("entity.goober.lay_down");
    public static final RegistryObject<SoundEvent> GOOBER_REAR_UP = register("entity.goober.rear_up");
    public static final RegistryObject<SoundEvent> GOOBER_STEP = register("entity.goober.step");
    public static final RegistryObject<SoundEvent> GOOBER_YAWN = register("entity.goober.yawn");
    public static final RegistryObject<SoundEvent> GOOBER_EAT = register("entity.goober.eat");

    public static final RegistryObject<SoundEvent> TREEPER_DEATH = register("entity.treeper.death");
    public static final RegistryObject<SoundEvent> TREEPER_HURT = register("entity.treeper.hurt");
    public static final RegistryObject<SoundEvent> TREEPER_IDLE = register("entity.treeper.idle");
    public static final RegistryObject<SoundEvent> TREEPER_IDLE_PLANTED = register("entity.treeper.idle_planted");
    public static final RegistryObject<SoundEvent> TREEPER_PLANT = register("entity.treeper.plant");
    public static final RegistryObject<SoundEvent> TREEPER_UPROOT = register("entity.treeper.uproot");
    public static final RegistryObject<SoundEvent> TREEPER_STEP = register("entity.treeper.step");
    public static final RegistryObject<SoundEvent> TREEPER_SHAKE_FAIL = register("entity.treeper.shake_fail");
    public static final RegistryObject<SoundEvent> TREEPER_SHAKE_SUCCESS = register("entity.treeper.shake_success");
    public static final RegistryObject<SoundEvent> TREEPER_BURN = register("entity.treeper.burn");

    public static final RegistryObject<SoundEvent> TROOPER_DEATH = register("entity.trooper.death");
    public static final RegistryObject<SoundEvent> TROOPER_HURT = register("entity.trooper.hurt");
    public static final RegistryObject<SoundEvent> TROOPER_STEP = register("entity.trooper.step");
    public static final RegistryObject<SoundEvent> TROOPER_LEAVES = register("entity.trooper.leaves");
    public static final RegistryObject<SoundEvent> TROOPER_UPROOT = register("entity.trooper.uproot");
    public static final RegistryObject<SoundEvent> TROOPER_PLANT = register("entity.trooper.plant");

    public static final RegistryObject<SoundEvent> PETRIFIED_EGG_CRACK = register("block.petrified_egg.crack");
    public static final RegistryObject<SoundEvent> PETRIFIED_EGG_HATCH = register("block.petrified_egg.hatch");
    public static final RegistryObject<SoundEvent> PETRIFIED_EGG_PLOP = register("block.petrified_egg.plop");

    public static final RegistryObject<SoundEvent> CRUNCHER_DEATH = register("entity.cruncher.death");
    public static final RegistryObject<SoundEvent> CRUNCHER_HURT = register("entity.cruncher.hurt");
    public static final RegistryObject<SoundEvent> CRUNCHER_IDLE = register("entity.cruncher.idle");
    public static final RegistryObject<SoundEvent> CRUNCHER_STEP = register("entity.cruncher.step");
    public static final RegistryObject<SoundEvent> CRUNCHER_ROAR = register("entity.cruncher.roar");
    public static final RegistryObject<SoundEvent> CRUNCHER_STUN = register("entity.cruncher.stun");
    public static final RegistryObject<SoundEvent> CRUNCHER_STOMP = register("entity.cruncher.stomp");
    public static final RegistryObject<SoundEvent> CRUNCHER_SPIT = register("entity.cruncher.spit");

    public static final RegistryObject<SoundEvent> GUT_FEELING_SPAWN = register("effect.gut_feeling.spawn");
    public static final RegistryObject<SoundEvent> GUT_FEELING_APPLIED = register("effect.gut_feeling.applied");
    public static final RegistryObject<SoundEvent> GUT_FEELING_ROAR = register("effect.gut_feeling.roar");

    public static final RegistryObject<SoundEvent> SPRINGLING_DEATH = register("entity.springling.death");
    public static final RegistryObject<SoundEvent> SPRINGLING_HURT = register("entity.springling.hurt");
    public static final RegistryObject<SoundEvent> SPRINGLING_IDLE = register("entity.springling.idle");
    public static final RegistryObject<SoundEvent> SPRINGLING_STEP = register("entity.springling.step");
    public static final RegistryObject<SoundEvent> SPRINGLING_EAT = register("entity.springling.eat");
    public static final RegistryObject<SoundEvent> SPRINGLING_EXTEND = register("entity.springling.extend");
    public static final RegistryObject<SoundEvent> SPRINGLING_EXTEND_FINISH = register("entity.springling.extend_finish");
    public static final RegistryObject<SoundEvent> SPRINGLING_EGG_CRACK = register("block.springling_egg.crack");
    public static final RegistryObject<SoundEvent> SPRINGLING_EGG_HATCH = register("block.springling_egg.hatch");
    public static final RegistryObject<SoundEvent> SPRINGLING_EGG_PLOP = register("block.springling_egg.plop");

    public static final RegistryObject<SoundEvent> MAMMUTILATION_DEATH = register("entity.mammutilation.death");
    public static final RegistryObject<SoundEvent> MAMMUTILATION_HURT = register("entity.mammutilation.hurt");
    public static final RegistryObject<SoundEvent> MAMMUTILATION_IDLE = register("entity.mammutilation.idle");
    public static final RegistryObject<SoundEvent> MAMMUTILATION_HOWL = register("entity.mammutilation.howl");
    public static final RegistryObject<SoundEvent> MAMMUTILATION_COUGH = register("entity.mammutilation.cough");
    public static final RegistryObject<SoundEvent> MAMMUTIFUL_IDLE = register("entity.mammutilation.mammutiful_idle");
    public static final RegistryObject<SoundEvent> MAMMUTIFUL_HOWL = register("entity.mammutilation.mammutiful_howl");
    public static final RegistryObject<SoundEvent> ICHOR_BOTTLE = register("item.ichor_bottle.applied");
    public static final RegistryObject<SoundEvent> YOUTH_POTION_BABY = register("item.youth_potion.baby");
    public static final RegistryObject<SoundEvent> YOUTH_POTION_STUMPED = register("item.youth_potion.stumped");

    public static final SoundType WRAPTOR_EGG = new ForgeSoundType(1.0F, 1.5F, SpeciesSoundEvents.WRAPTOR_EGG_BREAK, () -> SoundEvents.SHROOMLIGHT_STEP, () -> SoundEvents.SHROOMLIGHT_PLACE, () -> SoundEvents.SHROOMLIGHT_HIT, () -> SoundEvents.SHROOMLIGHT_FALL);
    public static final SoundType FROZEN_HAIR = register("frozen_hair", 0.8F, 1);
    public static final SoundType FROZEN_MEAT = register("frozen_meat", 0.8F, 1);
    public static final SoundType ALPHACENE_GRASS = register("alphacene_grass", 0.8F, 1.6F);
    public static final SoundType ALPHACENE_MOSS = register("alphacene_moss", 0.8F, 1.6F);
    public static final SoundType ALPHACENE_FOLIAGE = register("alphacene_foliage", 0.8F, 1.6F);

    private static SoundType register(String name, float volume, float pitch) {
        return new ForgeSoundType(volume, pitch, register("block." + name + ".break"), register("block." + name + ".step"), register("block." + name + ".place"), register("block." + name + ".hit"), register("block." + name + ".fall"));
    }

    private static RegistryObject<SoundEvent> register(String name) {
        ResourceLocation id = new ResourceLocation(Species.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
