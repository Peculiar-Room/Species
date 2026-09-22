package com.ninni.species.mixin;

import com.ninni.species.mixin_util.ServerPlayerAccess;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerListener;
import net.minecraft.world.inventory.ContainerSynchronizer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ServerPlayer.class)
public class ServerPlayerMixin implements ServerPlayerAccess {


    @Shadow
    private int containerCounter;

    @Shadow
    @Final
    private ContainerSynchronizer containerSynchronizer;
    @Shadow
    @Final
    private ContainerListener containerListener;

    public void getNextContainerCounter() {
        this.containerCounter = this.containerCounter % 100 + 1;
    }

    @Unique
    public void doInitMenu(AbstractContainerMenu menu) {
        menu.addSlotListener(this.containerListener);
        menu.setSynchronizer(this.containerSynchronizer);
    }

    public int getContainerCounter() {
        return containerCounter;
    }
}
