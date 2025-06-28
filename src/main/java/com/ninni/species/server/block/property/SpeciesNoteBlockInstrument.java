package com.ninni.species.server.block.property;

import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.core.Holder;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;

public enum SpeciesNoteBlockInstrument {
    QUAKE_SYNTH("quake_synth", SpeciesSoundEvents.NOTE_BLOCK_QUAKE_SYNTH, NoteBlockInstrument.Type.BASE_BLOCK),
    WICKED("wicked", SpeciesSoundEvents.NOTE_BLOCK_IMITATE_WICKED, NoteBlockInstrument.Type.MOB_HEAD),
    QUAKE("quake", SpeciesSoundEvents.NOTE_BLOCK_IMITATE_QUAKE, NoteBlockInstrument.Type.MOB_HEAD),
    GHOUL("ghoul", SpeciesSoundEvents.NOTE_BLOCK_IMITATE_GHOUL, NoteBlockInstrument.Type.MOB_HEAD),
    BEWEREAGER("bewereager", SpeciesSoundEvents.NOTE_BLOCK_IMITATE_BEWEREAGER, NoteBlockInstrument.Type.MOB_HEAD);

    private final String string;
    private final Holder<SoundEvent> soundEvent;
    private final NoteBlockInstrument.Type type;

    SpeciesNoteBlockInstrument(String string, Holder sound, NoteBlockInstrument.Type type) {
        this.string = string;
        this.soundEvent = sound;
        this.type = type;
    }

    public NoteBlockInstrument.Type getType() {
        return type;
    }

    public Holder<SoundEvent> getSoundEvent() {
        return soundEvent;
    }

    public String getString() {
        return string;
    }

    public NoteBlockInstrument get() {
        return NoteBlockInstrument.valueOf(this.name());
    }
}
