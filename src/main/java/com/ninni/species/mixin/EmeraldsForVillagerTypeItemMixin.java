package com.ninni.species.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.function.Consumer;
import net.minecraft.world.entity.npc.VillagerTrades.EmeraldsForVillagerTypeItem;

@Mixin(EmeraldsForVillagerTypeItem.class)
public abstract class EmeraldsForVillagerTypeItemMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/Optional;ifPresent(Ljava/util/function/Consumer;)V"))
    private void suppressMissingTypeCheck(Optional<?> opt, Consumer<?> consumer) {
    }
}
