package com.ninni.species.server.packet;

import com.ninni.species.Species;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class BlockEntitySyncPacket implements CustomPacketPayload {

    public static final CustomPacketPayload.Type<BlockEntitySyncPacket> TYPE = new CustomPacketPayload.Type<>(
            ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "block_entity_sync"));
    public static final StreamCodec<FriendlyByteBuf, BlockEntitySyncPacket> STREAM_CODEC = StreamCodec.of(BlockEntitySyncPacket::write, BlockEntitySyncPacket::read);
    @Override
    public @NotNull Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    final BlockPos pos;
    final CompoundTag tag;

    public BlockEntitySyncPacket(BlockPos pos, CompoundTag tag) {
        this.pos = pos;
        this.tag = tag;
    }

    public static void write(FriendlyByteBuf buffer, BlockEntitySyncPacket object) {
        buffer.writeBlockPos(object.pos);
        buffer.writeNbt(object.tag);
    }

    public static BlockEntitySyncPacket read(FriendlyByteBuf buffer) {
        return new BlockEntitySyncPacket(buffer.readBlockPos(), buffer.readNbt());
    }

    public static void handle(BlockEntitySyncPacket packet, IPayloadContext ctx) {
        ctx.enqueueWork(() -> {
            Level world;
            Player sender = ctx.player();
            if (ctx.flow().isClientbound())
                world = Species.PROXY.getWorld();
            else {
                if (sender == null) return;
                world = sender.level();
            }

            BlockEntity t = world.getBlockEntity(packet.pos);
            if (t != null) {
                t.loadCustomOnly(packet.tag, world.registryAccess());
                t.setChanged();
            }
        });
        //ctx.get().setPacketHandled(true);
    }
}