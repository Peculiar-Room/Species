package com.ninni.species.server.block.entity;

import com.google.common.collect.Lists;
import com.ninni.species.server.block.BirtDwellingBlock;
import com.ninni.species.registry.SpeciesBlockEntities;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.gameevent.BlockPositionSource;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.GameEventListener;
import net.minecraft.world.level.gameevent.PositionSource;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.ninni.species.server.block.BirtDwellingBlock.BIRTS;
import static com.ninni.species.server.block.BirtDwellingBlock.EGGS;

public class BirtDwellingBlockEntity extends BlockEntity implements GameEventListener.Holder<BirtDwellingBlockEntity.BirtDwellingListener>  {
    public static final String MIN_OCCUPATION_TICKS_KEY = "MinOccupationTicks";
    public static final String ENTITY_DATA_KEY = "EntityData";
    public static final String TICKS_IN_DWELLING_KEY = "TicksInDwelling";
    public static final String BIRTS_KEY = "Birts";
    private static final List<String> IRRELEVANT_BIRT_NBT_KEYS = Arrays.asList("Air", "Bees", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance", "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "CannotEnterDwellingTicks", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "DwellingPos", "HivePos", "Passengers", "Leash", "UUID");
    private final List<Birt> birts = Lists.newArrayList();
    private int day = -1;
    private final BirtDwellingListener birtDwellingListener;
    private int pacifyTicks = 0;

    public BirtDwellingBlockEntity(BlockPos pos, BlockState state) {
        super(SpeciesBlockEntities.BIRT_DWELLING.get(), pos, state);
        this.birtDwellingListener = new BirtDwellingListener(state, new BlockPositionSource(pos));
    }

    public boolean hasNoBirts() {
        return this.birts.isEmpty();
    }

    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean isFullOfBirts() {
        return this.birts.size() == 3;
    }

    public void angerBirts(@Nullable Player player, BlockState state, BirtState birtState) {
        if (this.pacifyTicks > 0) return;
        List<Entity> list = this.tryReleaseBirt(state, birtState);
        if (player != null) {
            for (Entity entity : list) {
                if (!(entity instanceof com.ninni.species.server.entity.mob.update_1.Birt birt)) continue;
                if (!(player.position().distanceToSqr(entity.position()) <= 16.0)) continue;
                birt.setTarget(player);
                birt.setCannotEnterDwellingTicks(400);
            }
        }
    }

    public static void tickLayEgg(BirtDwellingBlockEntity birtDwellingBlockEntity, Level world, BlockPos blockPos, BlockState state) {
        long day = world.getDayTime() / 24000L;
        if (birtDwellingBlockEntity.day == -1 || day != birtDwellingBlockEntity.day && day == 0) {
            birtDwellingBlockEntity.day = (int) day;
        }
        if (state.getValue(BIRTS) > 0 && birtDwellingBlockEntity.day < day) {
            birtDwellingBlockEntity.day++;
            world.setBlockAndUpdate(blockPos, state.setValue(EGGS, Math.min(5, state.getValue(EGGS) + state.getValue(BIRTS))));
        }
    }

    private List<Entity> tryReleaseBirt(BlockState state, BirtState birtState) {
        ArrayList<Entity> list = Lists.newArrayList();
        this.birts.removeIf(birt -> {
            assert this.level != null;
            return BirtDwellingBlockEntity.releaseBirt(this.level, this.worldPosition, state, birt, list, birtState);
        });
        if (!list.isEmpty()) {
            super.setChanged();
        }
        return list;
    }

    public void tryEnterDwelling(Entity entity) {
        this.tryEnterDwelling(entity, 0);
    }

    public void tryEnterDwelling(Entity entity, int ticksInDwelling) {
        if (this.birts.size() >= 3) {
            return;
        }
        entity.stopRiding();
        entity.ejectPassengers();
        CompoundTag nbtCompound = new CompoundTag();
        entity.save(nbtCompound);
        BlockPos blockPos = this.getBlockPos();
        this.addBirt(nbtCompound, ticksInDwelling);
        if (this.level != null) {
            this.level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SpeciesSoundEvents.BLOCK_BIRT_DWELLING_ENTER.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(entity, this.getBlockState()));
        }
        entity.discard();
        super.setChanged();
    }

    public void addBirt(CompoundTag nbtCompound, int ticksInDwelling) {
        assert this.level != null;
        this.birts.add(new Birt(nbtCompound, ticksInDwelling, 1200));
    }

    private static boolean releaseBirt(Level world, BlockPos pos, BlockState state, Birt birt, @Nullable List<Entity> entities, BirtState birtState) {
        if (world.isDay() && birtState != BirtState.EMERGENCY) {
            return false;
        }
        CompoundTag nbtCompound = birt.entityData.copy();
        BirtDwellingBlockEntity.removeIrrelevantNbtKeys(nbtCompound);
        nbtCompound.put("DwellingPos", NbtUtils.writeBlockPos(pos));
        Direction direction = state.getValue(BirtDwellingBlock.FACING);
        BlockPos blockPos = pos.relative(direction);
        boolean bl = !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty();

        if (bl && birtState != BirtState.EMERGENCY) return false;
        Entity newBirt = EntityType.loadEntityRecursive(nbtCompound, world, entity -> entity);
        if (newBirt != null) {
            if (newBirt instanceof com.ninni.species.server.entity.mob.update_1.Birt birtEntity) {
                BirtDwellingBlockEntity.ageBirt(BirtDwellingBlockEntity.Birt.ticksInDwelling, birtEntity);
                if (entities != null) entities.add(birtEntity);
                float f = newBirt.getBbWidth();
                double d = bl ? 0.0 : 0.55 + (double)(f / 2.0f);
                double x = (double)pos.getX() + 0.5 + d * (double)direction.getStepX();
                double y = (double)pos.getY() + 0.5 - (double)(newBirt.getBbHeight() / 2.0f);
                double z = (double)pos.getZ() + 0.5 + d * (double)direction.getStepZ();
                newBirt.moveTo(x, y, z, newBirt.getYRot(), newBirt.getXRot());
            } else {
                return false;
            }
            world.playSound(null, pos, SpeciesSoundEvents.BLOCK_BIRT_DWELLING_EXIT.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            world.gameEvent(GameEvent.BLOCK_CHANGE, pos, GameEvent.Context.of(newBirt, world.getBlockState(pos)));
            return world.addFreshEntity(newBirt);
        }
        return false;
    }

    static void removeIrrelevantNbtKeys(CompoundTag compound) {
        for (String string : IRRELEVANT_BIRT_NBT_KEYS) compound.remove(string);
    }

    private static void ageBirt(int ticks, com.ninni.species.server.entity.mob.update_1.Birt birt) {
        int i = birt.getAge();
        if (i < 0) birt.setAge(Math.min(0, i + ticks));
        else if (i > 0) birt.setAge(Math.max(0, i - ticks));
    }

    private static void tickBirts(Level world, BlockPos pos, BlockState state, List<Birt> birts) {
        boolean bl = false;
        Iterator<Birt> iterator = birts.iterator();
        world.setBlockAndUpdate(pos, state.setValue(BIRTS, birts.size()));
        while (iterator.hasNext()) {
            Birt birt = iterator.next();
            if (BirtDwellingBlockEntity.Birt.ticksInDwelling > birt.minOccupationTicks) {
                if (BirtDwellingBlockEntity.releaseBirt(world, pos, state, birt, null, BirtState.BIRT_RELEASED)) {
                    bl = true;
                    iterator.remove();
                }
            }
            ++BirtDwellingBlockEntity.Birt.ticksInDwelling;
        }
        if (bl) BirtDwellingBlockEntity.setChanged(world, pos, state);
    }

    public static void serverTick(Level world, BlockPos pos, BlockState state, BirtDwellingBlockEntity blockEntity) {
        BirtDwellingBlockEntity.tickBirts(world, pos, state, blockEntity.birts);
        BirtDwellingBlockEntity.tickLayEgg(blockEntity, world, pos, state);
        if (blockEntity.pacifyTicks > 0) blockEntity.pacifyTicks--;
        if (!blockEntity.birts.isEmpty() && world.getRandom().nextDouble() < 0.005) {
            double d = (double)pos.getX() + 0.5;
            double e = pos.getY();
            double f = (double)pos.getZ() + 0.5;
            world.playSound(null, d, e, f, SpeciesSoundEvents.BLOCK_BIRT_DWELLING_WORK.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
        }
    }

    @Override
    public void load(CompoundTag nbt) {
        super.load(nbt);
        this.birts.clear();
        ListTag nbtList = nbt.getList(BIRTS_KEY, 10);
        for (int i = 0; i < nbtList.size(); ++i) {
            CompoundTag nbtCompound = nbtList.getCompound(i);
            Birt birt = new Birt(nbtCompound.getCompound(ENTITY_DATA_KEY), nbtCompound.getInt(TICKS_IN_DWELLING_KEY), nbtCompound.getInt(MIN_OCCUPATION_TICKS_KEY));
            this.birts.add(birt);
        }
        this.day = nbt.getInt("Day");
        this.pacifyTicks = nbt.getInt("PacifyTicks");
    }

    @Override
    protected void saveAdditional(CompoundTag nbt) {
        super.saveAdditional(nbt);
        nbt.put(BIRTS_KEY, this.getBirts());
        nbt.putInt("Day", this.day);
        nbt.putInt("PacifyTicks", this.pacifyTicks);
    }

    public ListTag getBirts() {
        ListTag nbtList = new ListTag();
        for (Birt birt : this.birts) {
            CompoundTag nbtCompound = birt.entityData.copy();
            nbtCompound.remove("UUID");
            CompoundTag nbtCompound2 = new CompoundTag();
            nbtCompound2.put(ENTITY_DATA_KEY, nbtCompound);
            nbtCompound2.putInt(IRRELEVANT_BIRT_NBT_KEYS.toString(), BirtDwellingBlockEntity.Birt.ticksInDwelling);
            nbtCompound2.putInt(MIN_OCCUPATION_TICKS_KEY, birt.minOccupationTicks);
            nbtList.add(nbtCompound2);
        }
        return nbtList;
    }
    @Override
    public BirtDwellingListener getListener() {
        return this.birtDwellingListener;
    }

    public enum BirtState {
        BIRT_RELEASED,
        EMERGENCY
    }

    static class Birt {
        final CompoundTag entityData;
        static int ticksInDwelling;
        final int minOccupationTicks;

        Birt(CompoundTag entityData, int ticksInDwelling, int minOccupationTicks) {
            BirtDwellingBlockEntity.removeIrrelevantNbtKeys(entityData);
            this.entityData = entityData;
            BirtDwellingBlockEntity.Birt.ticksInDwelling = ticksInDwelling;
            this.minOccupationTicks = minOccupationTicks;
        }
    }

    public static class BirtDwellingListener implements GameEventListener {
        private final BlockState blockState;
        private final PositionSource positionSource;
        public BirtDwellingListener(BlockState blockState, PositionSource positionSource) {
            this.blockState = blockState;
            this.positionSource = positionSource;
        }
        @Override
        public PositionSource getListenerSource() {
            return this.positionSource;
        }
        @Override
        public int getListenerRadius() {
            return 8;
        }
        @Override
        public GameEventListener.DeliveryMode getDeliveryMode() {
            return GameEventListener.DeliveryMode.BY_DISTANCE;
        }
        @Override
        public boolean handleGameEvent(ServerLevel serverLevel, GameEvent gameEvent, GameEvent.Context context, Vec3 vec32) {
            Optional<Vec3> position = this.positionSource.getPosition(serverLevel);
            if (position.isEmpty()) return false;
            BlockPos blockPos = BlockPos.containing(position.get());
            if (com.ninni.species.server.entity.mob.update_1.Birt.isLoudNoise(gameEvent, serverLevel, BlockPos.containing(vec32))) {
                BlockEntity blockEntity = serverLevel.getBlockEntity(blockPos);
                if (blockEntity instanceof BirtDwellingBlockEntity birtDwellingBlockEntity && birtDwellingBlockEntity.pacifyTicks == 0) {
                    birtDwellingBlockEntity.pacifyTicks = 100;
                    return true;
                }
            }
            return false;
        }
    }
}
