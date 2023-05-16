package com.ninni.species.criterion;

import net.minecraft.advancements.CriteriaTriggers;

public class SpeciesCriterion {
    public static SpeciesCriteriaTriggers SILK_TOUCH_BREAK_LIMPET;
    public static SpeciesCriteriaTriggers HATCH_WRAPTOR;
    public static SpeciesCriteriaTriggers BIRT_EGG_AT_WARDEN;
    public static SpeciesCriteriaTriggers SHEAR_WRAPTOR_COMPLETELY;


    public static void init() {
        SILK_TOUCH_BREAK_LIMPET = CriteriaTriggers.register(new SpeciesCriteriaTriggers("silk_touch_break_limpet"));
        HATCH_WRAPTOR = CriteriaTriggers.register(new SpeciesCriteriaTriggers("hatch_wraptor"));
        BIRT_EGG_AT_WARDEN = CriteriaTriggers.register(new SpeciesCriteriaTriggers("birt_egg_at_warden"));
        SHEAR_WRAPTOR_COMPLETELY = CriteriaTriggers.register(new SpeciesCriteriaTriggers("shear_wraptor_completely"));
    }
}
