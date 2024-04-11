package com.ninni.species.client.inventory;

import com.ninni.species.Species;
import com.ninni.species.entity.Cruncher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;

@Environment(EnvType.CLIENT)
public class CruncherInventoryScreen extends AbstractContainerScreen<CruncherInventoryMenu> {
    private static final ResourceLocation HAMSTER_INVENTORY_LOCATION = new ResourceLocation(Species.MOD_ID, "textures/gui/container/cruncher.png");

    public CruncherInventoryScreen(CruncherInventoryMenu abstractContainerMenu, Inventory inventory, Cruncher cruncher) {
        super(abstractContainerMenu, inventory, cruncher.getDisplayName());
        //this.passEvents = false;
    }

    @Override
    public void render(GuiGraphics poseStack, int i, int j, float f) {
        super.render(poseStack, i, j, f);
        this.renderTooltip(poseStack, i, j);
    }

    @Override
    protected void renderBg(GuiGraphics poseStack, float f, int i, int j) {
        this.renderBackground(poseStack);
        int k = (this.width - this.imageWidth) / 2;
        int l = (this.height - this.imageHeight) / 2;
        poseStack.blit(HAMSTER_INVENTORY_LOCATION, k, l, 0, 0, this.imageWidth, this.imageHeight);
    }
}
