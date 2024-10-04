package com.ninni.species.criterion;

import com.ninni.species.Species;
import com.ninni.species.init.SpeciesCriteriaTriggers;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Species.MOD_ID)
public class SpeciesCriterion {

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

}