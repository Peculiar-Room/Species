package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.client.inventory.BirtdayCakeMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class SpeciesMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, Species.MOD_ID);

    public static final RegistryObject<MenuType<BirtdayCakeMenu>> BIRTDAY_CAKE = MENUS.register("birtday_cake", () -> IForgeMenuType.create(BirtdayCakeMenu::new));
}
