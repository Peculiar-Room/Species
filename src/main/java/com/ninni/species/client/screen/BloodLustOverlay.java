package com.ninni.species.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesStatusEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.LayeredDraw;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RenderGuiLayerEvent;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import net.neoforged.neoforge.client.gui.VanillaGuiLayers;

@EventBusSubscriber(modid = Species.MOD_ID, value = Dist.CLIENT)
@OnlyIn(Dist.CLIENT)
public class BloodLustOverlay {
    private static final ResourceLocation SPECIES_ICONS = ResourceLocation.fromNamespaceAndPath(Species.MOD_ID, "textures/gui/icons.png");
    private static final ResourceLocation ICONS = ResourceLocation.withDefaultNamespace("textures/gui/icons.png");

    @SubscribeEvent
    public static void preGuiRender(RenderGuiLayerEvent.Pre event) {
        Minecraft minecraft = Minecraft.getInstance();
        LocalPlayer player = minecraft.player;
        if (shouldRenderBloodLust(player, event.getName())) {
            event.setCanceled(true);
            PoseStack poseStack = event.getGuiGraphics().pose();

            poseStack.pushPose();
            RenderSystem.enableBlend();

            renderFood(event.getGuiGraphics(), player);

            RenderSystem.disableBlend();
            poseStack.popPose();
        }
    }

    private static boolean shouldRenderBloodLust(LocalPlayer player, ResourceLocation layerName) {
        return player != null && player.hasEffect(SpeciesStatusEffects.BLOODLUST) && layerName == VanillaGuiLayers.FOOD_LEVEL && !player.isCreative() && !player.isSpectator();
    }

    private static void renderFood(GuiGraphics guiGraphics, Player player) {
        Minecraft minecraft = Minecraft.getInstance();
        Gui gui = minecraft.gui;

        //if (!(gui instanceof ScreenUtils forgeGui)) return;

        minecraft.getProfiler().push("food");

        RenderSystem.enableBlend();

        int left = guiGraphics.guiWidth() / 2 + 91;
        int top = guiGraphics.guiHeight() - gui.rightHeight;
        gui.rightHeight = gui.rightHeight + 10;
        FoodData stats = minecraft.player.getFoodData();
        int level = stats.getFoodLevel();

        for (int i = 0; i < 10; ++i) {
            int idx = i * 2 + 1;
            int x = left - i * 8 - 9;
            int y = top;

            if (player.getFoodData().getSaturationLevel() <= 0.0F && gui.tickCount % (level * 3 + 1) == 0) {
                y = top + (gui.random.nextInt(3) - 1);
            }

            //guiGraphics.blit(ICONS, x, y, 16, 27, 9, 9);
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
