package com.ninni.species.compat.jei;

import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.server.data.CruncherPelletManager;
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
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class CruncherPelletCategory implements IRecipeCategory<CruncherPelletManager.CruncherPelletData> {
    public static final ResourceLocation UID = new ResourceLocation(Species.MOD_ID, "cruncher_pellet");
    public static final ResourceLocation TEXTURE = new ResourceLocation(Species.MOD_ID, "textures/gui/jei/cruncher_pellet.png");
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawable slotDrawable;
    private final IDrawable cruncher;
    private final IDrawable sparkles;
    private final IDrawable resultArrow;

    public CruncherPelletCategory(IGuiHelper guiHelper) {
        this.background = guiHelper.createBlankDrawable(150, 40);
        this.cruncher = guiHelper.createDrawable(TEXTURE, 0, 16, 68, 44);
        this.icon = guiHelper.createDrawable(TEXTURE, 0, 0, 16, 16);
        this.slotDrawable = guiHelper.getSlotDrawable();
        this.sparkles = guiHelper.createDrawable(TEXTURE, 0, 28, 18, 13);
        this.resultArrow = guiHelper.createDrawable(TEXTURE, 0, 16, 21, 12);
    }

    @Override
    public RecipeType<CruncherPelletManager.CruncherPelletData> getRecipeType() {
        return JEIPlugin.CRUNCHER_PELLET;
    }

    @Override
    public Component getTitle() {
        return Component.translatable("jei.species.cruncher_pellet");
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
    public void setRecipe(IRecipeLayoutBuilder builder, CruncherPelletManager.CruncherPelletData recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 43, 12).addItemStack(recipe.item());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 91, 12)
                .addItemStack(SpeciesItems.CRUNCHER_PELLET.get().getDefaultInstance())
                .addTooltipCallback((view, tooltip) -> {
                    String entityName = recipe.entityType().getDescription().getString();
                    tooltip.add(Component.translatable("jei.species.cruncher_pellet.mob", entityName).setStyle(Style.EMPTY.withColor(0x00c2f9)));
                    tooltip.add(Component.translatable("jei.species.cruncher_pellet.amount", recipe.minTries(), recipe.maxTries()).setStyle(Style.EMPTY.withColor(0x007dde)));
                });
    }

    @Override
    public void draw(CruncherPelletManager.CruncherPelletData recipe, IRecipeSlotsView view, GuiGraphics stack, double mouseX, double mouseY) {
        cruncher.draw(stack, 23, -1);
        slotDrawable.draw(stack, 90, 11);
    }
}
