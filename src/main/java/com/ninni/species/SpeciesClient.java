package com.ninni.species;

import com.google.common.reflect.Reflection;
import com.ninni.species.client.particles.BirtdParticle;
import com.ninni.species.client.particles.SnoringParticle;
import com.ninni.species.client.renderer.*;
import com.ninni.species.entity.Springling;
import com.ninni.species.registry.SpeciesBlockEntities;
import com.ninni.species.registry.SpeciesBlocks;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesEntityModelLayers;
import com.ninni.species.registry.SpeciesNetwork;
import com.ninni.species.registry.SpeciesParticles;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.impl.client.rendering.BlockEntityRendererRegistryImpl;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.blockentity.BrushableBlockRenderer;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Player;

public class SpeciesClient implements ClientModInitializer {
    public static KeyMapping extendKey;
    public static KeyMapping retractKey;

    @Override
    public void onInitializeClient() {
        extendKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.extend", 265, "key.categories.species"));
        retractKey = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.retract", 264, "key.categories.species"));
        Reflection.initialize(SpeciesEntityModelLayers.class);
        EntityRendererRegistry.register(SpeciesEntities.WRAPTOR, WraptorRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.DEEPFISH, DeepfishRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.ROOMBUG, RoombugRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.BIRT, BirtRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.BIRT_EGG, ThrownItemRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.LIMPET, LimpetRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.TREEPER, TreeperRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.TREEPER_SAPLING, TreeperSaplingRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.GOOBER, GooberRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.CRUNCHER, CruncherRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.MAMMUTILATION, MammutilationRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.SPRINGLING, SpringlingRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.CRUNCHER_PELLET, FallingBlockRenderer::new);

        ParticleFactoryRegistry.getInstance().register(SpeciesParticles.SNORING, SnoringParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SpeciesParticles.BIRTD, BirtdParticle.Factory::new);

        BlockEntityRenderers.register(SpeciesBlockEntities.CRUNCHER_PELLET, BrushableBlockRenderer::new);

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
                SpeciesBlocks.BIRT_DWELLING,
                SpeciesBlocks.BONE_SPIKE,
                SpeciesBlocks.TREEPER_SAPLING
        );

        SpeciesNetwork.clientInit();

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            Player player = client.player;
            if (player != null && player.getVehicle() instanceof Springling springling) {
                float extendedAmount = springling.getExtendedAmount();
                if (!retractKey.isDown() && !springling.level().getBlockState(player.blockPosition().above(2)).isSolid() && extendKey.isDown()) {
                    FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
                    friendlyByteBuf.writeFloat(0.1F);
                    friendlyByteBuf.writeBoolean(extendedAmount < springling.getMaxExtendedAmount());
                    ClientPlayNetworking.send(SpeciesNetwork.UPDATE_SPRINGLING_EXTENDED_DATA, friendlyByteBuf);
                }
                if (retractKey.isDown() && !extendKey.isDown()) {
                    FriendlyByteBuf friendlyByteBuf = PacketByteBufs.create();
                    friendlyByteBuf.writeFloat(-0.25F);
                    friendlyByteBuf.writeBoolean(extendedAmount > 0);
                    ClientPlayNetworking.send(SpeciesNetwork.UPDATE_SPRINGLING_EXTENDED_DATA, friendlyByteBuf);
                }
            }
        });
    }
}
