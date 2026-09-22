package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.server.packet.*;
import net.minecraft.network.protocol.game.ServerGamePacketListener;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.NetworkRegistry;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

import java.util.Optional;

@EventBusSubscriber(modid = Species.MOD_ID)
public class SpeciesNetwork {
    private static final String PROTOCOL_VERSION = "1";
    /*
    public static final ServerGamePacketListener INSTANCE = NetworkRegistry.b.named(
                    new ResourceLocation(Species.MOD_ID, "network"))
            .clientAcceptedVersions(PROTOCOL_VERSION::equals)
            .serverAcceptedVersions(PROTOCOL_VERSION::equals)
            .networkProtocolVersion(() -> PROTOCOL_VERSION)
            .simpleChannel();
            
     */

    protected static int packetID = 0;

    public SpeciesNetwork() {
    }

    @SubscribeEvent
    public static void onRegisterPayloads(RegisterPayloadHandlersEvent event) {
        PayloadRegistrar registrar = event.registrar("species");
        registrar.playToClient(SendSpringlingPacket.TYPE, SendSpringlingPacket.STREAM_CODEC, SendSpringlingPacket::handle);
        registrar.playToClient(OpenCruncherScreenPacket.TYPE, OpenCruncherScreenPacket.STREAM_CODEC, OpenCruncherScreenPacket::handle);
        registrar.playToClient(PlayGutFeelingSoundPacket.TYPE, PlayGutFeelingSoundPacket.STREAM_CODEC, PlayGutFeelingSoundPacket::handle);
        registrar.playToServer(UpdateSpringlingDataPacket.TYPE, UpdateSpringlingDataPacket.STREAM_CODEC, UpdateSpringlingDataPacket::handle);
        registrar.playToClient(SnatchedPacket.TYPE, SnatchedPacket.STREAM_CODEC, SnatchedPacket::handle);
        registrar.playToClient(TankedPacket.TYPE, TankedPacket.STREAM_CODEC, TankedPacket::handle);
        registrar.playToClient(BlockEntitySyncPacket.TYPE, BlockEntitySyncPacket.STREAM_CODEC, BlockEntitySyncPacket::handle);
        registrar.playToServer(HarpoonInputPacket.TYPE, HarpoonInputPacket.STREAM_CODEC, HarpoonInputPacket::handle);
        registrar.playToClient(HarpoonSyncPacket.TYPE, HarpoonSyncPacket.STREAM_CODEC, HarpoonSyncPacket::handle);
        registrar.playBidirectional(GooberGooSyncPacket.TYPE, GooberGooSyncPacket.STREAM_CODEC, GooberGooSyncPacket::handle);
        registrar.playBidirectional(CruncherPelletSyncPacket.TYPE, CruncherPelletSyncPacket.STREAM_CODEC, CruncherPelletSyncPacket::handle);
        registrar.playToServer(UpdateBirtdayCakeDataPacket.TYPE, UpdateBirtdayCakeDataPacket.STREAM_CODEC, UpdateBirtdayCakeDataPacket::handle);
        //INSTANCE.registerMessage(getPacketID(), SendSpringlingPacket.class, SendSpringlingPacket::write, SendSpringlingPacket::read, SendSpringlingPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //INSTANCE.registerMessage(getPacketID(), OpenCruncherScreenPacket.class, OpenCruncherScreenPacket::write, OpenCruncherScreenPacket::read, OpenCruncherScreenPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //INSTANCE.registerMessage(getPacketID(), PlayGutFeelingSoundPacket.class, PlayGutFeelingSoundPacket::write, PlayGutFeelingSoundPacket::read, PlayGutFeelingSoundPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //INSTANCE.registerMessage(getPacketID(), UpdateSpringlingDataPacket.class, UpdateSpringlingDataPacket::write, UpdateSpringlingDataPacket::read, UpdateSpringlingDataPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        //INSTANCE.registerMessage(getPacketID(), SnatchedPacket.class, SnatchedPacket::write, SnatchedPacket::read, SnatchedPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //INSTANCE.registerMessage(getPacketID(), TankedPacket.class, TankedPacket::write, TankedPacket::read, TankedPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //INSTANCE.registerMessage(getPacketID(), BlockEntitySyncPacket.class, BlockEntitySyncPacket::write, BlockEntitySyncPacket::read, BlockEntitySyncPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //INSTANCE.registerMessage(getPacketID(), HarpoonInputPacket.class, HarpoonInputPacket::write, HarpoonInputPacket::read, HarpoonInputPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
        //INSTANCE.registerMessage(getPacketID(), HarpoonSyncPacket.class, HarpoonSyncPacket::write, HarpoonSyncPacket::read, HarpoonSyncPacket::handle, Optional.of(NetworkDirection.PLAY_TO_CLIENT));
        //INSTANCE.registerMessage(getPacketID(), GooberGooSyncPacket.class, GooberGooSyncPacket::write, GooberGooSyncPacket::read, GooberGooSyncPacket::handle);
        //INSTANCE.registerMessage(getPacketID(), CruncherPelletSyncPacket.class, CruncherPelletSyncPacket::write, CruncherPelletSyncPacket::read, CruncherPelletSyncPacket::handle);
        //INSTANCE.registerMessage(getPacketID(), UpdateBirtdayCakeDataPacket.class, UpdateBirtdayCakeDataPacket::write, UpdateBirtdayCakeDataPacket::read, UpdateBirtdayCakeDataPacket::handle, Optional.of(NetworkDirection.PLAY_TO_SERVER));
    }

    public static int getPacketID() {
        return packetID++;
    }
}
