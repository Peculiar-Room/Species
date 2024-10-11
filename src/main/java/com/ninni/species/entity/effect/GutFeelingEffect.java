package com.ninni.species.entity.effect;

import com.ninni.species.entity.Cruncher;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.SpawnPlacements;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.Heightmap;

public class GutFeelingEffect extends MobEffect {

    public GutFeelingEffect(MobEffectCategory mobEffectCategory, int i) {
        super(mobEffectCategory, i);
    }

    @Override
    public boolean isDurationEffectTick(int i, int j) {
        return i == 1;
    }

    @Override
    public void applyEffectTick(LivingEntity livingEntity, int i) {
        if (livingEntity instanceof ServerPlayer serverPlayer) {
            if (!livingEntity.isSpectator()) {
                BlockPos blockPos = serverPlayer.blockPosition();
                Level level = livingEntity.level();

                if (!(level instanceof ServerLevel serverLevel)) return;

                int k = i == 0 ? 2 : 2 - i;
                int j = 20;
                BlockPos spawnPos = null;
                BlockPos.MutableBlockPos mutableBlockPos = new BlockPos.MutableBlockPos();
                for (int l = 0; l < j; ++l) {
                    float f = level.random.nextFloat() * ((float)Math.PI * 2);
                    int m = blockPos.getX() + Mth.floor(Mth.cos(f) * 20.0f * (float)k) + level.random.nextInt(5);
                    int n = blockPos.getZ() + Mth.floor(Mth.sin(f) * 20.0f * (float)k) + level.random.nextInt(5);
                    int o = level.getHeight(Heightmap.Types.WORLD_SURFACE, m, n);

                    if (o < blockPos.getY() - 20) {
                        m = blockPos.getX() + Mth.floor(Mth.cos(f) * 19.0f * (float)k) + level.random.nextInt(5);
                        n = blockPos.getZ() + Mth.floor(Mth.sin(f) * 19.0f * (float)k) + level.random.nextInt(5);
                        o = level.getHeight(Heightmap.Types.WORLD_SURFACE, m, n);
                    }

                    mutableBlockPos.set(m, o, n);
                    if (serverLevel.isVillage(mutableBlockPos) && i < 2) continue;

                    if (!serverLevel.hasChunksAt(mutableBlockPos.getX() - 10, mutableBlockPos.getZ() - 10, mutableBlockPos.getX() + 10, mutableBlockPos.getZ() + 10)
                            || !serverLevel.isPositionEntityTicking(mutableBlockPos)
                            || !NaturalSpawner.isSpawnPositionOk(SpawnPlacements.Type.ON_GROUND, serverLevel, mutableBlockPos, SpeciesEntities.CRUNCHER.get())
                            && (!serverLevel.getBlockState(mutableBlockPos.below()).is(Blocks.SNOW) || !serverLevel.getBlockState(mutableBlockPos).isAir())) continue;

                    spawnPos = mutableBlockPos;
                    break;
                }
                if (spawnPos != null) {
                    serverLevel.playSound(null, livingEntity.blockPosition(), SpeciesSoundEvents.GUT_FEELING_SPAWN.get(), SoundSource.HOSTILE, 2, 1);
                    Cruncher cruncher = SpeciesEntities.CRUNCHER.get().spawn(serverLevel, (CompoundTag)null, null, spawnPos, MobSpawnType.TRIGGERED, true, false);
                    if (cruncher != null) cruncher.getBrain().setMemory(MemoryModuleType.NEAREST_ATTACKABLE, serverPlayer);
                }
            }
        }
    }

}