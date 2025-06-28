package com.ninni.species.server.block;

import com.ninni.species.Species;
import com.ninni.species.client.screen.ScreenShakeEvent;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.block.entity.SpectraliburBlockEntity;
import com.ninni.species.server.entity.mob.update_3.Spectre;
import com.ninni.species.registry.SpeciesBlockEntities;
import com.ninni.species.registry.SpeciesBlocks;
import com.ninni.species.registry.SpeciesParticles;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.Difficulty;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;

import static net.minecraft.world.entity.EntitySelector.NO_SPECTATORS;

public class SpectraliburBlock extends BaseEntityBlock {
    protected static final VoxelShape SHAPE = Block.box(1, 0, 4, 15, 16, 12);

    public record Wave(Spectre.Type spectreType, int baseCount, boolean scaleWithPlayers) {}

    private static final Map<Integer, List<Wave>> WAVES = Map.of(
            0, List.of(new Wave(Spectre.Type.SPECTRE, 1, false)),
            1, List.of(new Wave(Spectre.Type.SPECTRE, 1, true)),
            2, List.of(new Wave(Spectre.Type.JOUSTING_SPECTRE, 1, false)),
            3, List.of(
                    new Wave(Spectre.Type.JOUSTING_SPECTRE, 0, true),
                    new Wave(Spectre.Type.SPECTRE, 1, false)
            ),
            4, List.of(
                    new Wave(Spectre.Type.SPECTRE, 1, true),
                    new Wave(Spectre.Type.JOUSTING_SPECTRE, 0, true)
            ),
            5, List.of(new Wave(Spectre.Type.HULKING_SPECTRE, 1, false)),
            6, List.of(
                    new Wave(Spectre.Type.HULKING_SPECTRE, 0, true),
                    new Wave(Spectre.Type.SPECTRE, 1, true)
            ),
            7, List.of(
                    new Wave(Spectre.Type.SPECTRE, 1, false),
                    new Wave(Spectre.Type.JOUSTING_SPECTRE, 1, true),
                    new Wave(Spectre.Type.HULKING_SPECTRE, 0, true)
            ),
            8, List.of(
                    new Wave(Spectre.Type.SPECTRE, 0, true),
                    new Wave(Spectre.Type.JOUSTING_SPECTRE, 1, true)
            ),
            9, List.of(
                    new Wave(Spectre.Type.JOUSTING_SPECTRE, 1, true),
                    new Wave(Spectre.Type.HULKING_SPECTRE, 1, true)
            )
    );


    public SpectraliburBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new SpectraliburBlockEntity(pos, state);
    }

    public RenderShape getRenderShape(BlockState p_49232_) {
        return RenderShape.INVISIBLE;
    }

    public VoxelShape getShape(BlockState p_56331_, BlockGetter p_56332_, BlockPos p_56333_, CollisionContext p_56334_) {
        return SHAPE;
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
        return new ItemStack(SpeciesBlocks.SPECTRALIBUR_PEDESTAL.get());
    }

    @Override
    public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult blockHitResult) {
        BlockEntity blockEntity = world.getBlockEntity(pos);

        if (blockEntity instanceof SpectraliburBlockEntity sword && !sword.isOnCooldown() ) {

            if (!player.hasEffect(MobEffects.DAMAGE_BOOST) && sword.swordPosition == 0) {
                if (world instanceof ServerLevel serverLevel) serverLevel.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SpeciesSoundEvents.SPECTRALIBUR_CANT_PULL.get(), SoundSource.BLOCKS, 2.0F, 1);
                player.displayClientMessage(Component.translatable("item.species.spectralibur.no_strength").withStyle(Style.EMPTY.withColor(0x7CF2F5)), true);
            } else  {
                if (world instanceof ServerLevel serverLevel) {
                    if (sword.getSwordPosition() < 0.9) serverLevel.sendParticles(SpeciesParticles.SPECTRALIBUR.get(), pos.getX() + 0.5F,pos.getY() + 0.01F, pos.getZ() + 0.5F, 1,0, 0, 0, 0);
                    serverLevel.playSound(null, pos.getX(), pos.getY(), pos.getZ(), SpeciesSoundEvents.SPECTRALIBUR_PULL.get(), SoundSource.BLOCKS, 2.0F, 0.75F + sword.getSwordPosition() * 0.5F);
                    float position = sword.getSwordPosition() * 10;
                    List<Player> list = world.getEntitiesOfClass(Player.class, new AABB(pos).inflate(12), NO_SPECTATORS);
                    int playerCount = list.size();

                    if (position > 9) {
                        if (world.getDifficulty() == Difficulty.EASY) world.getEntitiesOfClass(Spectre.class, new AABB(pos).inflate(12)).forEach(LivingEntity::kill);
                        if (world.getDifficulty() == Difficulty.PEACEFUL) player.displayClientMessage(Component.translatable("item.species.spectralibur.peaceful").withStyle(Style.EMPTY.withColor(0x7CF2F5)), true);
                        Species.PROXY.screenShake(new ScreenShakeEvent(pos.getCenter(), 14, 1F, 10, false));
                    }
                    if (position <= 9) {
                        int wave = Math.min((int) position, 9);
                        for (Wave w : WAVES.getOrDefault(wave, List.of())) {
                            int totalSpectres = w.baseCount() + (w.scaleWithPlayers() ? playerCount : 0);
                            for (int i = 0; i < totalSpectres; i++) {
                                Spectre.spawnSpectre(serverLevel, player, pos.above(), w.spectreType(), false);
                            }
                        }
                    }
                }
            }

            sword.onHit(player);

            return InteractionResult.SUCCESS;
        }

        return super.use(state, world, pos, player, hand, blockHitResult);
    }


    public boolean canSurvive(BlockState state, LevelReader levelReader, BlockPos pos) {
        return levelReader.getBlockState(pos.below()).is(SpeciesBlocks.SPECTRALIBUR_PEDESTAL.get());
    }

    public BlockState updateShape(BlockState p_153483_, Direction p_153484_, BlockState p_153485_, LevelAccessor p_153486_, BlockPos p_153487_, BlockPos p_153488_) {
        return !p_153483_.canSurvive(p_153486_, p_153487_) ? Blocks.AIR.defaultBlockState() : super.updateShape(p_153483_, p_153484_, p_153485_, p_153486_, p_153487_, p_153488_);
    }


    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, SpeciesBlockEntities.SPECTRALIBUR.get(), SpectraliburBlockEntity::tick);
    }
}