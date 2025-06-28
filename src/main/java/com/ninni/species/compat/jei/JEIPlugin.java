package com.ninni.species.compat.jei;

import com.ninni.species.Species;
import com.ninni.species.server.data.CruncherPelletManager;
import com.ninni.species.server.data.GooberGooManager;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final ResourceLocation ID = new ResourceLocation(Species.MOD_ID, "jei_plugin");
    public static final RecipeType<GooberGooManager.GooberGooData> GOOBER_GOO = RecipeType.create(Species.MOD_ID, "goober_goo", GooberGooManager.GooberGooData.class);
    public static final RecipeType<CruncherPelletManager.CruncherPelletData> CRUNCHER_PELLET = RecipeType.create(Species.MOD_ID, "cruncher_pellet", CruncherPelletManager.CruncherPelletData.class);

    @Override
    public ResourceLocation getPluginUid() {
        return ID;
    }


    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new GooberGooCategory(registration.getJeiHelpers().getGuiHelper()));
        registration.addRecipeCategories(new CruncherPelletCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        List<GooberGooManager.GooberGooData> gooberGooRecipes = GooberGooManager.DATA.stream().map(data -> new GooberGooManager.GooberGooData(data.input(), data.output())).toList();
        registration.addRecipes(new RecipeType<>(GooberGooCategory.UID, GooberGooManager.GooberGooData.class), gooberGooRecipes);

        List<CruncherPelletManager.CruncherPelletData> cruncherPelletRecipe = CruncherPelletManager.DATA.values().stream().map(data -> new CruncherPelletManager.CruncherPelletData(data.entityType(), data.item(), data.minTries(), data.maxTries())).toList();
        registration.addRecipes(new RecipeType<>(CruncherPelletCategory.UID, CruncherPelletManager.CruncherPelletData.class), cruncherPelletRecipe);
    }

}