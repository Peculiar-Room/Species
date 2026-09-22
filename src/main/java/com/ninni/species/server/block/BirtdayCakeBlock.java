package com.ninni.species.server.block;

import com.mojang.serialization.MapCodec;
import com.ninni.species.client.inventory.BirtdayCakeMenu;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.block.entity.BirtdayCakeBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.VibrationParticleOption;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.neoforged.neoforge.common.extensions.IPlayerExtension;
import net.neoforged.neoforge.network.registration.NetworkChannel;
import org.jetbrains.annotations.Nullable;

public class BirtdayCakeBlock extends BaseEntityBlock {
    public static final MapCodec<BirtdayCakeBlock> CODEC = simpleCodec(BirtdayCakeBlock::new);
    private static final VoxelShape SHAPE = Shapes.join(Block.box(7, 6, 7, 9, 10, 9), Block.box(2, 0, 2, 14, 6, 14), BooleanOp.OR);
    public static final BooleanProperty LIT = BlockStateProperties.LIT;

    public BirtdayCakeBlock(Properties properties) {
        super(properties);
        this.registerDefaultState((this.stateDefinition.any()).setValue(LIT, false));
    }

    @Override
    protected MapCodec<? extends BaseEntityBlock> codec() {
        return CODEC;
    }

    @Override
    public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        if (level.isClientSide) return ItemInteractionResult.SUCCESS;

        if (!state.getValue(LIT)) {
            if (player.getItemInHand(hand).is(Items.FLINT_AND_STEEL)) {
                level.setBlock(pos, state.setValue(LIT, true), 3);
                level.playSound(null, pos, SpeciesSoundEvents.HAPPY_BIRTDAY.get(), SoundSource.RECORDS, 3, 1);

                if (level instanceof ServerLevel serverLevel) {
                    BlockPositionSource positionSource = new BlockPositionSource(new BlockPos(pos.getX(), pos.getY() + 5, pos.getZ()));
                    serverLevel.sendParticles(new VibrationParticleOption(positionSource, 20), pos.getX() + 0.5, pos.getY(), pos.getZ() + 0.5, 0, 0, 0, 0, 0);
                }

                if (level.getBlockEntity(pos) instanceof BirtdayCakeBlockEntity blockEntity) {
                    for (Player p : level.players()) {
                        p.displayClientMessage(Component.translatable("messages.species.birtday", blockEntity.getPlayerName(), blockEntity.getAge()).withStyle(Style.EMPTY.withColor(0x4dd1e1)), false);
                    }
                }

            } else if (player instanceof IPlayerExtension playerExtension){
                playerExtension.openMenu(new MenuProvider() {
                    @Override
                    public Component getDisplayName() {
                        return Component.translatable("container.species.birtday_cake");
                    }

                    @Override
                    public AbstractContainerMenu createMenu(int id, Inventory inv, Player player) {
                        System.out.println("Creating menu");
                        return new BirtdayCakeMenu(id, inv, pos);
                    }
                }, pos);
            }
            return ItemInteractionResult.SUCCESS;
        }

        level.playSound(null, pos, SpeciesSoundEvents.BLOCK_BIRTDAY_CAKE_BLOW.get(), SoundSource.BLOCKS, 1, 1);
        level.setBlock(pos, state.setValue(LIT, false), 3);
        level.scheduleTick(pos, this, 80);
        return ItemInteractionResult.SUCCESS;
    }


    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource randomSource) {
        level.destroyBlock(pos, true, null);
        super.tick(state, level, pos, randomSource);
    }

    @Override
    public VoxelShape getShape(BlockState blockState, BlockGetter blockGetter, BlockPos blockPos, CollisionContext collisionContext) {
        return SHAPE;
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        builder.add(LIT);
    }


    public RenderShape getRenderShape(BlockState p_48727_) {
        return RenderShape.MODEL;
    }

    @Override
    public void setPlacedBy(Level level, BlockPos blockPos, BlockState blockState, LivingEntity livingEntity, ItemStack itemStack) {
        if (level.getBlockEntity(blockPos) instanceof BirtdayCakeBlockEntity blockEntity && (!itemStack.has(DataComponents.BLOCK_ENTITY_DATA) || ! itemStack.getOrDefault(DataComponents.BLOCK_ENTITY_DATA, CustomData.EMPTY).contains("PlayerName"))) {
            blockEntity.setPlayerName(livingEntity.getScoreboardName());
        }
    }

    @Override
    public @Nullable BlockEntity newBlockEntity(BlockPos p_153215_, BlockState p_153216_) {
        return new BirtdayCakeBlockEntity(p_153215_, p_153216_);
    }
}
