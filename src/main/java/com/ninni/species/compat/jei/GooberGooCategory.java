package com.ninni.species.compat.jei;

import com.ninni.species.Species;
import com.ninni.species.server.data.GooberGooManager;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GooberGooCategory implements IRecipeCategory<GooberGooManager.GooberGooData> {
    public static final ResourceLocation UID = new ResourceLocation(Species.MOD_ID, "goober_goo");
    public static final ResourceLocation TEXTURE = new ResourceLocation(Species.MOD_ID, "textures/gui/jei/goober_goo.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final IDrawable goober;
    private final IDrawable sparkles;
    private final IDrawable resultArrow;

    public GooberGooCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(150, 40);
        this.goober = guiHelper.createDrawable(TEXTURE, 0, 48, 61, 29);
        this.icon = guiHelper.createDrawable(TEXTURE, 0, 0, 16, 16);
        this.slotDrawable = guiHelper.getSlotDrawable();
        this.sparkles = guiHelper.createDrawable(TEXTURE, 0, 28, 18, 13);
        this.resultArrow = guiHelper.createDrawable(TEXTURE, 0, 16, 21, 12);
    }

    @Override
    public RecipeType<GooberGooManager.GooberGooData> getRecipeType() {
        return JEIPlugin.GOOBER_GOO;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.species.goober_goo");
    }

    @Override
    public IDrawable getBackground() {
        return background;
    }

    @Override
    public IDrawable getIcon() {
        return icon;
    }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, GooberGooManager.GooberGooData recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 67, 12).addItemStack(recipe.input().asItem().getDefaultInstance());
        builder.addSlot(RecipeIngredientRole.OUTPUT, 110, 12).addItemStack(recipe.output().asItem().getDefaultInstance());
    }

    @Override
    public void draw(GooberGooManager.GooberGooData recipe, IRecipeSlotsView view, GuiGraphics stack, double mouseX, double mouseY) {
        slotDrawable.draw(stack, 66, 11);
        slotDrawable.draw(stack, 109, 11);
        goober.draw(stack, 3, 4);
        sparkles.draw(stack, 109, -2);
        resultArrow.draw(stack, 86, 14);
    }

}
