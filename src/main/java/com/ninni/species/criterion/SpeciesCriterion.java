package com.ninni.species.criterion;

import net.minecraft.advancements.CriteriaTriggers;

public class SpeciesCriterion {
    public static SpeciesCriteriaTriggers SILK_TOUCH_BREAK_LIMPET;
    public static SpeciesCriteriaTriggers HATCH_WRAPTOR;
    public static SpeciesCriteriaTriggers BIRT_EGG_AT_WARDEN;
    public static SpeciesCriteriaTriggers SHEAR_WRAPTOR_COMPLETELY;
    public static SpeciesCriteriaTriggers TURN_MOB_INTO_BABY;
    public static SpeciesCriteriaTriggers EXTEND_SPRINGLING_FULLY;
    public static SpeciesCriteriaTriggers TAME_TROOPER;
    public static SpeciesCriteriaTriggers BURN_TREEPER_INTO_PLACE;
    public static SpeciesCriteriaTriggers FEED_CRUNCHER;
    public static SpeciesCriteriaTriggers TICKLE_GOOBER;


    public static void init() {
        SILK_TOUCH_BREAK_LIMPET = CriteriaTriggers.register(new SpeciesCriteriaTriggers("silk_touch_break_limpet"));
        HATCH_WRAPTOR = CriteriaTriggers.register(new SpeciesCriteriaTriggers("hatch_wraptor"));
        BIRT_EGG_AT_WARDEN = CriteriaTriggers.register(new SpeciesCriteriaTriggers("birt_egg_at_warden"));
        SHEAR_WRAPTOR_COMPLETELY = CriteriaTriggers.register(new SpeciesCriteriaTriggers("shear_wraptor_completely"));
        TURN_MOB_INTO_BABY = CriteriaTriggers.register(new SpeciesCriteriaTriggers("turn_mob_into_baby"));
        EXTEND_SPRINGLING_FULLY = CriteriaTriggers.register(new SpeciesCriteriaTriggers("extend_springling_fully"));
        TAME_TROOPER = CriteriaTriggers.register(new SpeciesCriteriaTriggers("tame_trooper"));
        BURN_TREEPER_INTO_PLACE = CriteriaTriggers.register(new SpeciesCriteriaTriggers("burn_treeper_into_place"));
        FEED_CRUNCHER = CriteriaTriggers.register(new SpeciesCriteriaTriggers("feed_cruncher"));
        TICKLE_GOOBER = CriteriaTriggers.register(new SpeciesCriteriaTriggers("tickle_goober"));
    }
}
