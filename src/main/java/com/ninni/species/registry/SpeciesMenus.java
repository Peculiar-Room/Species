package com.ninni.species.registry;

import com.ninni.species.Species;
import com.ninni.species.client.inventory.BirtdayCakeMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class SpeciesMenus {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, Species.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<BirtdayCakeMenu>> BIRTDAY_CAKE = MENUS.register("birtday_cake", () -> IMenuTypeExtension.create(BirtdayCakeMenu::new));
}
