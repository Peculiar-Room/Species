package com.ninni.species.registry;

import com.ninni.species.entity.Cruncher;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;

public class SpeciesEntityDataSerializers {
    public static final EntityDataSerializer<Cruncher.CruncherState> CRUNCHER_STATE = EntityDataSerializer.simpleEnum(Cruncher.CruncherState.class);

    public static void init() {
        EntityDataSerializers.registerSerializer(CRUNCHER_STATE);
    }
}
