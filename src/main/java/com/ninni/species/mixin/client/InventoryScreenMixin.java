package com.ninni.species.mixin.client;

import com.ninni.species.mixin_util.EntityRenderDispatcherAccess;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import javax.annotation.Nullable;

@OnlyIn(Dist.CLIENT)
@Mixin(InventoryScreen.class)
public abstract class InventoryScreenMixin extends EffectRenderingInventoryScreen<InventoryMenu> implements RecipeUpdateListener {

    public InventoryScreenMixin(InventoryMenu p_98701_, Inventory p_98702_, Component p_98703_) {
        super(p_98701_, p_98702_, p_98703_);
    }

    @Inject(method = "renderEntityInInventory", at = @At("HEAD"))
    private static void onRenderInventoryStart(GuiGraphics graphics, int x, int y, int scale, Quaternionf bodyRot, @Nullable Quaternionf headRot, LivingEntity entity, CallbackInfo ci) {
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        ((EntityRenderDispatcherAccess) dispatcher).setIsRenderingInventoryEntity(true);
    }

    @Inject(method = "renderEntityInInventory", at = @At("RETURN"))
    private static void onRenderInventoryEnd(GuiGraphics graphics, int x, int y, int scale, Quaternionf bodyRot, @Nullable Quaternionf headRot, LivingEntity entity, CallbackInfo ci) {
        EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
        ((EntityRenderDispatcherAccess) dispatcher).setIsRenderingInventoryEntity(false);
    }

}
