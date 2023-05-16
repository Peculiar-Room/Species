package com.ninni.species.criterion;

import com.ninni.species.Species;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Species.MOD_ID)
public class SpeciesCriterion {

    public static final SpeciesCriteriaTriggers SILK_TOUCH_BREAK_LIMPET = CriteriaTriggers.register(new SpeciesCriteriaTriggers("silk_touch_break_limpet"));
    public static final SpeciesCriteriaTriggers HATCH_WRAPTOR = CriteriaTriggers.register(new SpeciesCriteriaTriggers("hatch_wraptor"));
    public static final SpeciesCriteriaTriggers BIRT_EGG_AT_WARDEN = CriteriaTriggers.register(new SpeciesCriteriaTriggers("birt_egg_at_warden"));
    public static final SpeciesCriteriaTriggers SHEAR_WRAPTOR_COMPLETELY = CriteriaTriggers.register(new SpeciesCriteriaTriggers("shear_wraptor_completely"));

}