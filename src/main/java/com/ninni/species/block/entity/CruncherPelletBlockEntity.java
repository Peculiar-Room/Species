package com.ninni.species.block.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.ninni.species.block.CruncherPelletBlock;
import com.ninni.species.data.CruncherPelletManager;
import com.ninni.species.mixin.BrushableBlockEntityAccessor;
import com.ninni.species.registry.SpeciesBlockEntities;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.BrushableBlock;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.BrushableBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.gameevent.vibrations.VibrationSystem;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.Objects;

public class CruncherPelletBlockEntity extends BrushableBlockEntity {
    @Nullable
    private CruncherPelletManager.CruncherPelletData data = null;
    private static final Logger LOGGER = LogUtils.getLogger();

    public CruncherPelletBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(blockPos, blockState);
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
        CruncherPelletManager.CruncherPelletData.CODEC.encodeStart(NbtOps.INSTANCE, this.getPelletData()).resultOrPartial(LOGGER::error).ifPresent(tag -> compoundTag.put("PelletData", tag));
    }

    public CruncherPelletManager.CruncherPelletData getPelletData() {
        return this.data;
    }

    public void setPelletData(CruncherPelletManager.CruncherPelletData data) {
        this.data = data;
    }

    @Override
    public boolean brush(long l, Player player, Direction direction) {
        BrushableBlockEntityAccessor accessor = (BrushableBlockEntityAccessor) this;

        if (this.getPelletData() == null) return false;

        if (accessor.getHitDirection() == null) {
            accessor.setHitDirection(direction);
        }
        accessor.setBrushCountResetsAtTick(l + 40L);
        if (l < accessor.getCoolDownEndsAtTick() || !(this.level instanceof ServerLevel)) {
            return false;
        }
        accessor.setCoolDownEndsAtTick(l + 10L);
        this.unpackLootTable(player);
        int i = this.getCompletionState();
        accessor.setBrushCount(accessor.getBrushCount() + 1);
        if (accessor.getBrushCount() >= 10) {
            this.unpackLootTable(player);
            this.brushingCompleted();
            return true;
        }
        this.level.scheduleTick(this.getBlockPos(), this.getBlockState().getBlock(), 40);
        int j = this.getCompletionState();
        if (i != j) {
            BlockState blockState = this.getBlockState();
            BlockState blockState2 = blockState.setValue(BlockStateProperties.DUSTED, j);
            this.level.setBlock(this.getBlockPos(), blockState2, 3);
        }
        return false;
    }

    @Override
    public void unpackLootTable(Player player) {
        BrushableBlockEntityAccessor accessor = (BrushableBlockEntityAccessor) this;

        if (this.getPelletData() == null || this.level == null || this.level.isClientSide || this.level.getServer() == null) return;

        if (player instanceof ServerPlayer serverPlayer) {
            CriteriaTriggers.GENERATE_LOOT.trigger(serverPlayer, this.getPelletData().entityType().getDefaultLootTable());
        }

        ObjectArrayList<ItemStack> randomDrops = this.getRandomDrops(player);

        if (this.getItem().isEmpty()) {
            accessor.setItem(randomDrops.get(0));
        }

        if (accessor.getBrushCount() >= 10) {
            for (int i = 0; i < 32; i++) {
                randomDrops = this.getRandomDrops(player);
                randomDrops.forEach(this::spawnItem);
            }
            this.brushingCompleted();
        }

        accessor.setLootTable(null);
        this.setChanged();
    }

    private void brushingCompleted() {
        if (this.level == null || this.level.getServer() == null) return;

        BrushableBlockEntityAccessor accessor = (BrushableBlockEntityAccessor) this;

        BlockState blockState = this.getBlockState();
        this.level.levelEvent(3008, this.getBlockPos(), Block.getId(blockState));
        this.level.setBlock(this.worldPosition, blockState.setValue(BlockStateProperties.DUSTED, 0), 2);
        this.setPelletData(null);
        accessor.setBrushCount(0);
    }

    private void spawnItem(ItemStack itemStack) {
        BrushableBlockEntityAccessor accessor = (BrushableBlockEntityAccessor) this;
        Direction direction = Objects.requireNonNullElse(accessor.getHitDirection(), Direction.UP);
        BlockPos blockPos = this.worldPosition.relative(direction, 1);
        double d = EntityType.ITEM.getWidth();
        double e = 1.0 - d;
        double f = d / 2.0;
        double g = (double) blockPos.getX() + 0.5 * e + f;
        double h = (double) blockPos.getY() + 0.5 + (double)(EntityType.ITEM.getHeight() / 2.0f);
        double i = (double) blockPos.getZ() + 0.5 * e + f;
        ItemEntity itemEntity = new ItemEntity(this.level, g, h, i, itemStack.split(this.level.random.nextInt(21) + 10));
        itemEntity.setDeltaMovement(Vec3.ZERO);
        this.level.addFreshEntity(itemEntity);
    }

    private int getCompletionState() {
        BrushableBlockEntityAccessor accessor = (BrushableBlockEntityAccessor) this;
        if (accessor.getBrushCount() == 0) {
            return 0;
        }
        if (accessor.getBrushCount() < 3) {
            return 1;
        }
        if (accessor.getBrushCount() < 6) {
            return 2;
        }
        return 3;
    }

    @Override
    public BlockEntityType<?> getType() {
        return SpeciesBlockEntities.CRUNCHER_PELLET;
    }

    public ObjectArrayList<ItemStack> getRandomDrops(Player player) {
        BrushableBlockEntityAccessor accessor = (BrushableBlockEntityAccessor) this;
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

        return lootTable.getRandomItems(lootParams, accessor.getLootTableSeed());
    }

}
