package com.ninni.species.sound;

import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.level.block.SoundType;

public class SpeciesBlockSoundGroup {

    public static final SoundType WRAPTOR_EGG = new SoundType(
        1.0F, 1.5F,

        SpeciesSoundEvents.BLOCK_WRAPTOR_EGG_BREAK,
        SoundEvents.SHROOMLIGHT_STEP,
        SoundEvents.SHROOMLIGHT_PLACE,
        SoundEvents.SHROOMLIGHT_HIT,
        SoundEvents.SHROOMLIGHT_FALL
    );
}
