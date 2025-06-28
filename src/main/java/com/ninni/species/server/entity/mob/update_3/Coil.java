package com.ninni.species.server.entity.mob.update_3;

import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.entity.util.SpeciesPose;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class Coil extends Entity {
    public static final EntityDataAccessor<Boolean> START_POINT = SynchedEntityData.defineId(Coil.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Optional<UUID>> END_POINT_UUID = SynchedEntityData.defineId(Coil.class, EntityDataSerializers.OPTIONAL_UUID);
    public static final EntityDataAccessor<Optional<BlockPos>> END_POINT_POS = SynchedEntityData.defineId(Coil.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    public static final EntityDataAccessor<Integer> LOOSENESS = SynchedEntityData.defineId(Coil.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Boolean> IS_KNOT = SynchedEntityData.defineId(Coil.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> IS_WAXED = SynchedEntityData.defineId(Coil.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Boolean> IS_BEING_PLACED = SynchedEntityData.defineId(Coil.class, EntityDataSerializers.BOOLEAN);
    private static final EntityDimensions KNOT_DIMENSIONS = EntityDimensions.scalable(0.4F, 0.5F);
    private int lifeTime;
    private int checkTimer = 200;

    public Coil(EntityType<? extends Coil> type, Level world) {
        super(type, world);
    }

    public Coil(Level world, boolean isStartPoint, Vec3 pos, @Nullable UUID endPointUUID, @Nullable BlockPos endPointPos) {
        this(SpeciesEntities.COIL.get(), world);
        this.setStartPoint(isStartPoint);
        this.setPos(pos);
        this.setEndPointUUID(endPointUUID);
        this.setEndPointPos(endPointPos);
        this.playSound(isStartPoint ? SpeciesSoundEvents.COIL_PLACE.get() : SpeciesSoundEvents.COIL_LINK.get(), 1, 1);
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand hand) {
        ItemStack itemInHand = player.getItemInHand(hand);

        if (!this.getUUID().equals(this.getEndPointUUID())) {

            if (this.isWaxed()) {
                if (itemInHand.getItem() instanceof AxeItem) {
                    itemInHand.hurtAndBreak(1, player, p -> p.broadcastBreakEvent(hand));

                    this.setWaxed(false);
                    level().playSound(player, getBlockPos(), SoundEvents.AXE_WAX_OFF, SoundSource.BLOCKS, 1.0F, 1.0F);
                    level().levelEvent(player, 3004, getBlockPos(), 0);
                    if (this.getEndPoint() != null) {
                        this.getEndPoint().setWaxed(false);
                        level().levelEvent(player, 3004, this.getEndPoint().getBlockPos(), 0);
                    }

                    return InteractionResult.SUCCESS;
                }
            } else {
                if (itemInHand.is(Items.HONEYCOMB)) {

                    this.setWaxed(true);
                    level().levelEvent(player, 3003, getBlockPos(), 0);
                    if (this.getEndPoint() != null) {
                        this.getEndPoint().setWaxed(true);
                        level().levelEvent(player, 3003, this.getEndPoint().getBlockPos(), 0);
                    }

                    itemInHand.shrink(1);
                } else this.cycleLooseness();
                return InteractionResult.SUCCESS;
            }
        }

        return super.interact(player, hand);
    }

    @Override
    public void tick() {
        super.tick();

        if (this.isBeingPlaced()) {
            if (++lifeTime > 20 * 20) destroy(null);
            return;
        }

        if (--checkTimer <= 0) {
            checkTimer = 200;
            if (this.isKnot() && level().isEmptyBlock(BlockPos.containing(this.position()))) destroy(null);
            if (!this.isKnot() && level().isEmptyBlock(getBlockPos())) destroy(null);
        }
    }

    @Override
    public boolean hurt(DamageSource damageSource, float v) {
        if (damageSource.getEntity() instanceof Player player) destroy(player);
        return super.hurt(damageSource, v);
    }

    public @NotNull BlockPos getBlockPos() {
        Direction direction2;
        if (Math.abs(this.getXRot()) > 45) direction2 = this.getXRot() > 0 ? Direction.DOWN : Direction.UP;
        else direction2 = Direction.fromYRot(this.getYRot());
        Vec3 offset = Vec3.atLowerCornerOf(direction2.getNormal()).scale(-0.125).add(this.position());
        return BlockPos.containing(offset);
    }

    @Override
    public EntityDimensions getDimensions(Pose pose) {
        return (pose == SpeciesPose.KNOT.get()) ? KNOT_DIMENSIONS : super.getDimensions(pose);
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(END_POINT_UUID, Optional.empty());
        this.entityData.define(END_POINT_POS, Optional.empty());
        this.entityData.define(START_POINT, true);
        this.entityData.define(IS_KNOT, false);
        this.entityData.define(IS_WAXED, false);
        this.entityData.define(IS_BEING_PLACED, false);
        this.entityData.define(LOOSENESS, 0);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        this.setStartPoint(tag.getBoolean("StartPoint"));
        this.setLooseness(tag.getInt("Looseness"));
        this.setKnot(tag.getBoolean("IsKnot"));
        this.setWaxed(tag.getBoolean("IsWaxed"));
        this.setIsBeingPlaced(tag.getBoolean("IsBeingPlaced"));
        if (tag.hasUUID("EndPointUUID")) this.setEndPointUUID(tag.getUUID("EndPointUUID"));
        if (tag.contains("EndPointPos")) this.setEndPointPos(NbtUtils.readBlockPos(tag.getCompound("EndPointPos")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        tag.putBoolean("StartPoint", this.isStartPoint());
        tag.putInt("Looseness", this.getLooseness());
        tag.putBoolean("IsKnot", this.isKnot());
        tag.putBoolean("IsWaxed", this.isWaxed());
        tag.putBoolean("IsBeingPlaced", this.isBeingPlaced());
        if (this.getEndPointUUID() != null) tag.putUUID("EndPointUUID", this.getEndPointUUID());
        if (this.getEndPointPos() != null) tag.put("EndPointPos", NbtUtils.writeBlockPos(this.getEndPointPos()));
    }

    @Nullable
    public UUID getEndPointUUID() {
        return this.entityData.get(END_POINT_UUID).orElse(null);
    }
    public void setEndPointUUID(@Nullable UUID uuid) {
        this.entityData.set(END_POINT_UUID, Optional.ofNullable(uuid));
    }

    public boolean isStartPoint() {
        return this.entityData.get(START_POINT);
    }
    public void setStartPoint(boolean bl) {
        this.entityData.set(START_POINT, bl);
    }

    public boolean isKnot() {
        return this.entityData.get(IS_KNOT);
    }
    public void setKnot(boolean bl) {
        setPose(bl ? SpeciesPose.KNOT.get() : Pose.STANDING);
        this.refreshDimensions();
        this.entityData.set(IS_KNOT, bl);
    }


    public boolean isWaxed() {
        return this.entityData.get(IS_WAXED);
    }
    public void setWaxed(boolean bl) {
        this.entityData.set(IS_WAXED, bl);
    }

    public boolean isBeingPlaced() {
        return this.entityData.get(IS_BEING_PLACED);
    }
    public void setIsBeingPlaced(boolean bl) {
        this.entityData.set(IS_BEING_PLACED, bl);
    }

    public int getLooseness() {
        return this.entityData.get(LOOSENESS);
    }
    public void setLooseness(int value) {
        this.entityData.set(LOOSENESS, value);
    }
    public void cycleLooseness() {
        this.setLooseness((this.getLooseness() + 1) % 4);
        this.playSound(SpeciesSoundEvents.COIL_ADJUST.get(), 1, 1 - ((float) this.getLooseness()/8));
        if (getEndPoint() != null) getEndPoint().setLooseness(this.getLooseness());
    }

    @Nullable
    public BlockPos getEndPointPos() {
        return this.entityData.get(END_POINT_POS).orElse(null);
    }
    public void setEndPointPos(@Nullable BlockPos pos) {
        this.entityData.set(END_POINT_POS, Optional.ofNullable(pos));
    }

    @Nullable
    public Coil getEndPoint() {
        UUID uuid = this.getEndPointUUID();
        if (uuid == null) return null;

        BlockPos pos = this.getEndPointPos();
        if (pos != null) {
            List<Coil> coils = this.level().getEntitiesOfClass(Coil.class, new AABB(pos),
                    coil -> uuid.equals(coil.getUUID()));
            if (!coils.isEmpty()) return coils.get(0);
        }

        if (!level().isClientSide && level() instanceof ServerLevel serverLevel) {
            Entity entity = serverLevel.getEntity(uuid);
            if (entity instanceof Coil coil) return coil;
        }

        return null;
    }


    private void destroy(@Nullable Player player) {
        if (!this.isStartPoint() && (player == null || !player.isCreative())) this.spawnAtLocation(SpeciesItems.COIL.get().getDefaultInstance());

        if (!this.level().isClientSide && this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(new net.minecraft.core.particles.ItemParticleOption(net.minecraft.core.particles.ParticleTypes.ITEM, SpeciesItems.COIL.get().getDefaultInstance()), this.getX(), this.getY() + 0.5, this.getZ(), 5, 0.2, 0.2, 0.2, 0.05);
        }

        this.playSound(SpeciesSoundEvents.COIL_REMOVE.get());
        if (!this.isStartPoint() && this.getEndPoint() != null) this.getEndPoint().destroy(player);
        this.remove(RemovalReason.KILLED);
    }

    @Override
    public @Nullable ItemStack getPickResult() {
        return SpeciesItems.COIL.get().getDefaultInstance();
    }

    @Override
    public boolean isPickable() {
        return true;
    }
}
