package com.ninni.species.client;

import com.ninni.species.CommonProxy;
import com.ninni.species.Species;
import com.ninni.species.client.events.ClientEvents;
import com.ninni.species.client.events.ClientEventsHandler;
import com.ninni.species.client.screen.ScreenShakeEvent;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.server.entity.util.PlayerAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.MinecraftForge;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();
        MinecraftForge.EVENT_BUS.register(new ClientEvents());
    }

    public void clientSetup() {

        ItemProperties.register(SpeciesItems.CRANKBOW.get(),
                new ResourceLocation(Species.MOD_ID, "pull"),
                (stack, level, entity, seed) -> {
                    if (entity != null && ClientEventsHandler.PULL_PROGRESS.containsKey(entity.getUUID())) {
                        float progress = ClientEventsHandler.PULL_PROGRESS.get(entity.getUUID());

                        if (progress < 0.05F) return 0.0F;
                        else if (progress < 0.20F) return 0.15F;
                        else if (progress < 0.35F) return 0.3F;
                        else if (progress < 0.5F) return 0.4F;
                        else return 0.6F;
                    }
                    return -1.0F;
                });

        ItemProperties.register(SpeciesItems.RICOSHIELD.get(), new ResourceLocation(Species.MOD_ID, "blocking"), (stack, level, player, i) -> {
            return player != null && player.isUsingItem() && player.getUseItem() == stack ? 1.0F : 0.0F;
        });

        ItemProperties.register(SpeciesItems.SPECTRALIBUR.get(), new ResourceLocation(Species.MOD_ID, "souls"), (stack, level, player, i) -> {
            return stack.getOrCreateTag().contains("Souls") ? stack.getTag().getInt("Souls") : 0.0F;
        });

        ItemProperties.register(SpeciesItems.COIL.get(), new ResourceLocation(Species.MOD_ID, "placing"), (stack, level, player, i) -> {
            return stack.hasTag() && stack.getTag().contains("EndPointPos") ? 1.0F : 0.0F;
        });

        ItemProperties.register(SpeciesItems.HARPOON.get(), new ResourceLocation(Species.MOD_ID, "using"), (stack, level, player, i) -> {
            return player != null && player.isUsingItem() && player.getUseItem() == stack ? 1 : 0;
        });
    }

    @Override
    public void harpoonSync(int id) {
        Player player = Minecraft.getInstance().player;
        if (player instanceof PlayerAccess playerAccess) {
            playerAccess.setHarpoonId(id);
        }
    }

    @Override
    public Level getWorld() {
        return Minecraft.getInstance().level;
    }

    @Override
    public void screenShake(ScreenShakeEvent event) {
        ClientEvents.SCREEN_SHAKE_EVENTS.add(event);
    }
}
