package com.ninni.species.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import com.ninni.species.client.inventory.CruncherInventoryMenu;
import com.ninni.species.client.inventory.CruncherInventoryScreen;
import com.ninni.species.registry.SpeciesBannerPatterns;
import com.ninni.species.registry.SpeciesPaintingVariants;
import com.ninni.species.server.entity.mob.update_2.Cruncher;
import com.ninni.species.server.packet.OpenCruncherScreenPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.*;

public class ClientEventsHandler {

    public static void openCruncherScreen(OpenCruncherScreenPacket packet) {
        Minecraft client = Minecraft.getInstance();
        Level level = client.level;
        Optional.ofNullable(level).ifPresent(world -> {
            Entity entity = world.getEntity(packet.getId());
            if (entity instanceof Cruncher cruncher) {
                int slotCount = packet.getSlotCount();
                int syncId = packet.getSyncId();
                LocalPlayer clientPlayerEntity = client.player;
                SimpleContainer simpleInventory = new SimpleContainer(slotCount);
                assert clientPlayerEntity != null;
                CruncherInventoryMenu cruncherInventoryMenu = new CruncherInventoryMenu(syncId, clientPlayerEntity.getInventory(), simpleInventory, cruncher);
                clientPlayerEntity.containerMenu = cruncherInventoryMenu;
                client.execute(() -> client.setScreen(new CruncherInventoryScreen(cruncherInventoryMenu, clientPlayerEntity.getInventory(), cruncher)));
            }
        });
    }

    public static boolean isValidKey(InputConstants.Key key) {
        return key.getType() == InputConstants.Type.KEYSYM && key.getValue() > 0;
    }

    public static ItemStack getHopefulBannerInstance() {
        ItemStack itemstack = new ItemStack(Items.WHITE_BANNER);
        CompoundTag compoundtag = new CompoundTag();
        ListTag listtag = (new BannerPattern.Builder()).addPattern(SpeciesBannerPatterns.VILLAGER.getKey(), DyeColor.WHITE).toListTag();
        compoundtag.put("Patterns", listtag);
        BlockItem.setBlockEntityData(itemstack, BlockEntityType.BANNER, compoundtag);
        itemstack.hideTooltipPart(ItemStack.TooltipPart.ADDITIONAL);
        itemstack.setHoverName(Component.translatable("block.species.hopeful_banner").withStyle(ChatFormatting.GREEN));
        return itemstack;
    }

    public static ItemStack getSpeciesPainting(PaintingVariant variant) {
        ItemStack itemStack = new ItemStack(Items.PAINTING);
        SpeciesPaintingVariants.PAINTING_VARIANTS.getEntries().forEach(variantRegistry -> {
            if (variantRegistry.get().equals(variant)) {
                CompoundTag compoundtag = itemStack.getOrCreateTagElement("EntityTag");
                Painting.storeVariant(compoundtag, variantRegistry.getHolder().get());
            }
        });

        return itemStack;
    }

}
