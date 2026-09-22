package com.ninni.species.client;

import com.ninni.species.CommonProxy;
import com.ninni.species.Species;
import com.ninni.species.client.events.ClientEvents;
import com.ninni.species.client.screen.ScreenShakeEvent;
import com.ninni.species.mixin_util.PlayerAccess;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.server.item.CrankbowItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;

public class ClientProxy extends CommonProxy {

    @Override
    public void init() {
        super.init();
    }

    public void clientSetup() {
        ItemProperties.register(SpeciesItems.CRANKBOW.get(),
                ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "pull"),
                (stack, level, entity, seed) -> {
                    if (!(entity instanceof Player)) return -1.0F;

                    var tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                    if (!tag.getBoolean(CrankbowItem.TAG_USING)) return -1.0F;

                    int cooldown = tag.getInt(CrankbowItem.TAG_COOLDOWN);
                    int maxCooldown = CrankbowItem.getShootingCooldown(stack, level.registryAccess());
                    float progress = 1.0f - ((float) cooldown / Math.max(1, maxCooldown));

                    if (progress < 0.05F) return 0.0F;
                    else if (progress < 0.20F) return 0.15F;
                    else if (progress < 0.35F) return 0.3F;
                    else if (progress < 0.5F) return 0.4F;
                    else return 0.6F;
                });

        ItemProperties.register(SpeciesItems.RICOSHIELD.get(), ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "blocking"), (stack, level, player, i) -> {
            return player != null && player.isUsingItem() && player.getUseItem() == stack ? 1.0F : 0.0F;
        });

        ItemProperties.register(SpeciesItems.SPECTRALIBUR.get(), ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "souls"), (stack, level, player, i) -> {
            return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains("Souls") ? stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt("Souls") : 0.0F;
        });

        ItemProperties.register(SpeciesItems.COIL.get(), ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "placing"), (stack, level, player, i) -> {
            return stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().contains("EndPointPos") ? 1.0F : 0.0F;
        });

        ItemProperties.register(SpeciesItems.HARPOON.get(), ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "using"), (stack, level, player, i) -> {
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
