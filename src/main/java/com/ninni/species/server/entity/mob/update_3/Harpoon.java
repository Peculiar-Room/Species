package com.ninni.species.server.entity.mob.update_3;

import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.entity.util.PlayerAccess;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.entity.IEntityAdditionalSpawnData;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class Harpoon extends Projectile implements IEntityAdditionalSpawnData {
    public static final EntityDataAccessor<Boolean> ANCHORED = SynchedEntityData.defineId(Harpoon.class, EntityDataSerializers.BOOLEAN);
    public static final EntityDataAccessor<Optional<BlockPos>> ANCHOR_POS = SynchedEntityData.defineId(Harpoon.class, EntityDataSerializers.OPTIONAL_BLOCK_POS);
    private float swingInputX = 0;
    private float swingInputY = 0;
    private float swingInputZ = 0;
    private double targetLength = 5.0;
    private double releaseFactor = 1.5;
    public Vec3 ownerPosition = new Vec3(0, 0, 0);
    public Vec3 ownerPositionO = new Vec3(0, 0, 0);
    private boolean isZiplining;
    private Vec3 ziplineDirection;
    private Coil currentCoil;
    private double progressAlongCoil = 0.0;
    private int lastPullSoundTick = 0;

    public Harpoon(EntityType<? extends Projectile> entityType, Level level) {
        super(entityType, level);
    }

    public Harpoon(Level world, Player owner) {
        super(SpeciesEntities.HARPOON.get(), world);
        this.setOwner(owner);
        this.setPos(owner.getX(), owner.getY() + owner.getEyeHeight(), owner.getZ());
        this.targetLength = 5.0;
    }

    @Override
    public void tick() {
        super.tick();
        if (this.getOwner() instanceof Player player) {
            lastPullSoundTick++;
            updateOwnerPosition();
            boolean hasBlockAbove = checkBlockAbove();
            boolean canHoldKnot = checkCanHoldKnot();
            List<Coil> coils = this.level().getEntitiesOfClass(Coil.class, new AABB(this.blockPosition().above()).inflate(0.5), coil -> coil.isStartPoint() && !coil.isBeingPlaced());

            if (!isZiplining && hasBlockAbove && canHoldKnot && !coils.isEmpty() && this.distanceTo(player) <= 12 && !this.isAnchored()) {
                startZiplining(coils);
            }

            if (player.isUsingItem() && player.getUseItem().is(SpeciesItems.HARPOON.get())) {
                if (handleZiplining(player)) return;
                if (handleAnchoring(player, hasBlockAbove)) return;
                handleSwingMovement(player, hasBlockAbove);
            } else {
                handleRelease(player);
            }

            player.hurtMarked = true;
        }
    }

    private void updateOwnerPosition() {
        this.ownerPositionO = this.ownerPosition;
        this.ownerPosition = this.getOwner().position();
    }

    private boolean checkBlockAbove() {
        boolean result = false;
        BlockPos[] positions = new BlockPos[]{
                BlockPos.containing(this.getBoundingBox().getCenter().add(getBbWidth()/2,getBbHeight() + 0.2,0)),
                BlockPos.containing(this.getBoundingBox().getCenter().add(0,getBbHeight() + 0.2,getBbWidth()/2)),
                BlockPos.containing(this.getBoundingBox().getCenter().add(-getBbWidth()/2,getBbHeight() + 0.2,0)),
                BlockPos.containing(this.getBoundingBox().getCenter().add(0,getBbHeight() + 0.2,-getBbWidth()/2))
        };

        for (BlockPos pos : positions) {
            if (!this.level().getBlockState(pos).getCollisionShape(this.level(), pos).isEmpty()) {
                result = true;
                break;
            }
        }
        return result;
    }

    private boolean checkCanHoldKnot() {
        BlockState state = this.level().getBlockState(this.blockPosition().above());
        Block block = state.getBlock();
        return block instanceof FenceBlock
                || (block instanceof LightningRodBlock && state.getValue(LightningRodBlock.FACING).getAxis().isVertical())
                || (block instanceof EndRodBlock && state.getValue(EndRodBlock.FACING).getAxis().isVertical())
                || (block instanceof ChainBlock && state.getValue(ChainBlock.AXIS).isVertical());
    }

    private boolean handleZiplining(Player player) {
        if (isZiplining && currentCoil != null) {
            Coil endPoint = currentCoil.getEndPoint();
            if (endPoint == null) {
                stopZiplining();
                return true;
            }
            if (this.distanceTo(player) > 15) {
                removeHook(player, 100);
            }

            double slopeBonus = ziplineDirection.y * -0.5;
            double ziplineSpeed = 0.5;
            double distance = currentCoil.position().distanceTo(endPoint.position());
            double sag = distance * 0.1 * currentCoil.getLooseness();

            Vec3 start = currentCoil.position();
            Vec3 end = endPoint.position();
            Vec3 middle = start.add(end).scale(0.5).add(0, -sag, 0);

            progressAlongCoil += (ziplineSpeed + slopeBonus) / distance;
            progressAlongCoil = Mth.clamp(progressAlongCoil, 0.0, 1.0);

            Vec3 nextPos = quadraticBezier(start, middle, end, progressAlongCoil);

            if (this.lastPullSoundTick >= 20 + this.random.nextInt(10)) {
                this.lastPullSoundTick = 0;
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.HARPOON_ZIPLINING.get(), player.getSoundSource(), 1.0F, 0.5F + ((float)progressAlongCoil));
            }

            if (progressAlongCoil >= 1.0) {
                List<Coil> nearbyCoils = this.level().getEntitiesOfClass(Coil.class,
                        new AABB(BlockPos.containing(end)).inflate(0.5), Coil::isStartPoint);

                if (!nearbyCoils.isEmpty()) {
                    currentCoil = nearbyCoils.get(0);
                    ziplineDirection = endPoint.position().subtract(currentCoil.position()).normalize();
                    progressAlongCoil = 0.0;
                    this.setPos(currentCoil.position());
                } else {
                    stopZiplining();
                }
            } else {
                this.setPos(nextPos.add(0,0.15,0));
                Vec3 desiredPos = this.position().add(0, -targetLength, 0);
                Vec3 correction = desiredPos.subtract(player.position()).scale(0.1);
                Vec3 dampedVel = player.getDeltaMovement().scale(0.7);
                player.setDeltaMovement(dampedVel.add(correction));
            }
            return true;
        }
        return false;
    }

    private Vec3 quadraticBezier(Vec3 p0, Vec3 p1, Vec3 p2, double t) {
        double it = 1 - t;
        return p0.scale(it * it).add(p1.scale(2 * it * t)).add(p2.scale(t * t));
    }

    private boolean handleAnchoring(Player player, boolean hasBlockAbove) {
        if (!isAnchored() && !this.isZiplining) {
            if (hasBlockAbove) {
                this.setAnchorPos(this.blockPosition().above());
                this.setAnchored(true);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.HARPOON_ANCHOR.get(), SoundSource.PLAYERS, 2F, 1.0F);
                this.level().playSound(null, this.getX(), this.getY(), this.getZ(), level().getBlockState(this.getAnchorPos()).getSoundType().getBreakSound(), SoundSource.BLOCKS, 2.25F, 1.0F);
            }

            if (this.onGround()) {
                if (player instanceof PlayerAccess playerAccess) playerAccess.setHarpoonId(-1);
                removeHook(player, 20);
            }

            Vec3 motion = this.getDeltaMovement().add(0, -0.05, 0);
            this.setDeltaMovement(motion);
            this.move(MoverType.SELF, motion);

            return true;
        }
        return false;
    }

    private void handleSwingMovement(Player player, boolean hasBlockAbove) {
        if (!hasBlockAbove) this.setAnchored(false);

        Vec3 anchor = Vec3.atCenterOf(this.getAnchorPos());
        Vec3 playerPos = player.position();
        Vec3 toPlayer = playerPos.subtract(anchor);
        double currentLength = toPlayer.length();
        Vec3 radial = toPlayer.normalize();

        double reelSpeed = 0.1;
        if (swingInputY > 0.0F) targetLength -= reelSpeed;
        else if (swingInputY < 0.0F) targetLength += reelSpeed;
        targetLength = Mth.clamp(targetLength, 2.0, 15.0);

        double distanceError = currentLength - targetLength;
        double dampingFactor = 0.05 + (Math.abs(distanceError) * 0.05);
        double descendingFactor = -0.3;
        if (swingInputY == -1) {
            descendingFactor = -0.5;
            releaseFactor = Math.max(1, releaseFactor - 0.1);
            player.resetFallDistance();

            Vec3 toAnchorXZ = new Vec3(anchor.x - playerPos.x, 0, anchor.z - playerPos.z);
            Vec3 xzPull = toAnchorXZ.normalize().scale(0.05);
            Vec3 currentVel = player.getDeltaMovement();
            player.setDeltaMovement(currentVel.add(xzPull));
        }
        if (swingInputY == 1) releaseFactor = Math.min(3, releaseFactor + 0.2);
        if (player.onGround()) releaseFactor = 1;

        double yMovement = (swingInputY * descendingFactor) - player.getAttributeValue(ForgeMod.ENTITY_GRAVITY.get()) - 0.025;
        double pullStrength = Mth.clamp(-distanceError * dampingFactor, yMovement, 0.2);

        playSwingingSounds(player, pullStrength);

        Vec3 radialCorrection = radial.scale(pullStrength);
        Vec3 playerVel = player.getDeltaMovement();
        Vec3 tangentVel = playerVel.subtract(radial.scale(playerVel.dot(radial)));
        Vec3 forward = player.getLookAngle().normalize();
        Vec3 right = forward.cross(new Vec3(0, 1, 0)).normalize();
        Vec3 swingVec = right.scale(swingInputX).add(forward.scale(swingInputZ)).normalize();
        Vec3 swingTangent = swingVec.subtract(radial.scale(swingVec.dot(radial))).normalize();
        Vec3 swingForce = swingTangent.scale(0.05);

        Vec3 finalVelocity = tangentVel.add(radialCorrection).add(swingForce);
        player.setDeltaMovement(finalVelocity);
    }

    private void playSwingingSounds(Player player, double pullStrength) {
        double speed = player.getDeltaMovement().length();
        double minInterval = 8;
        double maxInterval = 25;
        double speedFactor = Mth.clamp(speed * 3, 0.0, 1.0);
        boolean isPulling = swingInputY >= 0.0F;

        int interval = (int) Mth.lerp(speedFactor, maxInterval, minInterval) + (isPulling ? 0 : 3);

        if (Math.abs(pullStrength) > 0.01 && lastPullSoundTick >= interval && !player.onGround() && player.distanceTo(this) > 2.5) {
            lastPullSoundTick = 0;
            float pitch = getSwingingPitch(player, isPulling);
            this.level().playSound(null, player.getX(), player.getY(), player.getZ(), SpeciesSoundEvents.HARPOON_PULL.get(), player.getSoundSource(), 0.5F, pitch);
        }
    }

    private float getSwingingPitch(Player player, boolean isPulling) {
        double dx = player.getX() - this.getX();
        double dz = player.getZ() - this.getZ();
        double distance = Math.sqrt(dx * dx + dz * dz);
        double minDistance = 0;
        double maxDistance = 14;
        double clamped = Mth.clamp(distance, minDistance, maxDistance);

        float stretchFactor = (float)((clamped - minDistance) / (maxDistance - minDistance));

        float pitchBase = isPulling ? 0.9F : 0.7F;
        float pitchRange = isPulling ? 0.6F : 0.5F;
        float pitch = pitchBase + stretchFactor * pitchRange;
        return pitch;
    }


    private void handleRelease(Player player) {
        if (!isZiplining) player.setDeltaMovement(player.getDeltaMovement().multiply(releaseFactor, 1, releaseFactor).add(0, 0.1 * releaseFactor, 0));
        stopZiplining();
        if (player instanceof PlayerAccess playerAccess) playerAccess.setHarpoonId(-1);
        removeHook(player, 5);
    }

    private void startZiplining(List<Coil> coils) {
        this.isZiplining = true;
        this.progressAlongCoil = 0.0;
        this.currentCoil = coils.get(0);
        Vec3 start = currentCoil.position();
        Vec3 end = currentCoil.getEndPoint().position();
        this.ziplineDirection = end.subtract(start).normalize();

        this.setDeltaMovement(Vec3.ZERO);
        this.setAnchored(false);
        this.setAnchorPos(null);
        this.releaseFactor = 1;
        this.setPos(start);
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.HARPOON_START_ZIPLINING.get(), SoundSource.PLAYERS, 2.0F, 1.0F);
    }

    private void removeHook(Player player, int cooldown) {
        this.level().playSound(null, player.getX(), player.getY(), player.getZ(), SpeciesSoundEvents.HARPOON_RETRIEVED.get(), player.getSoundSource(), 1.0F, 1.0F);
        this.discard();
        player.stopUsingItem();
        player.getCooldowns().addCooldown(SpeciesItems.HARPOON.get(), cooldown);
    }

    private void stopZiplining() {
        isZiplining = false;
        currentCoil = null;
    }

    @Override
    protected void defineSynchedData() {
        this.entityData.define(ANCHOR_POS, Optional.empty());
        this.entityData.define(ANCHORED, false);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setAnchored(tag.getBoolean("IsAnchored"));
        if (tag.contains("AnchorPos")) this.setAnchorPos(NbtUtils.readBlockPos(tag.getCompound("AnchorPos")));
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsAnchored", this.isAnchored());
        if (this.getAnchorPos() != null) tag.put("AnchorPos", NbtUtils.writeBlockPos(this.getAnchorPos()));
    }

    public boolean isAnchored() {
        return this.entityData.get(ANCHORED);
    }
    public void setAnchored(boolean bl) {
        this.entityData.set(ANCHORED, bl);
    }

    @Nullable
    public BlockPos getAnchorPos() {
        return this.entityData.get(ANCHOR_POS).orElse(null);
    }
    public void setAnchorPos(@Nullable BlockPos pos) {
        this.entityData.set(ANCHOR_POS, Optional.ofNullable(pos));
    }

    public void setSwingInput(float x, float y, float z) {
        this.swingInputX = x;
        this.swingInputY = y;
        this.swingInputZ = z;
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void writeSpawnData(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeInt(this.getOwner() == null ? -1 : this.getOwner().getId());
    }

    @Override
    public void readSpawnData(FriendlyByteBuf friendlyByteBuf) {
        int ownerId = friendlyByteBuf.readInt();
        if (ownerId != -1) {
            Entity owner = level().getEntity(ownerId);
            if (owner != null) this.setOwner(owner);
        }
    }
}
