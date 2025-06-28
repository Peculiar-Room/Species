package com.ninni.species.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Optional;
import java.util.stream.Stream;

@Mixin(targets = "net.minecraft.world.entity.npc.VillagerTrades$EmeraldsForVillagerTypeItem")
public abstract class EmeraldsForVillagerTypeItemMixin {

    @Redirect(method = "<init>", at = @At(value = "INVOKE", target = "Ljava/util/stream/Stream;findAny()Ljava/util/Optional;"))
    private <T> Optional<T> S$init(Stream instance) {
        return Optional.empty();
    }

}
