package com.ninni.species.registry;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public class SpeciesBlockSoundGroup {

    public static final SoundType WRAPTOR_EGG = new SoundType(
        1.0F, 1.5F,

        SpeciesSoundEvents.WRAPTOR_EGG_BREAK,
        SoundEvents.SHROOMLIGHT_STEP,
        SoundEvents.SHROOMLIGHT_PLACE,
        SoundEvents.SHROOMLIGHT_HIT,
        SoundEvents.SHROOMLIGHT_FALL
    );
}
