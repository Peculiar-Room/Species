package com.ninni.species.mixin.client;

import com.ninni.species.mixin_util.EntityRenderDispatcherAccess;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@OnlyIn(Dist.CLIENT)
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin implements EntityRenderDispatcherAccess {
    @Unique
    private static boolean isRenderingInventoryEntity = false;

    @Override
    public boolean getRenderingInventoryEntity() {
        return isRenderingInventoryEntity;
    }
    @Override
    public void setIsRenderingInventoryEntity(boolean isRenderingInventoryEntity) {
        EntityRenderDispatcherMixin.isRenderingInventoryEntity = isRenderingInventoryEntity;
    }
}
