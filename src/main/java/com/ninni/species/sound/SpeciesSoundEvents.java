package com.ninni.species.sound;

import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import static com.ninni.species.Species.*;

public interface SpeciesSoundEvents {

    SoundEvent ENTITY_WRAPTOR_AGGRO        = wraptor("aggro");
    SoundEvent ENTITY_WRAPTOR_AGITATED     = wraptor("agitated");
    SoundEvent ENTITY_WRAPTOR_ATTACK       = wraptor("attack");
    SoundEvent ENTITY_WRAPTOR_DEATH        = wraptor("death");
    SoundEvent ENTITY_WRAPTOR_HURT         = wraptor("hurt");
    SoundEvent ENTITY_WRAPTOR_IDLE         = wraptor("idle");
    SoundEvent ENTITY_WRAPTOR_SHEAR        = wraptor("shear");
    SoundEvent ENTITY_WRAPTOR_STEP         = wraptor("step");
    SoundEvent ENTITY_WRAPTOR_FEATHER_LOSS = wraptor("feather_loss");
    private static SoundEvent wraptor(String type) {
        return createEntitySound("wraptor", type);
    }

    SoundEvent BLOCK_WRAPTOR_EGG_BREAK = wraptor_egg("break");
    SoundEvent BLOCK_WRAPTOR_EGG_CRACK = wraptor_egg("crack");
    SoundEvent BLOCK_WRAPTOR_EGG_HATCH = wraptor_egg("hatch");
    private static SoundEvent wraptor_egg(String type) {
        return createBlockSound("wraptor_egg", type);
    }

    SoundEvent ITEM_CRACKED_WRAPTOR_EGG_SLURP = cracked_wraptor_egg("slurp");
    private static SoundEvent cracked_wraptor_egg(String type) {
        return createItemSound("cracked_wraptor_egg", type);
    }


    private static SoundEvent register(String id) {
        Identifier identifier = new Identifier(MOD_ID, id);
        return Registry.register(Registry.SOUND_EVENT, identifier, new SoundEvent(identifier));
    }
    private static SoundEvent createEntitySound(String entity, String id) {
        return register("entity." + entity + "." + id);
    }
    private static SoundEvent createBlockSound(String block, String id) {
        return register("block." + block + "." + id);
    }
    private static SoundEvent createItemSound(String item, String id) {
        return register("item." + item + "." + id);
    }
}
