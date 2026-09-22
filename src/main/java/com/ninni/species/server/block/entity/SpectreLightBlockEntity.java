package com.ninni.species.server.block.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.FastColor;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockEntityExtension;
import org.jetbrains.annotations.Nullable;

public abstract class SpectreLightBlockEntity extends BlockEntity {
    String TAG_COLOR = "color";
    public DyedItemColor dyeColor;
    private final DyedItemColor defaultColor = new DyedItemColor(0x7CF2F5, false);

    public SpectreLightBlockEntity(BlockEntityType<?> p_155228_, BlockPos p_155229_, BlockState p_155230_) {
        super(p_155228_, p_155229_, p_155230_);
        dyeColor = defaultColor;
    }

    public int getColor() {
        return FastColor.ARGB32.opaque(this.dyeColor.rgb());
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);

        if (tag.contains(TAG_COLOR)) {
            DyedItemColor.CODEC.parse(provider.createSerializationContext(NbtOps.INSTANCE), tag.get(TAG_COLOR))
                    .resultOrPartial().ifPresent((dyedItemColor) -> this.dyeColor = dyedItemColor);
        }
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        if (!this.dyeColor.equals(defaultColor)) {
            tag.put(TAG_COLOR, DyedItemColor.CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), this.dyeColor).getOrThrow());
        }
    }

    @Override
    public CompoundTag getUpdateTag(HolderLookup.Provider provider) {
        return this.saveWithoutMetadata(provider);
    }

    protected void applyImplicitComponents(BlockEntity.DataComponentInput componentInput) {
        super.applyImplicitComponents(componentInput);
        this.dyeColor = componentInput.getOrDefault(DataComponents.DYED_COLOR, defaultColor);
    }

    protected void collectImplicitComponents(DataComponentMap.Builder components) {
        super.collectImplicitComponents(components);
        components.set(DataComponents.DYED_COLOR, this.dyeColor);
    }

    public void removeComponentsFromTag(CompoundTag tag) {
        tag.remove(TAG_COLOR);
    }


    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }
}
