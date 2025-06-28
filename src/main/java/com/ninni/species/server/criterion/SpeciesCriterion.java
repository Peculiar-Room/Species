package com.ninni.species.server.criterion;

import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesCriteriaTriggers;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Species.MOD_ID)
public class SpeciesCriterion {

    public static final SpeciesCriteriaTriggers BREAK_LIMPET = CriteriaTriggers.register(new SpeciesCriteriaTriggers("break_limpet"));
    public static final SpeciesCriteriaTriggers SILK_TOUCH_BREAK_LIMPET = CriteriaTriggers.register(new SpeciesCriteriaTriggers("silk_touch_break_limpet"));
    public static final SpeciesCriteriaTriggers HATCH_WRAPTOR = CriteriaTriggers.register(new SpeciesCriteriaTriggers("hatch_wraptor"));
    public static final SpeciesCriteriaTriggers BIRT_EGG_AT_WARDEN = CriteriaTriggers.register(new SpeciesCriteriaTriggers("birt_egg_at_warden"));
    public static final SpeciesCriteriaTriggers SHEAR_WRAPTOR_COMPLETELY = CriteriaTriggers.register(new SpeciesCriteriaTriggers("shear_wraptor_completely"));

    public static final SpeciesCriteriaTriggers TURN_MOB_INTO_BABY = CriteriaTriggers.register(new SpeciesCriteriaTriggers("turn_mob_into_baby"));
    public static final SpeciesCriteriaTriggers EXTEND_SPRINGLING_FULLY = CriteriaTriggers.register(new SpeciesCriteriaTriggers("extend_springling_fully"));
    public static final SpeciesCriteriaTriggers TAME_TROOPER = CriteriaTriggers.register(new SpeciesCriteriaTriggers("tame_trooper"));
    public static final SpeciesCriteriaTriggers BURN_TREEPER_INTO_PLACE = CriteriaTriggers.register(new SpeciesCriteriaTriggers("burn_treeper_into_place"));
    public static final SpeciesCriteriaTriggers FEED_CRUNCHER = CriteriaTriggers.register(new SpeciesCriteriaTriggers("feed_cruncher"));
    public static final SpeciesCriteriaTriggers TICKLE_GOOBER = CriteriaTriggers.register(new SpeciesCriteriaTriggers("tickle_goober"));

    public static final SpeciesCriteriaTriggers KILL_TEN_MOBS_WITH_QUAKE = CriteriaTriggers.register(new SpeciesCriteriaTriggers("kill_ten_mobs_with_quake"));
    public static final SpeciesCriteriaTriggers KILL_ALL_PREHISTORIC_MOBS_WITH_QUAKE = CriteriaTriggers.register(new SpeciesCriteriaTriggers("kill_all_prehistoric_mobs_with_quake"));
    public static final SpeciesCriteriaTriggers WICKED_MASK_WITHER = CriteriaTriggers.register(new SpeciesCriteriaTriggers("wicked_mask_wither"));
    public static final SpeciesCriteriaTriggers WICKED_STOP_HAUNTING = CriteriaTriggers.register(new SpeciesCriteriaTriggers("wicked_stop_haunting"));
    public static final SpeciesCriteriaTriggers AGGRO_GHOUL = CriteriaTriggers.register(new SpeciesCriteriaTriggers("aggro_ghoul"));
    public static final SpeciesCriteriaTriggers SURVIVE_GHOUL = CriteriaTriggers.register(new SpeciesCriteriaTriggers("survive_ghoul"));
    public static final SpeciesCriteriaTriggers FALL_FOR_HANGER = CriteriaTriggers.register(new SpeciesCriteriaTriggers("fall_for_hanger"));
    public static final SpeciesCriteriaTriggers FALL_FOR_HANGER_TWICE = CriteriaTriggers.register(new SpeciesCriteriaTriggers("fall_for_hanger_twice"));
    public static final SpeciesCriteriaTriggers CURE_BEWEREAGER = CriteriaTriggers.register(new SpeciesCriteriaTriggers("cure_bewereager"));
    public static final SpeciesCriteriaTriggers START_SPECTRE_CHALLENGE = CriteriaTriggers.register(new SpeciesCriteriaTriggers("start_spectre_challenge"));
    public static final SpeciesCriteriaTriggers SUMMON_SPECTRE = CriteriaTriggers.register(new SpeciesCriteriaTriggers("summon_spectre"));

}