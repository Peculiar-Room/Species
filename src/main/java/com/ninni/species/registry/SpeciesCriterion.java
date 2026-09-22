package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.criterion.SpeciesCriteriaTriggers;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.core.registries.Registries;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class SpeciesCriterion {

    public static final DeferredRegister<CriterionTrigger<?>> TRIGGER_TYPES =
            DeferredRegister.create(Registries.TRIGGER_TYPE, Species.MOD_ID);
    
    public static final Supplier<SpeciesCriteriaTriggers> BREAK_LIMPET = TRIGGER_TYPES.register("break_limpet", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> SILK_TOUCH_BREAK_LIMPET = TRIGGER_TYPES.register("silk_touch_break_limpet", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> HATCH_WRAPTOR = TRIGGER_TYPES.register("hatch_wraptor", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> BIRT_EGG_AT_WARDEN = TRIGGER_TYPES.register("birt_egg_at_warden", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> SHEAR_WRAPTOR_COMPLETELY = TRIGGER_TYPES.register("shear_wraptor_completely", SpeciesCriteriaTriggers::new);

    public static final Supplier<SpeciesCriteriaTriggers> TURN_MOB_INTO_BABY = TRIGGER_TYPES.register("turn_mob_into_baby", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> EXTEND_SPRINGLING_FULLY = TRIGGER_TYPES.register("extend_springling_fully", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> TAME_TROOPER = TRIGGER_TYPES.register("tame_trooper", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> BURN_TREEPER_INTO_PLACE = TRIGGER_TYPES.register("burn_treeper_into_place", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> FEED_CRUNCHER = TRIGGER_TYPES.register("feed_cruncher", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> TICKLE_GOOBER = TRIGGER_TYPES.register("tickle_goober", SpeciesCriteriaTriggers::new);

    public static final Supplier<SpeciesCriteriaTriggers> KILL_TEN_MOBS_WITH_QUAKE = TRIGGER_TYPES.register("kill_ten_mobs_with_quake", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> KILL_ALL_PREHISTORIC_MOBS_WITH_QUAKE = TRIGGER_TYPES.register("kill_all_prehistoric_mobs_with_quake", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> WICKED_MASK_WITHER = TRIGGER_TYPES.register("wicked_mask_wither", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> WICKED_STOP_HAUNTING = TRIGGER_TYPES.register("wicked_stop_haunting", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> AGGRO_GHOUL = TRIGGER_TYPES.register("aggro_ghoul", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> SURVIVE_GHOUL = TRIGGER_TYPES.register("survive_ghoul", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> FALL_FOR_HANGER = TRIGGER_TYPES.register("fall_for_hanger", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> FALL_FOR_HANGER_TWICE = TRIGGER_TYPES.register("fall_for_hanger_twice", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> CURE_BEWEREAGER = TRIGGER_TYPES.register("cure_bewereager", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> START_SPECTRE_CHALLENGE = TRIGGER_TYPES.register("start_spectre_challenge", SpeciesCriteriaTriggers::new);
    public static final Supplier<SpeciesCriteriaTriggers> SUMMON_SPECTRE = TRIGGER_TYPES.register("summon_spectre", SpeciesCriteriaTriggers::new);

}