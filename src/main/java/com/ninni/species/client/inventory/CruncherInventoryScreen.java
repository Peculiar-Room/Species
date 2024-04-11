package com.ninni.species.client.inventory;

import com.ninni.species.Species;
import com.ninni.species.entity.Cruncher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Phantom;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Inventory;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventoryFollowsMouse;

@Environment(EnvType.CLIENT)
public class CruncherInventoryScreen extends AbstractContainerScreen<CruncherInventoryMenu> {
    private static final ResourceLocation HAMSTER_INVENTORY_LOCATION = new ResourceLocation(Species.MOD_ID, "textures/gui/container/cruncher.png");
    private float xMouse;
    private float yMouse;
    private Cruncher cruncher;

    public CruncherInventoryScreen(CruncherInventoryMenu abstractContainerMenu, Inventory inventory, Cruncher cruncher) {
        super(abstractContainerMenu, inventory, cruncher.getDisplayName());
        this.cruncher = cruncher;
    }


    @Override
    protected void renderBg(GuiGraphics poseStack, float f, int i, int j) {
        this.renderBackground(poseStack);
        int imgX = (this.width - this.imageWidth) / 2;
        int imgY = (this.height - this.imageHeight) / 2;
        poseStack.blit(HAMSTER_INVENTORY_LOCATION, imgX, imgY, 0, 0, this.imageWidth, this.imageHeight);

        Zombie phantom = EntityType.ZOMBIE.create(cruncher.level());
        //TODO replace with the entity being hunted
        renderEntityInInventoryFollowsMouse(poseStack, imgX + 118, imgY + 64, 20, (float)(imgX + 118) - this.xMouse, (float)(imgY + 66 - 40) - this.yMouse, phantom);
    }

    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        this.renderBackground(guiGraphics);
        this.xMouse = (float)i;
        this.yMouse = (float)j;
        super.render(guiGraphics, i, j, f);
        this.renderTooltip(guiGraphics, i, j);
    }

}
