package com.ninni.species.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import com.ninni.species.client.inventory.CruncherInventoryMenu;
import com.ninni.species.client.inventory.CruncherInventoryScreen;
import com.ninni.species.registry.SpeciesBannerPatterns;
//import com.ninni.species.registry.SpeciesPaintingVariants;
import com.ninni.species.registry.SpeciesPaintingVariants;
import com.ninni.species.server.entity.mob.update_2.Cruncher;
import com.ninni.species.server.packet.OpenCruncherScreenPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.decoration.PaintingVariants;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BannerPatternLayers;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.Optional;
import java.util.function.Predicate;

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


    public static ItemStack getHopefulBannerInstance(HolderLookup.Provider provider) {
        ItemStack itemstack = new ItemStack(Items.WHITE_BANNER);
        var layers = new BannerPatternLayers.Builder().add(provider.lookupOrThrow(Registries.BANNER_PATTERN).getOrThrow(SpeciesBannerPatterns.VILLAGER), DyeColor.WHITE).build();
        itemstack.set(DataComponents.BANNER_PATTERNS, layers);
        itemstack.set(DataComponents.HIDE_ADDITIONAL_TOOLTIP, Unit.INSTANCE);
        itemstack.set(DataComponents.ITEM_NAME, Component.translatable("block.species.hopeful_banner").withStyle(ChatFormatting.GREEN));
        return itemstack;
    }



    public static ItemStack getSpeciesPainting(ResourceKey<PaintingVariant> variant, HolderLookup.Provider provider) {
        var paintingVariants = provider.lookupOrThrow(Registries.PAINTING_VARIANT);
        ItemStack itemStack = new ItemStack(Items.PAINTING);
        RegistryOps<Tag> registryops = provider.createSerializationContext(NbtOps.INSTANCE);
        var foundPainting = paintingVariants.listElements()
                .filter(painting -> painting.key().equals(variant))
                .findFirst();
        if (foundPainting.isPresent()) {
            CustomData customdata = CustomData.EMPTY
                    .update(registryops, Painting.VARIANT_MAP_CODEC, foundPainting.get())
                    .getOrThrow()
                    .update(tag -> tag.putString("id", "minecraft:painting"));
            itemStack.set(DataComponents.ENTITY_DATA, customdata);
            return itemStack;
        }

        return itemStack;
    }


}
