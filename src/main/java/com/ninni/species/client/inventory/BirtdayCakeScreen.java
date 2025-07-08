package com.ninni.species.client.inventory;

import com.ninni.species.registry.SpeciesNetwork;

import com.ninni.species.server.block.entity.BirtdayCakeBlockEntity;
import com.ninni.species.server.packet.UpdateBirtdayCakeDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

public class BirtdayCakeScreen extends AbstractContainerScreen<BirtdayCakeMenu> {
    private final BirtdayCakeMenu menu;
    private EditBox nameField;
    private EditBox ageField;


    public BirtdayCakeScreen(BirtdayCakeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
        this.menu = menu;
    }

    @Override
    protected void init() {
        int centerX = this.width / 2;
        int centerY = this.height / 2;

        nameField = new EditBox(this.font, centerX - 75, centerY - 30, 150, 20, Component.literal("Name"));
        if (Minecraft.getInstance().level.getBlockEntity(menu.blockPos) instanceof BirtdayCakeBlockEntity blockEntity) nameField.setValue(blockEntity.getPlayerName());
        nameField.setMaxLength(20);
        nameField.setTextColor(0x47b7d8);
        this.addRenderableWidget(nameField);

        ageField  = new EditBox(this.font, centerX - 75, centerY,150, 20, Component.literal("Age"));
        if (Minecraft.getInstance().level.getBlockEntity(menu.blockPos) instanceof BirtdayCakeBlockEntity blockEntity) ageField.setValue(Integer.toString(blockEntity.getAge()));
        ageField.setFilter(s -> s.matches("\\d{0,3}"));
        ageField.setTextColor(0x4d6ec7);
        this.addRenderableWidget(ageField);

    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTicks, int mouseX, int mouseY) {
        this.renderBackground(guiGraphics);
        int centerY = this.height / 2;

        guiGraphics.drawCenteredString(this.font, this.title.getString(), this.width / 2, centerY - 50, 0xFFFFFF);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if ((nameField.isFocused() || ageField.isFocused()) && Minecraft.getInstance().options.keyInventory.matches(keyCode, scanCode)) return true;
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public void onClose() {
        String name = nameField.getValue();
        int age = 0;
        try {
            age = Integer.parseInt(ageField.getValue());
        } catch (NumberFormatException ignored) {}

        SpeciesNetwork.INSTANCE.sendToServer(new UpdateBirtdayCakeDataPacket(menu.blockPos, name, age));
        super.onClose();
    }

    @Override
    protected void renderLabels(GuiGraphics p_281635_, int p_282681_, int p_283686_) {
    }

    @Override
    public boolean isPauseScreen() {
        return true;
    }
}
