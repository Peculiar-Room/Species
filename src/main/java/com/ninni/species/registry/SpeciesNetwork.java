package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.packet.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Optional;

public class SpeciesNetwork {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
                    new ResourceLocation(Species.MOD_ID, "network"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();

    protected static int packetID = 0;

    public SpeciesNetwork() {
    }

    public static void init() {
        INSTANCE.registerMessage(getPacketID(), SendSpringlingPacket.class, SendSpringlingPacket::write, SendSpringlingPacket::read, SendSpringlingPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(getPacketID(), OpenCruncherScreenPacket.class, OpenCruncherScreenPacket::write, OpenCruncherScreenPacket::read, OpenCruncherScreenPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(getPacketID(), PlayGutFeelingSoundPacket.class, PlayGutFeelingSoundPacket::write, PlayGutFeelingSoundPacket::read, PlayGutFeelingSoundPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(getPacketID(), UpdateSpringlingDataPacket.class, UpdateSpringlingDataPacket::write, UpdateSpringlingDataPacket::read, UpdateSpringlingDataPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(getPacketID(), SnatchedPacket.class, SnatchedPacket::write, SnatchedPacket::read, SnatchedPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(getPacketID(), TankedPacket.class, TankedPacket::write, TankedPacket::read, TankedPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(getPacketID(), BlockEntitySyncPacket.class, BlockEntitySyncPacket::write, BlockEntitySyncPacket::read, BlockEntitySyncPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(getPacketID(), HarpoonInputPacket.class, HarpoonInputPacket::write, HarpoonInputPacket::read, HarpoonInputPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        INSTANCE.registerMessage(getPacketID(), HarpoonSyncPacket.class, HarpoonSyncPacket::write, HarpoonSyncPacket::read, HarpoonSyncPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(getPacketID(), CrankbowPullPacket.class, CrankbowPullPacket::write, CrankbowPullPacket::read, CrankbowPullPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        INSTANCE.registerMessage(getPacketID(), GooberGooSyncPacket.class, GooberGooSyncPacket::write, GooberGooSyncPacket::read, GooberGooSyncPacket::handle);
        INSTANCE.registerMessage(getPacketID(), CruncherPelletSyncPacket.class, CruncherPelletSyncPacket::write, CruncherPelletSyncPacket::read, CruncherPelletSyncPacket::handle);
    }

    public static int getPacketID() {
        return packetID++;
    }
}
