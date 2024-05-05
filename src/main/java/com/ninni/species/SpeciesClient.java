package com.ninni.species;

import com.google.common.reflect.Reflection;
import com.ninni.species.client.inventory.CruncherInventoryMenu;
import com.ninni.species.client.inventory.CruncherInventoryScreen;
import com.ninni.species.client.particles.AscendingDustParticle;
import com.ninni.species.client.particles.RotatingParticle;
import com.ninni.species.client.particles.PelletDripParticle;
import com.ninni.species.client.particles.SnoringParticle;
import com.ninni.species.client.renderer.*;
import com.ninni.species.client.renderer.entity.GooberGooRenderer;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.entity.Springling;
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
import net.minecraft.client.KeyMapping;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.FallingBlockRenderer;
import net.minecraft.client.renderer.entity.ThrownItemRenderer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Optional;

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
        EntityRendererRegistry.register(SpeciesEntities.TROOPER, TrooperRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.GOOBER, GooberRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.GOOBER_GOO, GooberGooRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.CRUNCHER, CruncherRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.MAMMUTILATION, MammutilationRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.SPRINGLING, SpringlingRenderer::new);
        EntityRendererRegistry.register(SpeciesEntities.CRUNCHER_PELLET, FallingBlockRenderer::new);

        ParticleFactoryRegistry.getInstance().register(SpeciesParticles.SNORING, SnoringParticle.Factory::new);
        ParticleFactoryRegistry.getInstance().register(SpeciesParticles.BIRTD, RotatingParticle.BirtdFactory::new);
        ParticleFactoryRegistry.getInstance().register(SpeciesParticles.FOOD, RotatingParticle.FoodFactory::new);
        ParticleFactoryRegistry.getInstance().register(SpeciesParticles.ASCENDING_DUST, AscendingDustParticle.Factory::new);

        ParticleFactoryRegistry.getInstance().register(SpeciesParticles.DRIPPING_PELLET_DRIP, provider -> (particleOptions, clientLevel, d, e, f, g, h, i) -> {
            return PelletDripParticle.createPelletDripHangParticle(particleOptions, clientLevel, d, e, f, g, h, i, provider);
        });
        ParticleFactoryRegistry.getInstance().register(SpeciesParticles.FALLING_PELLET_DRIP, provider -> (particleOptions, clientLevel, d, e, f, g, h, i) -> {
            return PelletDripParticle.createPelletDripFallParticle(particleOptions, clientLevel, d, e, f, g, h, i, provider);
        });
        ParticleFactoryRegistry.getInstance().register(SpeciesParticles.LANDING_PELLET_DRIP, provider -> (particleOptions, clientLevel, d, e, f, g, h, i) -> {
            return PelletDripParticle.createPelletDripLandParticle(particleOptions, clientLevel, d, e, f, g, h, i, provider);
        });

        BlockRenderLayerMap.INSTANCE.putBlocks(RenderType.cutout(),
                SpeciesBlocks.ALPHACENE_GRASS,
                SpeciesBlocks.ALPHACENE_TALL_GRASS,
                SpeciesBlocks.ALPHACENE_MUSHROOM,
                SpeciesBlocks.CRUNCHER_EGG,
                SpeciesBlocks.BIRT_DWELLING,
                SpeciesBlocks.BONE_SPIKE,
                SpeciesBlocks.TROOPER,
                SpeciesBlocks.ALPHACENE_MUSHROOM_GROWTH
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
