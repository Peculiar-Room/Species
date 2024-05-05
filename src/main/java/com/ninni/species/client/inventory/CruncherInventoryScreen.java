package com.ninni.species.client.inventory;

import com.ninni.species.Species;
import com.ninni.species.entity.Cruncher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.CyclingSlotBackground;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;

import java.util.List;
import java.util.function.Function;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventoryFollowsMouse;

@Environment(EnvType.CLIENT)
public class CruncherInventoryScreen extends AbstractContainerScreen<CruncherInventoryMenu> {
    private static final ResourceLocation CRUNCHER_INVENTORY_LOCATION = new ResourceLocation(Species.MOD_ID, "textures/gui/container/cruncher.png");
    private static final Function<String, ResourceLocation> FUNCTION = s -> new ResourceLocation(Species.MOD_ID, "item/empty_slot_" + s);
    private static final List<ResourceLocation> INPUT_LIST = List.of(
            FUNCTION.apply("arrow"),
            FUNCTION.apply("bone"),
            FUNCTION.apply("creeper_head"),
            FUNCTION.apply("ender_pearl"),
            FUNCTION.apply("gold_nugget"),
            FUNCTION.apply("gunpowder"),
            FUNCTION.apply("head"),
            FUNCTION.apply("ingot"),
            FUNCTION.apply("nautilus_shell"),
            FUNCTION.apply("phantom_membrane"),
            FUNCTION.apply("prismarine_crystals"),
            FUNCTION.apply("prismarine_shard"),
            FUNCTION.apply("rotten_flesh"),
            FUNCTION.apply("sand"),
            FUNCTION.apply("skull"),
            FUNCTION.apply("slime_ball"),
            FUNCTION.apply("spider_eye"),
            FUNCTION.apply("string"),
            FUNCTION.apply("tipped_arrow")
    );
    private final CyclingSlotBackground inputIcon = new CyclingSlotBackground(0);
    private float xMouse;
    private float yMouse;
    private Cruncher cruncher;

    public CruncherInventoryScreen(CruncherInventoryMenu abstractContainerMenu, Inventory inventory, Cruncher cruncher) {
        super(abstractContainerMenu, inventory, cruncher.getDisplayName());
        this.cruncher = cruncher;
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.inputIcon.tick(INPUT_LIST);
    }

    @Override
    protected void renderBg(GuiGraphics poseStack, float f, int i, int j) {
        this.renderBackground(poseStack);

        poseStack.blit(CRUNCHER_INVENTORY_LOCATION, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);

        this.inputIcon.render(this.menu, poseStack, f, this.leftPos, this.topPos);
        if (this.cruncher.getPelletData() == null) return;

        Entity entity = this.cruncher.getPelletData().entityType().create(cruncher.level());

        if (!(entity instanceof LivingEntity livingEntity)) return;

        renderEntityInInventoryFollowsMouse(poseStack, this.leftPos + 118, this.topPos + 64, 20, (float)(this.leftPos + 118) - this.xMouse, (float)(this.topPos + 66 - 40) - this.yMouse, livingEntity);
    }

    public void render(GuiGraphics guiGraphics, int i, int j, float f) {
        this.renderBackground(guiGraphics);
        this.xMouse = (float)i;
        this.yMouse = (float)j;
        super.render(guiGraphics, i, j, f);
        this.renderTooltip(guiGraphics, i, j);
    }

}
