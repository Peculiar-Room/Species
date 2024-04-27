package com.ninni.species.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.ninni.species.data.CruncherPelletManager;
import com.ninni.species.registry.SpeciesBlockEntities;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class CruncherPelletBlockEntity extends BlockEntity {
    @Nullable
    private CruncherPelletManager.CruncherPelletData data = null;
    private static final Logger LOGGER = LogUtils.getLogger();

    public CruncherPelletBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(SpeciesBlockEntities.CRUNCHER_PELLET, blockPos, blockState);
    }

    @Override
    public void load(CompoundTag compoundTag) {
        super.load(compoundTag);
        if (compoundTag.contains("PelletData", 10)) {
            CruncherPelletManager.CruncherPelletData.CODEC.parse(new Dynamic<>(NbtOps.INSTANCE, compoundTag.getCompound("PelletData"))).resultOrPartial(LOGGER::error).ifPresent(this::setPelletData);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag compoundTag) {
        super.saveAdditional(compoundTag);
        if (this.getPelletData() != null) {
            CruncherPelletManager.CruncherPelletData.CODEC.encodeStart(NbtOps.INSTANCE, this.getPelletData()).resultOrPartial(LOGGER::error).ifPresent(tag -> compoundTag.put("PelletData", tag));
        }
    }

    public CruncherPelletManager.CruncherPelletData getPelletData() {
        return this.data;
    }

    public void setPelletData(CruncherPelletManager.CruncherPelletData data) {
        this.data = data;
    }

    public void unpackLootTable(Player player) {

        CruncherPelletManager.CruncherPelletData pelletData = this.getPelletData();
        if (pelletData == null || this.level == null || this.level.isClientSide || this.level.getServer() == null) return;

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.GENERATE_LOOT.trigger(serverPlayer, pelletData.entityType().getDefaultLootTable());
        }

        int count = UniformInt.of(pelletData.minTries(), pelletData.maxTries()).sample(player.getRandom());

        for (int i = 0; i < count; i++) {
            ObjectArrayList<ItemStack> randomDrops = this.getRandomDrops(player);
            randomDrops.forEach(this::spawnItem);
        }

        this.setChanged();
    }

    private void spawnItem(ItemStack itemStack) {
        Block.popResource(this.level, this.worldPosition, itemStack);
    }

    public ObjectArrayList<ItemStack> getRandomDrops(Player player) {
        CruncherPelletManager.CruncherPelletData data = this.getPelletData();
        ResourceLocation resourceLocation = data.entityType().getDefaultLootTable();
        LootTable lootTable = this.level.getServer().getLootData().getLootTable(resourceLocation);
        Entity entity = data.entityType().create(this.level);

        DamageSource damageSource;

        if (entity instanceof LivingEntity livingEntity) {
            damageSource = this.level.damageSources().mobAttack(livingEntity);
        } else {
            damageSource = this.level.damageSources().fellOutOfWorld();
        }

        LootParams.Builder builder = new LootParams.Builder((ServerLevel)this.level)
                .withParameter(LootContextParams.THIS_ENTITY, entity)
                .withParameter(LootContextParams.DAMAGE_SOURCE, damageSource)
                .withParameter(LootContextParams.ORIGIN, Vec3.atCenterOf(this.worldPosition))
                .withOptionalParameter(LootContextParams.KILLER_ENTITY, player)
                .withOptionalParameter(LootContextParams.DIRECT_KILLER_ENTITY, player)
                .withParameter(LootContextParams.LAST_DAMAGE_PLAYER, player)
                .withLuck(player.getLuck());

        LootParams lootParams = builder.create(LootContextParamSets.ENTITY);

        return lootTable.getRandomItems(lootParams);
    }

}
