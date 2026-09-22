package com.ninni.species.registry;

import com.mojang.serialization.Codec;
import com.ninni.species.Species;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.JukeboxSongs;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpeciesJukeboxSongs {
    public static final DeferredRegister<JukeboxSong> SONGS = DeferredRegister.create(Registries.JUKEBOX_SONG, Species.MOD_ID);

    public static final DeferredHolder<JukeboxSong, JukeboxSong> DIAL = SONGS.register(
            "dial",
            (location) -> new JukeboxSong(SpeciesSoundEvents.MUSIC_DISC_DIAL, Component.translatable(Util.makeDescriptionId("jukebox_song", location)), 193, 1)
    );
    public static final DeferredHolder<JukeboxSong, JukeboxSong> LAPIDARIAN = SONGS.register(
            "lapidarian",
            (location) -> new JukeboxSong(SpeciesSoundEvents.MUSIC_DISC_LAPIDARIAN, Component.translatable(Util.makeDescriptionId("jukebox_song", location)), 200, 2)
    );
    public static final DeferredHolder<JukeboxSong, JukeboxSong> SPAWNER = SONGS.register(
            "spawner",
            (location) -> new JukeboxSong(SpeciesSoundEvents.MUSIC_DISK_SPAWNER, Component.translatable(Util.makeDescriptionId("jukebox_song", location)), 136, 3)
    );
    
}