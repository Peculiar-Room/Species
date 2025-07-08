package com.ninni.species.mixin_util;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

public interface LivingEntityAccess {
    boolean hasSnatched();
    void setSnatched(boolean snatched);
    boolean hasTanked();
    void setTanked(boolean tanked);

    EntityType getDisguisedEntityType();
    void setDisguisedEntityType(EntityType disguisedEntityType);
    LivingEntity getDisguisedEntity();
    void setDisguisedEntity(LivingEntity disguisedEntityType);
}
