package com.ninni.species.entity;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.ninni.species.registry.SpeciesBlocks;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.control.MoveControl;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.PanicGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.gameevent.GameEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Mammutilation extends PathfinderMob {
    private static final Map<Block, SoundEvent> SOUNDS_BY_EGG = Util.make(Maps.newHashMap(), map -> {
        map.put(Blocks.TURTLE_EGG, SoundEvents.TURTLE_EGG_CRACK);
        map.put(Blocks.SNIFFER_EGG, SoundEvents.SNIFFER_EGG_CRACK);
        map.put(SpeciesBlocks.PETRIFIED_EGG, SpeciesSoundEvents.PETRIFIED_EGG_CRACK);
        map.put(SpeciesBlocks.SPRINGLING_EGG, SpeciesSoundEvents.PETRIFIED_EGG_CRACK);
        map.put(SpeciesBlocks.CRUNCHER_EGG, SpeciesSoundEvents.PETRIFIED_EGG_CRACK);
    });
    private int hatchCooldown;
    private int howlCooldown;

    public Mammutilation(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
        this.moveControl = new MoveControl(this);
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.hatchCooldown = compoundTag.getInt("HatchCooldown");
        this.howlCooldown = compoundTag.getInt("HowlCooldown");
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putInt("HatchCooldown", this.hatchCooldown);
        compoundTag.putInt("HowlCooldown", this.howlCooldown);
    }

    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(1, new PanicGoal(this, 1.75D));
        this.goalSelector.addGoal(2, new HowAtMoonGoal(this));
        this.goalSelector.addGoal(3, new WaterAvoidingRandomStrollGoal(this, 1.0));
        this.goalSelector.addGoal(4, new LookAtPlayerGoal(this, Player.class, 6.0f));
        this.goalSelector.addGoal(5, new RandomLookAroundGoal(this));
        //this.goalSelector.addGoal(5, new SitAndFollowMoonGoal(this));
        //this.goalSelector.addGoal(5, new RiseGoal(this));
        //this.goalSelector.addGoal(3, new TeleportIfCullingGoal(this));
        this.targetSelector.addGoal(0, new NearestAttackableTargetGoal<>(this, Player.class, true));
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes().add(Attributes.MAX_HEALTH, 20.0).add(Attributes.MOVEMENT_SPEED, 0.15).add(Attributes.KNOCKBACK_RESISTANCE, 0.6).add(Attributes.ATTACK_DAMAGE, 0.0);
    }

    @Override
    protected InteractionResult mobInteract(Player player, InteractionHand interactionHand) {
        ItemStack stack = player.getItemInHand(interactionHand);
        ItemStack mutatedJellyBottle = new ItemStack(SpeciesItems.MUTATED_JELLY_BOTTLE);
        if (stack.is(Items.GLASS_BOTTLE)) {
            stack.shrink(1);
            this.level().playSound(player, player.getX(), player.getY(), player.getZ(), SoundEvents.BOTTLE_FILL, SoundSource.BLOCKS, 1.0f, 1.0f);
            if (stack.isEmpty()) {
                player.setItemInHand(interactionHand, mutatedJellyBottle);
            } else if (!player.getInventory().add(mutatedJellyBottle)) {
                player.drop(mutatedJellyBottle, false);
            }
            this.level().gameEvent(player, GameEvent.FLUID_PICKUP, this.blockPosition());
            return InteractionResult.sidedSuccess(this.level().isClientSide);
        }
        return super.mobInteract(player, interactionHand);
    }

    @Override
    public void aiStep() {
        super.aiStep();
        if (!this.level().isClientSide) {
            if (this.hatchCooldown > 0) {
                this.hatchCooldown--;
            }
            if (this.howlCooldown > 0) {
                this.howlCooldown--;
            }
            if (this.hatchCooldown == 0) {
                this.getAllEggPositions().stream().filter(blockPos -> this.level().getBlockState(blockPos).hasProperty(BlockStateProperties.HATCH)).forEach(this::handleEggHatching);
            }
        }
    }

    private void handleEggHatching(BlockPos blockPos) {
        BlockState blockState = this.level().getBlockState(blockPos);
        int hatch = blockState.getValue(BlockStateProperties.HATCH);
        this.level().setBlock(blockPos, blockState.setValue(BlockStateProperties.HATCH, hatch + 1), 2);
        this.level().playSound(null, blockPos, SOUNDS_BY_EGG.get(blockState.getBlock()), SoundSource.BLOCKS, 0.7f, 0.9f + this.getRandom().nextFloat() * 0.2f);
        this.level().levelEvent(3009, blockPos, 0);
        this.hatchCooldown = UniformInt.of(6000, 12000).sample(this.getRandom());
    }

    public List<BlockPos> getAllEggPositions() {
        List<BlockPos> poses = Lists.newArrayList();
        int radius = 8;
        int yRange = radius / 2;
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -yRange; y <= yRange; y++) {
                    BlockPos blockPos = BlockPos.containing(this.getX() + x, this.getY() + y, this.getZ() + z);
                    BlockState blockState = this.level().getBlockState(blockPos);
                    if (blockState.hasProperty(BlockStateProperties.HATCH) && blockState.getValue(BlockStateProperties.HATCH) < 2) {
                        poses.add(blockPos);
                        this.handleTallEggs(blockState, blockPos, poses);
                    }
                }
            }
        }
        return poses;
    }

    private void handleTallEggs(BlockState blockState, BlockPos blockPos, List<BlockPos> poses) {
        boolean isDoubleBlockTall = blockState.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF);
        Arrays.stream(Direction.values()).filter(direction -> direction.getAxis() == Direction.Axis.Y).forEach(direction -> {
            BlockState relativeState = this.level().getBlockState(blockPos.relative(direction));
            DoubleBlockHalf doubleBlockHalf = direction == Direction.DOWN ? DoubleBlockHalf.LOWER : DoubleBlockHalf.UPPER;
            boolean hasDoubleBlockInRelativeState = relativeState.is(blockState.getBlock()) && relativeState.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == doubleBlockHalf && isDoubleBlockTall;
            if (hasDoubleBlockInRelativeState) {
                poses.add(blockPos.relative(direction));
            }
        });
    }

    @Nullable
    @Override
    public SpawnGroupData finalizeSpawn(ServerLevelAccessor serverLevelAccessor, DifficultyInstance difficultyInstance, MobSpawnType mobSpawnType, @Nullable SpawnGroupData spawnGroupData, @Nullable CompoundTag compoundTag) {
        this.hatchCooldown = UniformInt.of(6000, 12000).sample(this.getRandom());
        return super.finalizeSpawn(serverLevelAccessor, difficultyInstance, mobSpawnType, spawnGroupData, compoundTag);
    }

    @Nullable
    @Override
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.MAMMUTILATION_DEATH;
    }

    @Nullable
    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SpeciesSoundEvents.MAMMUTILATION_HURT;
    }

    @Nullable
    @Override
    protected SoundEvent getAmbientSound() {
        return SpeciesSoundEvents.MAMMUTILATION_IDLE;
    }

    @SuppressWarnings("unused")
    public static boolean canSpawn(EntityType<Mammutilation> entity, ServerLevelAccessor world, MobSpawnType spawnReason, BlockPos pos, RandomSource random) {
        return false;
    }

    public static class HowAtMoonGoal extends Goal{
        protected final Mammutilation mammutilation;

        public HowAtMoonGoal(Mammutilation mammutilation) {
            this.mammutilation = mammutilation;
        }

        @Override
        public void start() {
            this.mammutilation.playSound(SoundEvents.WOLF_HOWL, 1.0F, 0.1F);
            this.mammutilation.howlCooldown = 1000;
        }

        @Override
        public boolean canUse() {
            return this.mammutilation.level().isNight() && this.mammutilation.howlCooldown == 0;
        }
    }
}
