package com.ninni.species.sound;

import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;

public class SpeciesBlockSoundGroup {

    public static final BlockSoundGroup WRAPTOR_EGG = new BlockSoundGroup(
        1.0F, 1.5F,

        SpeciesSoundEvents.BLOCK_WRAPTOR_EGG_BREAK,
        SoundEvents.BLOCK_SHROOMLIGHT_STEP,
        SoundEvents.BLOCK_SHROOMLIGHT_PLACE,
        SoundEvents.BLOCK_SHROOMLIGHT_HIT,
        SoundEvents.BLOCK_SHROOMLIGHT_FALL
    );
}
