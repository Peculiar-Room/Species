package com.ninni.species.entity;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Dynamic;
import com.ninni.species.block.entity.CruncherPelletBlockEntity;
import com.ninni.species.data.CruncherPelletManager;
import com.ninni.species.registry.SpeciesBlocks;
import com.ninni.species.registry.SpeciesEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.tags.FluidTags;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MoverType;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.DirectionalPlaceContext;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.ConcretePowderBlock;
import net.minecraft.world.level.block.Fallable;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

public class CruncherPellet extends FallingBlockEntity {
    private static final Logger LOGGER = LogUtils.getLogger();
    private CruncherPelletManager.CruncherPelletData pelletData;

    public CruncherPellet(EntityType<? extends FallingBlockEntity> entityType, Level level) {
        super(entityType, level);
    }

    public CruncherPellet(Level level, double d, double e, double f, BlockState blockState, CruncherPelletManager.CruncherPelletData data) {
        this(SpeciesEntities.CRUNCHER_PELLET.get(), level);
        this.blockState = blockState;
        this.blocksBuilding = true;
        this.setPos(d, e, f);
        this.setDeltaMovement(Vec3.ZERO);
        this.xo = d;
        this.yo = e;
        this.zo = f;
        this.setStartPos(this.blockPosition());
        this.pelletData = data;
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        if (compoundTag.contains("PelletData", 10)) {
            CruncherPelletManager.CruncherPelletData.CODEC.parse(
                    new Dynamic<>(NbtOps.INSTANCE, compoundTag.getCompound("PelletData"))
            ).resultOrPartial(LOGGER::error).ifPresent(this::setPelletData);
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        if (this.pelletData != null) {
            CruncherPelletManager.CruncherPelletData.CODEC
                    .encodeStart(NbtOps.INSTANCE, this.getPelletData())
                    .resultOrPartial(LOGGER::error)
                    .ifPresent(tag -> compoundTag.put("PelletData", tag));
        }
    }

    @Override
    public void tick() {
        if (this.getBlockState().isAir()) {
            this.discard();
            return;
        }
        Block block = this.getBlockState().getBlock();
        ++this.time;
        if (!this.isNoGravity()) {
            this.setDeltaMovement(this.getDeltaMovement().add(0.0, -0.04, 0.0));
        }
        this.move(MoverType.SELF, this.getDeltaMovement());
        if (!this.level().isClientSide) {
            BlockHitResult blockHitResult;
            BlockPos blockPos = this.blockPosition();
            boolean bl = this.getBlockState().getBlock() instanceof ConcretePowderBlock;
            boolean bl2 = bl && this.level().getFluidState(blockPos).is(FluidTags.WATER);
            double d = this.getDeltaMovement().lengthSqr();
            if (bl && d > 1.0 && (blockHitResult = this.level().clip(new ClipContext(new Vec3(this.xo, this.yo, this.zo), this.position(), ClipContext.Block.COLLIDER, ClipContext.Fluid.SOURCE_ONLY, this))).getType() != HitResult.Type.MISS && this.level().getFluidState(blockHitResult.getBlockPos()).is(FluidTags.WATER)) {
                blockPos = blockHitResult.getBlockPos();
                bl2 = true;
            }
            if (this.onGround() || bl2) {
                BlockState blockState = this.level().getBlockState(blockPos);
                this.setDeltaMovement(this.getDeltaMovement().multiply(0.7, -0.5, 0.7));
                if (!blockState.is(Blocks.MOVING_PISTON)) {
                    boolean bl3 = blockState.canBeReplaced(new DirectionalPlaceContext(this.level(), blockPos, Direction.DOWN, ItemStack.EMPTY, Direction.UP));
                    boolean bl4 = FallingBlock.isFree(this.level().getBlockState(blockPos.below())) && (!bl || !bl2);
                    boolean bl5 = this.getBlockState().canSurvive(this.level(), blockPos) && !bl4;
                    if (bl3 && bl5) {
                        if (this.getBlockState().hasProperty(BlockStateProperties.WATERLOGGED) && this.level().getFluidState(blockPos).getType() == Fluids.WATER) {
                            this.blockState = this.getBlockState().setValue(BlockStateProperties.WATERLOGGED, true);
                        }
                        if (this.level().setBlock(blockPos, this.getBlockState(), 3)) {
                            this.playSound(SoundEvents.SCULK_BLOCK_PLACE);
                            if (this.level().getBlockEntity(blockPos) instanceof CruncherPelletBlockEntity cruncherPelletBlock) {
                                cruncherPelletBlock.setPelletData(this.pelletData);
                            }
                            BlockEntity blockEntity;
                            ((ServerLevel)this.level()).getChunkSource().chunkMap.broadcast(this, new ClientboundBlockUpdatePacket(blockPos, this.level().getBlockState(blockPos)));
                            this.discard();
                            if (block instanceof Fallable fallable) {
                                fallable.onLand(this.level(), blockPos, this.getBlockState(), blockState, this);
                            }
                            if (this.blockData != null && this.getBlockState().hasBlockEntity() && (blockEntity = this.level().getBlockEntity(blockPos)) != null) {
                                CompoundTag compoundTag = blockEntity.saveWithoutMetadata();
                                for (String string : this.blockData.getAllKeys()) {
                                    compoundTag.put(string, this.blockData.get(string).copy());
                                }
                                try {
                                    blockEntity.load(compoundTag);
                                }
                                catch (Exception exception) {
                                    LOGGER.error("Failed to load block entity from falling block", exception);
                                }
                                blockEntity.setChanged();
                            }
                        } else if (this.dropItem && this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
                            this.discard();
                            this.level().setBlock(blockPos, this.getBlockState(), 3);
                            this.playSound(SoundEvents.SCULK_BLOCK_PLACE);
                            if (this.level().getBlockEntity(blockPos) instanceof CruncherPelletBlockEntity cruncherPelletBlock) {
                                cruncherPelletBlock.setPelletData(this.pelletData);
                            }
                        }
                    } else {
                        this.discard();
                        this.level().setBlock(blockPos, this.getBlockState(), 3);
                        this.playSound(SoundEvents.SCULK_BLOCK_PLACE);
                        if (this.level().getBlockEntity(blockPos) instanceof CruncherPelletBlockEntity cruncherPelletBlock) {
                            cruncherPelletBlock.setPelletData(this.pelletData);
                        }
                    }
                }
            } else if (!(this.level().isClientSide || (this.time <= 100 || blockPos.getY() > this.level().getMinBuildHeight() && blockPos.getY() <= this.level().getMaxBuildHeight()) && this.time <= 600)) {
                if (this.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, SpeciesBlocks.CRUNCHER_PELLET.get().defaultBlockState()), this.getX(), this.getY(), this.getZ(), 3, 0.0, 0.0, 0.0, 1.0F);
                }
                this.discard();
            }
        }
        this.setDeltaMovement(this.getDeltaMovement().scale(0.98));
    }

    @Nullable
    public CruncherPelletManager.CruncherPelletData getPelletData() {
        return this.pelletData;
    }

    public void setPelletData(CruncherPelletManager.CruncherPelletData data) {
        this.pelletData = data;
    }

}