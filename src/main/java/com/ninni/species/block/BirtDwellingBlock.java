package com.ninni.species.block;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.block.*;
import net.minecraft.block.entity.BeehiveBlockEntity;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.TntEntity;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.mob.CreeperEntity;
import net.minecraft.entity.passive.BeeEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.WitherSkullEntity;
import net.minecraft.entity.vehicle.TntMinecartEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecation")
public class BirtDwellingBlock
        extends BlockWithEntity {
    public static final DirectionProperty FACING = HorizontalFacingBlock.FACING;

    public BirtDwellingBlock(AbstractBlock.Settings settings) {
        super(settings);
        this.setDefaultState(((this.stateManager.getDefaultState())).with(FACING, Direction.NORTH));
    }

    @Override
    public void afterBreak(World world, PlayerEntity player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack stack) {
        super.afterBreak(world, player, pos, state, blockEntity, stack);
        if (!world.isClient && blockEntity instanceof BeehiveBlockEntity beehiveBlockEntity) {
            if (EnchantmentHelper.getLevel(Enchantments.SILK_TOUCH, stack) == 0) {
                beehiveBlockEntity.angerBees(player, state, BeehiveBlockEntity.BeeState.EMERGENCY);
                world.updateComparators(pos, this);
                this.angerNearbyBees(world, pos);
            }
            Criteria.BEE_NEST_DESTROYED.trigger((ServerPlayerEntity)player, state, stack, beehiveBlockEntity.getBeeCount());
        }
    }

    private void angerNearbyBees(World world, BlockPos pos) {
        List<BeeEntity> list = world.getNonSpectatingEntities(BeeEntity.class, new Box(pos).expand(8.0, 6.0, 8.0));
        if (!list.isEmpty()) {
            List<PlayerEntity> list2 = world.getNonSpectatingEntities(PlayerEntity.class, new Box(pos).expand(8.0, 6.0, 8.0));
            int i = list2.size();
            for (BeeEntity beeEntity : list) {
                if (beeEntity.getTarget() != null) continue;
                beeEntity.setTarget(list2.get(world.random.nextInt(i)));
            }
        }
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState().with(FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    @Nullable
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new BeehiveBlockEntity(pos, state);
    }

    @Override
    @Nullable
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient ? null : BeehiveBlock.checkType(type, BlockEntityType.BEEHIVE, BeehiveBlockEntity::serverTick);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        BlockEntity blockEntity;
        if (!world.isClient && player.isCreative() && world.getGameRules().getBoolean(GameRules.DO_TILE_DROPS) && (blockEntity = world.getBlockEntity(pos)) instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
            ItemStack itemStack = new ItemStack(this);
            boolean bl = !beehiveBlockEntity.hasNoBees();
            if (bl) {
                NbtCompound nbtCompound;
                nbtCompound = new NbtCompound();
                nbtCompound.put("Bees", beehiveBlockEntity.getBees());
                BlockItem.setBlockEntityNbt(itemStack, BlockEntityType.BEEHIVE, nbtCompound);
                nbtCompound = new NbtCompound();
                itemStack.setSubNbt("BlockStateTag", nbtCompound);
                ItemEntity itemEntity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), itemStack);
                itemEntity.setToDefaultPickupDelay();
                world.spawnEntity(itemEntity);
            }
        }
        super.onBreak(world, pos, state, player);
    }

    @Override
    public List<ItemStack> getDroppedStacks(BlockState state, LootContext.Builder builder) {
        BlockEntity blockEntity;
        Entity entity = builder.getNullable(LootContextParameters.THIS_ENTITY);
        if ((entity instanceof TntEntity || entity instanceof CreeperEntity || entity instanceof WitherSkullEntity || entity instanceof WitherEntity || entity instanceof TntMinecartEntity) && (blockEntity = builder.getNullable(LootContextParameters.BLOCK_ENTITY)) instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
            beehiveBlockEntity.angerBees(null, state, BeehiveBlockEntity.BeeState.EMERGENCY);
        }
        return super.getDroppedStacks(state, builder);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState neighborState, WorldAccess world, BlockPos pos, BlockPos neighborPos) {
        BlockEntity blockEntity;
        if (world.getBlockState(neighborPos).getBlock() instanceof FireBlock && (blockEntity = world.getBlockEntity(pos)) instanceof BeehiveBlockEntity) {
            BeehiveBlockEntity beehiveBlockEntity = (BeehiveBlockEntity)blockEntity;
            beehiveBlockEntity.angerBees(null, state, BeehiveBlockEntity.BeeState.EMERGENCY);
        }
        return super.getStateForNeighborUpdate(state, direction, neighborState, world, pos, neighborPos);
    }
}

