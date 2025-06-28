package com.ninni.species.client.inventory;

import com.ninni.species.Species;
import com.ninni.species.server.entity.mob.update_2.Cruncher;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;
import java.util.function.Function;

import static net.minecraft.client.gui.screens.inventory.InventoryScreen.renderEntityInInventoryFollowsMouse;

@OnlyIn(Dist.CLIENT)
public class CruncherInventoryScreen extends AbstractContainerScreen<CruncherInventoryMenu> {
    private static final ResourceLocation CRUNCHER_INVENTORY_LOCATION = new ResourceLocation(Species.MOD_ID, "textures/gui/container/cruncher.png");
    private static final Function<String, ResourceLocation> FUNCTION = s -> new ResourceLocation(Species.MOD_ID, "item/empty_slot/" + s);
    private static final List<ResourceLocation> INPUT_LIST = List.of(
            FUNCTION.apply("arrow"),
            FUNCTION.apply("bone"),
            FUNCTION.apply("broken_links"),
            FUNCTION.apply("ender_pearl"),
            FUNCTION.apply("ghoul_tongue"),
            FUNCTION.apply("gold_nugget"),
            FUNCTION.apply("ingot"),
            FUNCTION.apply("kinetic_core"),
            FUNCTION.apply("mob_head"),
            FUNCTION.apply("nautilus_shell"),
            FUNCTION.apply("phantom_membrane"),
            FUNCTION.apply("powder"),
            FUNCTION.apply("prismarine_crystals"),
            FUNCTION.apply("prismarine_shard"),
            FUNCTION.apply("rotten_flesh"),
            FUNCTION.apply("sand"),
            FUNCTION.apply("slime_ball"),
            FUNCTION.apply("spider_eye"),
            FUNCTION.apply("string"),
            FUNCTION.apply("tipped_arrow"),
            FUNCTION.apply("werefang"),
            FUNCTION.apply("wicked_wax")
    );
    private final CruncherSlotBackground inputIcon = new CruncherSlotBackground(0);
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
        assert Minecraft.getInstance().level != null;
        this.inputIcon.tick(INPUT_LIST, Minecraft.getInstance().level.random);
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