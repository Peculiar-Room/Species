package com.ninni.species.registry;

import com.ninni.species.Species;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.util.DeferredSoundType;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpeciesSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS = DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, Species.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> WRAPTOR_DEATH = register("entity.wraptor.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> WRAPTOR_HURT = register("entity.wraptor.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> WRAPTOR_IDLE = register("entity.wraptor.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> WRAPTOR_AGGRO = register("entity.wraptor.aggro");
    public static final DeferredHolder<SoundEvent, SoundEvent> WRAPTOR_AGITATED = register("entity.wraptor.agitated");
    public static final DeferredHolder<SoundEvent, SoundEvent> WRAPTOR_ATTACK = register("entity.wraptor.attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> WRAPTOR_SHEAR = register("entity.wraptor.shear");
    public static final DeferredHolder<SoundEvent, SoundEvent> WRAPTOR_STEP = register("entity.wraptor.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> WRAPTOR_FEATHER_LOSS = register("entity.wraptor.feather_loss");
    public static final DeferredHolder<SoundEvent, SoundEvent> WRAPTOR_EGG = register("entity.wraptor.egg");

    public static final DeferredHolder<SoundEvent, SoundEvent> WRAPTOR_EGG_BREAK = register("block.wraptor_egg.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> WRAPTOR_EGG_CRACK = register("block.wraptor_egg.crack");
    public static final DeferredHolder<SoundEvent, SoundEvent> WRAPTOR_EGG_HATCH = register("block.wraptor_egg.hatch");

    public static final DeferredHolder<SoundEvent, SoundEvent> CRACKED_WRAPTOR_EGG_SLURP = register("item.cracked_wraptor_egg.slurp");

    public static final DeferredHolder<SoundEvent, SoundEvent> DEEPFISH_DEATH = register("entity.deepfish.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEEPFISH_HURT = register("entity.deepfish.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEEPFISH_IDLE = register("entity.deepfish.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEEPFISH_FLOP = register("entity.deepfish.flop");

    public static final DeferredHolder<SoundEvent, SoundEvent> STACKATICK_DEATH = register("entity.stackatick.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> STACKATICK_HURT = register("entity.stackatick.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> STACKATICK_IDLE = register("entity.stackatick.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> STACKATICK_SNORING = register("entity.stackatick.snoring");
    public static final DeferredHolder<SoundEvent, SoundEvent> STACKATICK_EAT = register("entity.stackatick.eat");
    public static final DeferredHolder<SoundEvent, SoundEvent> STACKATICK_STEP = register("entity.stackatick.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> STACKATICK_SIT = register("entity.stackatick.sit");
    public static final DeferredHolder<SoundEvent, SoundEvent> STACKATICK_STAND_UP = register("entity.stackatick.stand_up");
    public static final DeferredHolder<SoundEvent, SoundEvent> STACKATICK_GOOFY_AAH_STEP = register("entity.stackatick.goofy_aah_step");

    public static final DeferredHolder<SoundEvent, SoundEvent> BIRT_DEATH = register("entity.birt.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> BIRT_HURT = register("entity.birt.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> BIRT_IDLE = register("entity.birt.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> BIRT_FLY = register("entity.birt.fly");
    public static final DeferredHolder<SoundEvent, SoundEvent> BIRT_SEARCH = register("entity.birt.search");
    public static final DeferredHolder<SoundEvent, SoundEvent> BIRT_MESSAGE = register("entity.birt.message");

    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_BIRT_DWELLING_COLLECT = register("block.birt_dwelling.collect");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_BIRT_DWELLING_ENTER = register("block.birt_dwelling.enter");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_BIRT_DWELLING_EXIT = register("block.birt_dwelling.exit");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_BIRT_DWELLING_WORK = register("block.birt_dwelling.work");

    public static final DeferredHolder<SoundEvent, SoundEvent> BLOCK_BIRTDAY_CAKE_BLOW = register("block.birtday_cake.blow");

    public static final DeferredHolder<SoundEvent, SoundEvent> ENTITY_BIRTD = register("effect.birtd.applied");

    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_BIRT_EGG_THROW = register("item.birt_egg.throw");
    public static final DeferredHolder<SoundEvent, SoundEvent> ITEM_BIRT_EGG_HIT = register("item.birt_egg.hit");

    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_DIAL = register("music.disc.dial");
    public static final DeferredHolder<SoundEvent, SoundEvent> HAPPY_BIRTDAY = register("music.happy_birtday");

    public static final DeferredHolder<SoundEvent, SoundEvent> LIMPET_DEATH = register("entity.limpet.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> LIMPET_HURT = register("entity.limpet.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> LIMPET_IDLE = register("entity.limpet.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> LIMPET_BREAK = register("entity.limpet.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> LIMPET_STEP = register("entity.limpet.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> LIMPET_DEFLECT = register("entity.limpet.deflect");

    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISC_LAPIDARIAN = register("music.disc.lapidarian");

    public static final DeferredHolder<SoundEvent, SoundEvent> GOOBER_DEATH = register("entity.goober.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOBER_HURT = register("entity.goober.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOBER_IDLE = register("entity.goober.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOBER_IDLE_RESTING = register("entity.goober.idle_resting");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOBER_SNEEZE = register("entity.goober.sneeze");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOBER_LAY_DOWN = register("entity.goober.lay_down");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOBER_REAR_UP = register("entity.goober.rear_up");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOBER_STEP = register("entity.goober.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOBER_YAWN = register("entity.goober.yawn");
    public static final DeferredHolder<SoundEvent, SoundEvent> GOOBER_EAT = register("entity.goober.eat");

    public static final DeferredHolder<SoundEvent, SoundEvent> TREEPER_DEATH = register("entity.treeper.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> TREEPER_HURT = register("entity.treeper.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> TREEPER_IDLE = register("entity.treeper.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> TREEPER_IDLE_PLANTED = register("entity.treeper.idle_planted");
    public static final DeferredHolder<SoundEvent, SoundEvent> TREEPER_PLANT = register("entity.treeper.plant");
    public static final DeferredHolder<SoundEvent, SoundEvent> TREEPER_UPROOT = register("entity.treeper.uproot");
    public static final DeferredHolder<SoundEvent, SoundEvent> TREEPER_STEP = register("entity.treeper.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> TREEPER_SHAKE_FAIL = register("entity.treeper.shake_fail");
    public static final DeferredHolder<SoundEvent, SoundEvent> TREEPER_SHAKE_SUCCESS = register("entity.treeper.shake_success");
    public static final DeferredHolder<SoundEvent, SoundEvent> TREEPER_BURN = register("entity.treeper.burn");

    public static final DeferredHolder<SoundEvent, SoundEvent> TROOPER_DEATH = register("entity.trooper.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> TROOPER_HURT = register("entity.trooper.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> TROOPER_STEP = register("entity.trooper.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> TROOPER_LEAVES = register("entity.trooper.leaves");
    public static final DeferredHolder<SoundEvent, SoundEvent> TROOPER_UPROOT = register("entity.trooper.uproot");
    public static final DeferredHolder<SoundEvent, SoundEvent> TROOPER_PLANT = register("entity.trooper.plant");

    public static final DeferredHolder<SoundEvent, SoundEvent> PETRIFIED_EGG_CRACK = register("block.petrified_egg.crack");
    public static final DeferredHolder<SoundEvent, SoundEvent> PETRIFIED_EGG_HATCH = register("block.petrified_egg.hatch");
    public static final DeferredHolder<SoundEvent, SoundEvent> PETRIFIED_EGG_PLOP = register("block.petrified_egg.plop");

    public static final DeferredHolder<SoundEvent, SoundEvent> CRUNCHER_DEATH = register("entity.cruncher.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRUNCHER_HURT = register("entity.cruncher.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRUNCHER_IDLE = register("entity.cruncher.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRUNCHER_STEP = register("entity.cruncher.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRUNCHER_ROAR = register("entity.cruncher.roar");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRUNCHER_STUN = register("entity.cruncher.stun");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRUNCHER_STOMP = register("entity.cruncher.stomp");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRUNCHER_SPIT = register("entity.cruncher.spit");

    public static final DeferredHolder<SoundEvent, SoundEvent> GUT_FEELING_SPAWN = register("effect.gut_feeling.spawn");
    public static final DeferredHolder<SoundEvent, SoundEvent> GUT_FEELING_APPLIED = register("effect.gut_feeling.applied");
    public static final DeferredHolder<SoundEvent, SoundEvent> GUT_FEELING_ROAR = register("effect.gut_feeling.roar");

    public static final DeferredHolder<SoundEvent, SoundEvent> SPRINGLING_DEATH = register("entity.springling.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPRINGLING_HURT = register("entity.springling.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPRINGLING_IDLE = register("entity.springling.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPRINGLING_STEP = register("entity.springling.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPRINGLING_EAT = register("entity.springling.eat");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPRINGLING_EXTEND = register("entity.springling.extend");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPRINGLING_EXTEND_FINISH = register("entity.springling.extend_finish");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPRINGLING_EGG_CRACK = register("block.springling_egg.crack");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPRINGLING_EGG_HATCH = register("block.springling_egg.hatch");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPRINGLING_EGG_PLOP = register("block.springling_egg.plop");

    public static final DeferredHolder<SoundEvent, SoundEvent> MAMMUTILATION_DEATH = register("entity.mammutilation.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAMMUTILATION_HURT = register("entity.mammutilation.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAMMUTILATION_IDLE = register("entity.mammutilation.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAMMUTILATION_HOWL = register("entity.mammutilation.howl");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAMMUTILATION_BLEED = register("entity.mammutilation.bleed");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAMMUTILATION_COUGH = register("entity.mammutilation.cough");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAMMUTIFUL_IDLE = register("entity.mammutilation.mammutiful_idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> MAMMUTIFUL_HOWL = register("entity.mammutilation.mammutiful_howl");
    public static final DeferredHolder<SoundEvent, SoundEvent> ICHOR_BOTTLE = register("item.ichor_bottle.applied");
    public static final DeferredHolder<SoundEvent, SoundEvent> YOUTH_POTION_BABY = register("item.youth_potion.baby");
    public static final DeferredHolder<SoundEvent, SoundEvent> YOUTH_POTION_STUMPED = register("item.youth_potion.stumped");

    public static final DeferredHolder<SoundEvent, SoundEvent> GHOUL_IDLE = register("entity.ghoul.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> GHOUL_HURT = register("entity.ghoul.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> GHOUL_DEATH = register("entity.ghoul.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> GHOUL_CONFUSED = register("entity.ghoul.confused");
    public static final DeferredHolder<SoundEvent, SoundEvent> GHOUL_ATTACK = register("entity.ghoul.attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> GHOUL_INFECT = register("entity.ghoul.infect");
    public static final DeferredHolder<SoundEvent, SoundEvent> GHOUL_AGGRO = register("entity.ghoul.aggro");
    public static final DeferredHolder<SoundEvent, SoundEvent> GHOUL_STEP = register("entity.ghoul.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> GHOUL_SEARCHING = register("entity.ghoul.searching");
    public static final DeferredHolder<SoundEvent, SoundEvent> GHOUL_ANGRY = register("entity.ghoul.angry");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOODLUST_FEED = register("effect.bloodlust.feed");
    public static final DeferredHolder<SoundEvent, SoundEvent> BLOODLUST_REMOVED = register("effect.bloodlust.removed");
    public static final Holder.Reference<SoundEvent> NOTE_BLOCK_IMITATE_GHOUL = registerForHolder("block.note_block.imitate.ghoul");

    public static final DeferredHolder<SoundEvent, SoundEvent> QUAKE_SHIELD = register("entity.quake.shield");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUAKE_HURT = register("entity.quake.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUAKE_ABSORB = register("entity.quake.absorb");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUAKE_DEATH = register("entity.quake.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUAKE_CHARGE = register("entity.quake.charge");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUAKE_ATTACK = register("entity.quake.attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUAKE_UNSCREWS = register("entity.quake.unscrews");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUAKE_FINISHES_UNSCREWING = register("entity.quake.finishes_unscrewing");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUAKE_STEP = register("entity.quake.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> QUAKE_RECHARGE = register("entity.quake.recharge");
    public static final Holder.Reference<SoundEvent> NOTE_BLOCK_QUAKE_SYNTH = registerForHolder("block.note_block.quake_synth");
    public static final Holder.Reference<SoundEvent> NOTE_BLOCK_IMITATE_QUAKE = registerForHolder("block.note_block.imitate.quake");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEFLECTOR_DUMMY_PLACE = register("entity.deflector_dummy.place");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEFLECTOR_DUMMY_BREAK = register("entity.deflector_dummy.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEFLECTOR_DUMMY_HURT = register("entity.deflector_dummy.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEFLECTOR_DUMMY_DEFLECT = register("entity.deflector_dummy.deflect");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEFLECTOR_DUMMY_ABSORB = register("entity.deflector_dummy.absorb");
    public static final DeferredHolder<SoundEvent, SoundEvent> DEFLECTOR_DUMMY_ATTACK = register("entity.deflector_dummy.attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> RICOSHIELD_ABSORB = register("item.ricoshield.absorb");
    public static final DeferredHolder<SoundEvent, SoundEvent> RICOSHIELD_ATTACK = register("item.ricoshield.attack");

    public static final DeferredHolder<SoundEvent, SoundEvent> MUSIC_DISK_SPAWNER = register("music.disk.spawner");

    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_DEATH = register("entity.wicked.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_HAUNT = register("entity.wicked.haunt");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_HURT = register("entity.wicked.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_IDLE = register("entity.wicked.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_IDLE_HAUNTING = register("entity.wicked.idle_haunting");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_SHOOT = register("entity.wicked.shoot");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_SPOT = register("entity.wicked.spot");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_STOP_HAUNTING = register("entity.wicked.stop_haunting");

    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_WAX_EAT = register("item.wicked_wax.eat");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_MASK_EQUIP = register("item.wicked_mask.equip");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_MASK_LINK = register("item.wicked_mask.link");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_SWAPPER_THROW = register("item.wicked_swapper.throw");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_SWAPPER_FAIL = register("item.wicked_swapper.fail");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_SWAPPER_TELEPORT = register("item.wicked_swapper.teleport");

    public static final DeferredHolder<SoundEvent, SoundEvent> MONSTER_MEAL_APPLY = register("item.monster_meal.apply");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_TREAT_APPLY = register("item.wicked_treat.apply");
    public static final DeferredHolder<SoundEvent, SoundEvent> SMOKE_BOMB_CHARGE = register("item.smoke_bomb.charge");
    public static final DeferredHolder<SoundEvent, SoundEvent> SMOKE_BOMB_USE = register("item.smoke_bomb.use");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_DOPE_BOOST = register("item.wicked_dope.boost");
    public static final DeferredHolder<SoundEvent, SoundEvent> WICKED_DOPE_FAIL = register("item.wicked_dope.fail");
    public static final Holder.Reference<SoundEvent> NOTE_BLOCK_IMITATE_WICKED = registerForHolder("block.note_block.imitate.wicked");

    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRE_IDLE = register("entity.spectre.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRE_HURT = register("entity.spectre.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRE_DEATH = register("entity.spectre.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRE_ATTACK = register("entity.spectre.attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRE_HULKING_ATTACK = register("entity.spectre.hulking_attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRE_JOUSTING_ATTACK = register("entity.spectre.jousting_attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRE_SPAWN = register("entity.spectre.spawn");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRE_FLY = register("entity.spectre.fly");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRE_SPOT = register("entity.spectre.spot");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRE_DASH = register("entity.spectre.dash");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRALIBUR_COLLECT_SOUL = register("item.spectralibur.collect_soul");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRALIBUR_USE_SOUL = register("item.spectralibur.use_soul");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRALIBUR_START_CHARGING = register("item.spectralibur.start_charging");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRALIBUR_RELEASE_SPECTRE = register("item.spectralibur.release_spectre");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRALIBUR_CAN_BE_PULLED1 = register("block.spectralibur.can_be_pulled1");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRALIBUR_CAN_BE_PULLED2 = register("block.spectralibur.can_be_pulled2");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRALIBUR_CAN_BE_PULLED3 = register("block.spectralibur.can_be_pulled3");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRALIBUR_PULL = register("block.spectralibur.pull");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRALIBUR_EXTRACT = register("block.spectralibur.extract");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRALIBUR_GO_IN = register("block.spectralibur.go_in");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRALIBUR_GO_IN_FULLY = register("block.spectralibur.go_in_fully");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRALIBUR_CANT_PULL = register("block.spectralibur.cant_pull");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRALIBUR_PEDESTAL_ACTIVATE = register("block.spectralibur_pedestal.activate");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECTRALIBUR_PEDESTAL_DEACTIVATE = register("block.spectralibur_pedestal.deactivate");
    public static final DeferredHolder<SoundEvent, SoundEvent> HOPELIGHT_PLACE = register("block.hopelight.place");
    public static final DeferredHolder<SoundEvent, SoundEvent> HOPELIGHT_BREAK = register("block.hopelight.break");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECLIGHT_ON = register("block.speclight.on");
    public static final DeferredHolder<SoundEvent, SoundEvent> SPECLIGHT_OFF = register("block.speclight.off");

    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_IDLE = register("entity.bewereager.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_HURT = register("entity.bewereager.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_DEATH = register("entity.bewereager.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_STEP = register("entity.bewereager.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_STUN = register("entity.bewereager.stun");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_SLASH = register("entity.bewereager.slash");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_BITE = register("entity.bewereager.bite");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_HOWL = register("entity.bewereager.howl");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_HOWL_STRENGTH = register("entity.bewereager.howl_strength");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_JUMP = register("entity.bewereager.jump");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_LAND = register("entity.bewereager.land");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_SHAKE = register("entity.bewereager.shake");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_SPLIT = register("entity.bewereager.split");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_CELEBRATE = register("entity.bewereager.celebrate");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_TRANSFORM = register("entity.bewereager.transform");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_TRANSFORM_START = register("entity.bewereager.transform_start");
    public static final DeferredHolder<SoundEvent, SoundEvent> BEWEREAGER_SPEED = register("entity.bewereager.speed");
    public static final Holder.Reference<SoundEvent> NOTE_BLOCK_IMITATE_BEWEREAGER = registerForHolder("block.note_block.imitate.bewereager");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRANKTRAP_OPEN = register("block.cranktrap.open");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRANKTRAP_CLOSE = register("block.cranktrap.close");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRANKBOW_PULL = register("item.crankbow.pull");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRANKBOW_PULL_QUICK = register("item.crankbow.quick_pull");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRANKBOW_SHOOT = register("item.crankbow.shoot");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRANKBOW_SHOOT_SPARING = register("item.crankbow.shoot_spare");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRANKBOW_SPEED = register("item.crankbow.speed");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRANKBOW_LOAD_ARROW = register("item.crankbow.load_arrow");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRANKBOW_REMOVE_ARROW = register("item.crankbow.remove_arrow");
    public static final DeferredHolder<SoundEvent, SoundEvent> CRANKBOW_STOP = register("item.crankbow.stop");

    public static final DeferredHolder<SoundEvent, SoundEvent> HANGER_PULL = register("entity.hanger.pull");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLIFF_HANGER_IDLE = register("entity.cliff_hanger.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLIFF_HANGER_IDLE_PULLING = register("entity.cliff_hanger.idle_pulling");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLIFF_HANGER_HURT = register("entity.cliff_hanger.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLIFF_HANGER_DEATH = register("entity.cliff_hanger.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLIFF_HANGER_STEP = register("entity.cliff_hanger.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLIFF_HANGER_SHOOT = register("entity.cliff_hanger.shoot");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLIFF_HANGER_ATTACK = register("entity.cliff_hanger.attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> CLIFF_HANGER_ATTACH = register("entity.cliff_hanger.attach");
    public static final DeferredHolder<SoundEvent, SoundEvent> LEAF_HANGER_IDLE = register("entity.leaf_hanger.idle");
    public static final DeferredHolder<SoundEvent, SoundEvent> LEAF_HANGER_IDLE_PULLING = register("entity.leaf_hanger.idle_pulling");
    public static final DeferredHolder<SoundEvent, SoundEvent> LEAF_HANGER_HURT = register("entity.leaf_hanger.hurt");
    public static final DeferredHolder<SoundEvent, SoundEvent> LEAF_HANGER_DEATH = register("entity.leaf_hanger.death");
    public static final DeferredHolder<SoundEvent, SoundEvent> LEAF_HANGER_STEP = register("entity.leaf_hanger.step");
    public static final DeferredHolder<SoundEvent, SoundEvent> LEAF_HANGER_SHOOT = register("entity.leaf_hanger.shoot");
    public static final DeferredHolder<SoundEvent, SoundEvent> LEAF_HANGER_ATTACK = register("entity.leaf_hanger.attack");
    public static final DeferredHolder<SoundEvent, SoundEvent> LEAF_HANGER_CATCH = register("entity.leaf_hanger.catch");
    public static final DeferredHolder<SoundEvent, SoundEvent> COIL_PLACE = register("entity.coil.place");
    public static final DeferredHolder<SoundEvent, SoundEvent> COIL_LINK = register("entity.coil.link");
    public static final DeferredHolder<SoundEvent, SoundEvent> COIL_REMOVE = register("entity.coil.remove");
    public static final DeferredHolder<SoundEvent, SoundEvent> COIL_ADJUST = register("entity.coil.adjust");
    public static final DeferredHolder<SoundEvent, SoundEvent> HARPOON_THROWN = register("item.harpoon.thrown");
    public static final DeferredHolder<SoundEvent, SoundEvent> HARPOON_RETRIEVED = register("item.harpoon.retrieved");
    public static final DeferredHolder<SoundEvent, SoundEvent> HARPOON_ANCHOR = register("item.harpoon.anchor");
    public static final DeferredHolder<SoundEvent, SoundEvent> HARPOON_START_ZIPLINING = register("item.harpoon.start_ziplining");
    public static final DeferredHolder<SoundEvent, SoundEvent> HARPOON_ZIPLINING = register("item.harpoon.ziplining");
    public static final DeferredHolder<SoundEvent, SoundEvent> HARPOON_PULL = register("item.harpoon.pull");

    public static final SoundType WRAPTOR_EGG_BLOCK = new DeferredSoundType(1.0F, 1.5F, SpeciesSoundEvents.WRAPTOR_EGG_BREAK, () -> SoundEvents.SHROOMLIGHT_STEP, () -> SoundEvents.SHROOMLIGHT_PLACE, () -> SoundEvents.SHROOMLIGHT_HIT, () -> SoundEvents.SHROOMLIGHT_FALL);
    public static final SoundType BIRTDAY_CAKE = register("birtday_cake", 1F, 1.0F);
    public static final SoundType FROZEN_HAIR = register("frozen_hair", 0.8F, 1);
    public static final SoundType FROZEN_MEAT = register("frozen_meat", 0.8F, 1);
    public static final SoundType ALPHACENE_GRASS = register("alphacene_grass", 0.8F, 1.6F);
    public static final SoundType ALPHACENE_MOSS = register("alphacene_moss", 0.8F, 1.6F);
    public static final SoundType ALPHACENE_FOLIAGE = register("alphacene_foliage", 0.8F, 1.6F);
    public static final SoundType HOPELIGHT = new DeferredSoundType(1.0F, 1.0F, SpeciesSoundEvents.HOPELIGHT_BREAK, () -> SoundEvents.EMPTY, SpeciesSoundEvents.HOPELIGHT_PLACE, () -> SoundEvents.EMPTY, () -> SoundEvents.EMPTY);
    public static final SoundType SPECLIGHT = register("speclight", 0.7F, 1.0F);
    public static final SoundType CRANKTRAP = register("cranktrap", 1F, 1F);



    private static SoundType register(String name, float volume, float pitch) {
        return new DeferredSoundType(volume, pitch, register("block." + name + ".break"), register("block." + name + ".step"), register("block." + name + ".place"), register("block." + name + ".hit"), register("block." + name + ".fall"));
    }

    private static Holder.Reference<SoundEvent> registerForHolder(String name) {
        return Registry.registerForHolder(BuiltInRegistries.SOUND_EVENT, ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, name), SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, name)));
    }

    private static DeferredHolder<SoundEvent, SoundEvent> register(String name) {
        ResourceLocation id = ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, name);
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(id));
    }
}
