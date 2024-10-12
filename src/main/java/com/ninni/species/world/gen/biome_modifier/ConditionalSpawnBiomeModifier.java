package com.ninni.species.world.gen.biome_modifier;

import com.mojang.serialization.Codec;
import com.ninni.species.registry.SpeciesBiomeModifiers;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.MobSpawnSettingsBuilder;
import net.minecraftforge.common.world.ModifiableBiomeInfo;

import java.util.List;

public record ConditionalSpawnBiomeModifier(HolderSet<Biome> biomes, HolderSet<Biome> filtered, List<MobSpawnSettings.SpawnerData> spawners) implements BiomeModifier {
    @Override
    public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        if (phase == Phase.ADD && this.biomes.contains(biome) && !this.filtered.contains(biome)) {
            MobSpawnSettingsBuilder spawns = builder.getMobSpawnSettings();
            for (MobSpawnSettings.SpawnerData spawner : this.spawners) {
                EntityType<?> type = spawner.type;
                spawns.addSpawn(type.getCategory(), spawner);
            }
        }
    }

    @Override
    public Codec<? extends BiomeModifier> codec() {
        return SpeciesBiomeModifiers.ADD_SPAWNS_BIOME_MODIFIER_TYPE.get();
    }
}
