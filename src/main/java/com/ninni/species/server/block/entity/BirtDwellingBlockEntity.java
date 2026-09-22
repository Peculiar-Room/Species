package com.ninni.species.server.block.entity;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ninni.species.registry.SpeciesBlockEntities;
import com.ninni.species.registry.SpeciesDataComponents;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesSoundEvents;
import com.ninni.species.server.block.BirtDwellingBlock;
import com.ninni.species.server.entity.mob.update_1.Birt;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.CustomData;
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

public class BirtDwellingBlockEntity extends BlockEntity implements GameEventListener.Provider<BirtDwellingBlockEntity.BirtDwellingListener>  {
    public static final String BIRTS_KEY = "Birts";
    private static final List<String> IRRELEVANT_BIRT_NBT_KEYS = Arrays.asList("Air", "Bees", "ArmorDropChances", "ArmorItems", "Brain", "CanPickUpLoot", "DeathTime", "FallDistance", "FallFlying", "Fire", "HandDropChances", "HandItems", "HurtByTimestamp", "HurtTime", "LeftHanded", "Motion", "NoGravity", "OnGround", "PortalCooldown", "Pos", "Rotation", "CannotEnterDwellingTicks", "CannotEnterHiveTicks", "TicksSincePollination", "CropsGrownSincePollination", "DwellingPos", "HivePos", "Passengers", "Leash", "UUID");
    private final List<BirtData> birts = Lists.newArrayList();
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
                if (!(entity instanceof Birt birt)) continue;
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
        List<Entity> list = Lists.newArrayList();
        this.birts.removeIf((birtData) -> releaseBirt(this.level, this.worldPosition, state, birtData.toOccupant(), list, birtState));
        if (!list.isEmpty()) {
            super.setChanged();
        }

        return list;
    }

    public void tryEnterDwelling(Entity entity) {
        if (this.birts.size() >= 3) {
            return;
        }
        entity.stopRiding();
        entity.ejectPassengers();
        CompoundTag nbtCompound = new CompoundTag();
        entity.save(nbtCompound);
        BlockPos blockPos = this.getBlockPos();
        this.addBirt(Occupant.of(entity));
        if (this.level != null) {
            this.level.playSound(null, blockPos.getX(), blockPos.getY(), blockPos.getZ(), SpeciesSoundEvents.BLOCK_BIRT_DWELLING_ENTER.get(), SoundSource.BLOCKS, 1.0f, 1.0f);
            this.level.gameEvent(GameEvent.BLOCK_CHANGE, blockPos, GameEvent.Context.of(entity, this.getBlockState()));
        }
        entity.discard();
        super.setChanged();
    }

    public void addBirt(BirtDwellingBlockEntity.Occupant occupant) {
        this.birts.add(new BirtDwellingBlockEntity.BirtData(occupant));
    }

    private static boolean releaseBirt(Level world, BlockPos pos, BlockState state, Occupant birt, @Nullable List<Entity> entities, BirtState birtState) {
        if (world.isDay() && birtState != BirtState.EMERGENCY) {
            return false;
        }

        Direction direction = state.getValue(BirtDwellingBlock.FACING);
        BlockPos blockPos = pos.relative(direction);
        boolean bl = !world.getBlockState(blockPos).getCollisionShape(world, blockPos).isEmpty();

        if (bl && birtState != BirtState.EMERGENCY) {
            return false;
        }
        Entity newBirt = birt.createEntity(world);
        if (newBirt != null) {
            if (newBirt instanceof Birt birtEntity) {
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


    private static void tickBirts(Level world, BlockPos pos, BlockState state, List<BirtData> birts) {
        boolean bl = false;
        Iterator<BirtData> iterator = birts.iterator();
        world.setBlockAndUpdate(pos, state.setValue(BIRTS, birts.size()));
        while (iterator.hasNext()) {
            BirtData birt = iterator.next();
            if (birt.tick()) {
                if (BirtDwellingBlockEntity.releaseBirt(world, pos, state, birt.toOccupant(), null, BirtState.BIRT_RELEASED)) {
                    bl = true;
                    iterator.remove();
                }
            }
            ++birt.ticksInDwelling;
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
    public void loadAdditional(CompoundTag nbt, HolderLookup.Provider provider) {
        super.loadAdditional(nbt, provider);
        this.birts.clear();

        if (nbt.contains(BIRTS_KEY)) {
            BirtDwellingBlockEntity.Occupant.LIST_CODEC
                    .parse(NbtOps.INSTANCE, nbt.get(BIRTS_KEY))
                    .resultOrPartial()
                    .ifPresent(p_330134_ -> p_330134_.forEach(this::addBirt));
        }
        this.day = nbt.getInt("Day");
        this.pacifyTicks = nbt.getInt("PacifyTicks");
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put(BIRTS_KEY, BirtDwellingBlockEntity.Occupant.LIST_CODEC.encodeStart(NbtOps.INSTANCE, this.getBirts()).getOrThrow());
        tag.putInt("Day", this.day);
        tag.putInt("PacifyTicks", this.pacifyTicks);
    }

    @Override
    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.birts.clear();
        List<BirtDwellingBlockEntity.Occupant> list = componentInput.getOrDefault(SpeciesDataComponents.BIRTS.get(), List.of());
        list.forEach(this::addBirt);
    }

    @Override
    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(SpeciesDataComponents.BIRTS.get(), this.getBirts());
    }

    public List<Occupant> getBirts() {
        return this.birts.stream().map(BirtData::toOccupant).toList();
    }
    @Override
    public BirtDwellingListener getListener() {
        return this.birtDwellingListener;
    }

    public enum BirtState {
        BIRT_RELEASED,
        EMERGENCY
    }

    static class BirtData {
        private final Occupant occupant;
        private int ticksInDwelling;
        final int minOccupationTicks;

        BirtData(Occupant occupant) {
            this.occupant = occupant;
            this.ticksInDwelling = occupant.ticksInDwelling;
            this.minOccupationTicks = occupant.minOccupationTicks;
        }

        public boolean tick() {
            return this.ticksInDwelling++ > this.occupant.minOccupationTicks;
        }

        public Occupant toOccupant() {
            return new Occupant(this.occupant.entityData, this.ticksInDwelling, this.occupant.minOccupationTicks);
        }
    }

    public record Occupant(CustomData entityData, int ticksInDwelling, int minOccupationTicks) {
        public static final Codec<BirtDwellingBlockEntity.Occupant> CODEC = RecordCodecBuilder.create((occupant) -> occupant
                .group(
                        CustomData.CODEC.optionalFieldOf("entity_data", CustomData.EMPTY).forGetter(BirtDwellingBlockEntity.Occupant::entityData),
                        Codec.INT.fieldOf("ticks_in_dwelling").forGetter(BirtDwellingBlockEntity.Occupant::ticksInDwelling),
                        Codec.INT.fieldOf("min_ticks_in_dwelling").forGetter(BirtDwellingBlockEntity.Occupant::minOccupationTicks))
                .apply(occupant, BirtDwellingBlockEntity.Occupant::new));
        public static final Codec<List<Occupant>> LIST_CODEC = CODEC.listOf();

        public static final StreamCodec<ByteBuf, Occupant> STREAM_CODEC = StreamCodec.composite(
                CustomData.STREAM_CODEC, Occupant::entityData,
                ByteBufCodecs.VAR_INT, Occupant::ticksInDwelling,
                ByteBufCodecs.VAR_INT, Occupant::minOccupationTicks,
                Occupant::new
        );

        public static BirtDwellingBlockEntity.Occupant of(Entity entity) {
            CompoundTag compoundtag = new CompoundTag();
            compoundtag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(SpeciesEntities.BIRT.get()).toString());
            entity.save(compoundtag);
            Objects.requireNonNull(compoundtag);
            BirtDwellingBlockEntity.IRRELEVANT_BIRT_NBT_KEYS.forEach(compoundtag::remove);
            return new BirtDwellingBlockEntity.Occupant(CustomData.of(compoundtag), 0, 1200);
        }

        public static BirtDwellingBlockEntity.Occupant create(int ticksInDwelling) {
            CompoundTag compoundtag = new CompoundTag();
            compoundtag.putString("id", BuiltInRegistries.ENTITY_TYPE.getKey(SpeciesEntities.BIRT.get()).toString());
            return new BirtDwellingBlockEntity.Occupant(CustomData.of(compoundtag), ticksInDwelling, 600);
        }

        public Entity createEntity(Level level) {
            CompoundTag compoundtag = this.entityData.copyTag();
            Objects.requireNonNull(compoundtag);
            BirtDwellingBlockEntity.IRRELEVANT_BIRT_NBT_KEYS.forEach(compoundtag::remove);
            Entity entity = EntityType.loadEntityRecursive(compoundtag, level, (ent) -> ent);
            if (entity != null) {
                entity.setNoGravity(true);
                if (entity instanceof Birt birt) {
                    setBirtReleaseData(this.ticksInDwelling, birt);
                }

                return entity;
            } else {
                return null;
            }
        }

        private static void setBirtReleaseData(int ticksInDwelling, Birt birt) {
            int i = birt.getAge();
            if (i < 0) birt.setAge(Math.min(0, i + ticksInDwelling));
            else if (i > 0) birt.setAge(Math.max(0, i - ticksInDwelling));

            birt.setInLoveTime(Math.max(0, birt.getInLoveTime() - ticksInDwelling));
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
        public DeliveryMode getDeliveryMode() {
            return DeliveryMode.BY_DISTANCE;
        }
        @Override
        public boolean handleGameEvent(ServerLevel serverLevel, Holder<GameEvent> holder, GameEvent.Context context, Vec3 vec32) {
            Optional<Vec3> position = this.positionSource.getPosition(serverLevel);
            if (position.isEmpty()) return false;
            BlockPos blockPos = BlockPos.containing(position.get());
            if (Birt.isLoudNoise(holder.value(), serverLevel, BlockPos.containing(vec32))) {
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
