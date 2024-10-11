package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.network.OpenCruncherScreenPacket;
import com.ninni.species.network.PlayGutFeelingSoundPacket;
import com.ninni.species.network.SendSpringlingPacket;
import com.ninni.species.network.UpdateSpringlingDataPacket;
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
    }

    public static int getPacketID() {
        return packetID++;
    }
}
