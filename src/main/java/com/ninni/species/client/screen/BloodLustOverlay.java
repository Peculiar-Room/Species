package com.ninni.species.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesStatusEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.NamedGuiOverlay;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class BloodLustOverlay {
    private static final ResourceLocation SPECIES_ICONS = new ResourceLocation(Species.MOD_ID, "textures/gui/icons.png");
    private static final ResourceLocation ICONS = new ResourceLocation("textures/gui/icons.png");

    @SubscribeEvent
    public void preGuiRender(RenderGuiOverlayEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (shouldRenderBloodLust(player, event.getOverlay())) {
            event.setCanceled(true);
            PoseStack poseStack = event.getGuiGraphics().pose();

            poseStack.pushPose();
            RenderSystem.enableBlend();

            renderFood(event.getGuiGraphics(), player);

            RenderSystem.disableBlend();
            poseStack.popPose();
        }
    }

    private static boolean shouldRenderBloodLust(LocalPlayer player, NamedGuiOverlay overlay) {
        return player != null && player.hasEffect(SpeciesStatusEffects.BLOODLUST.get()) && overlay == VanillaGuiOverlay.FOOD_LEVEL.type() && !player.isCreative() && !player.isSpectator();
    }

    private static void renderFood(GuiGraphics guiGraphics, Player player) {
        Minecraft minecraft = Minecraft.getInstance();
        Gui gui = minecraft.gui;

        if (!(gui instanceof ForgeGui forgeGui)) return;

        minecraft.getProfiler().push("food");

        RenderSystem.enableBlend();

        int left = guiGraphics.guiWidth() / 2 + 91;
        int top = guiGraphics.guiHeight() - forgeGui.rightHeight;
        forgeGui.rightHeight = forgeGui.rightHeight + 10;
        FoodData stats = minecraft.player.getFoodData();
        int level = stats.getFoodLevel();

        for (int i = 0; i < 10; ++i) {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;

            if (player.getFoodData().getSaturationLevel() <= 0.0F && gui.tickCount % (level * 3 + 1) == 0) {
                y = top + (gui.random.nextInt(3) - 1);
            }

            guiGraphics.blit(ICONS, x, y, 16, 27, 9, 9);
            if (idx < level) {
                guiGraphics.blit(SPECIES_ICONS, x, y, 0, 0, 9, 9);
            }
            if (idx == level) {
                guiGraphics.blit(SPECIES_ICONS, x, y, 9, 0, 9, 9);
            }
        }

        RenderSystem.disableBlend();
        minecraft.getProfiler().pop();
    }

}
