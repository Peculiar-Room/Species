package com.ninni.species.mixin;

import com.ninni.species.Species;
import com.ninni.species.client.inventory.CruncherInventoryMenu;
import com.ninni.species.entity.Cruncher;
import com.ninni.species.entity.CruncherOpenContainer;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(ServerPlayer.class)
public abstract class ServerPlayerMixin implements CruncherOpenContainer {
    @Shadow protected abstract void nextContainerCounter();

    @Shadow protected abstract void initMenu(AbstractContainerMenu abstractContainerMenu);

    @Shadow private int containerCounter;

    @Override
    public void openCruncherInventory(Cruncher cruncher, Container container) {
        ServerPlayer $this = (ServerPlayer) (Object) this;
        if ($this.containerMenu != $this.inventoryMenu) {
            $this.closeContainer();
        }
        this.nextContainerCounter();
        FriendlyByteBuf buf = PacketByteBufs.create();
        buf.writeInt(cruncher.getId());
        buf.writeInt(container.getContainerSize());
        buf.writeInt(this.containerCounter);
        ServerPlayNetworking.send($this, Species.OPEN_CRUNCHER_SCREEN, buf);
        $this.containerMenu = new CruncherInventoryMenu(this.containerCounter, $this.getInventory(), container, cruncher);
        this.initMenu($this.containerMenu);
    }
}
