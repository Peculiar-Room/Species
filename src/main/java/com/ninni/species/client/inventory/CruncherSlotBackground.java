package com.ninni.species.client.inventory;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;

import java.util.List;

public class CruncherSlotBackground {
    private final int slotIndex;
    private List<ResourceLocation> icons = List.of();
    private int tick;
    private int iconIndex;

    public CruncherSlotBackground(int slotIndex) {
        this.slotIndex = slotIndex;
    }

    public void tick(List<ResourceLocation> resourceLocations, RandomSource random) {
        if (!this.icons.equals(resourceLocations)) {
            this.icons = resourceLocations;
            this.iconIndex = 0;
        }

        if (!this.icons.isEmpty() && ++this.tick % 20 == 0) {
            this.iconIndex = random.nextInt(this.icons.size());
        }

    }

    public void render(AbstractContainerMenu menu, GuiGraphics guiGraphics, float v, int i, int i1) {
        Slot slot = menu.getSlot(this.slotIndex);
        if (!this.icons.isEmpty() && !slot.hasItem()) {
            System.out.println(this.icons.get(this.iconIndex));
            this.renderIcon(slot, this.icons.get(this.iconIndex), guiGraphics, i, i1);
        }
    }

    private void renderIcon(Slot slot, ResourceLocation resourceLocation, GuiGraphics guiGraphics, int i, int i1) {
        TextureAtlasSprite atlas =  Minecraft.getInstance().getTextureAtlas(TextureAtlas.LOCATION_BLOCKS).apply(resourceLocation);
        guiGraphics.blit(i + slot.x, i1 + slot.y, 0, 16, 16, atlas, 1.0F, 1.0F, 1.0F, 1);
    }
}