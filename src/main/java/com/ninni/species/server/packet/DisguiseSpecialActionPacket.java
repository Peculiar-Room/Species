package com.ninni.species.server.packet;

import com.ninni.species.mixin_util.LivingEntityAccess;
import com.ninni.species.registry.SpeciesNetwork;
import com.ninni.species.server.disguise.DisguiseBehaviorRegistry;
import com.ninni.species.server.disguise.cosmetic.DisguiseCosmeticRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.PacketDistributor;

import java.util.function.Supplier;

/** Client → server: wearer pressed {@code DISGUISE_ACTION_KEY}. Server runs the authoritative
 *  {@code onSpecialAction}, then broadcasts {@link DisguiseSpecialActionAckPacket} so every
 *  tracking client replays it. {@code shifted} carries sneak-modifier state for alt actions. */
public final class DisguiseSpecialActionPacket {

    private final boolean shifted;

    public DisguiseSpecialActionPacket() { this(false); }
    public DisguiseSpecialActionPacket(boolean shifted) { this.shifted = shifted; }

    public static void write(DisguiseSpecialActionPacket msg, FriendlyByteBuf buf) {
        buf.writeBoolean(msg.shifted);
    }

    public static DisguiseSpecialActionPacket read(FriendlyByteBuf buf) {
        return new DisguiseSpecialActionPacket(buf.readBoolean());
    }

    public static void handle(DisguiseSpecialActionPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            ServerPlayer player = ctx.get().getSender();
            if (player == null) return;
            LivingEntity disguise = ((LivingEntityAccess) player).getDisguisedEntity();
            if (disguise == null) return;
            com.ninni.species.api.disguise.ActionContext context =
                    com.ninni.species.api.disguise.ActionContext.of(player, msg.shifted);
            try {
                DisguiseBehaviorRegistry.get(disguise).onSpecialAction(player, disguise, context);
                DisguiseCosmeticRegistry.get(disguise).onSpecialAction(player, disguise, context);
            } catch (Throwable ignored) {}
            // Broadcast to tracking clients (and sender) so each side runs the action locally.
            // Context is recomputed per-side on the ack from local state; shifted is relayed.
            SpeciesNetwork.INSTANCE.send(
                    PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> player),
                    new DisguiseSpecialActionAckPacket(player.getId(), msg.shifted));
        });
        ctx.get().setPacketHandled(true);
    }
}
