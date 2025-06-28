package com.ninni.species.client.model.mob_heads;

import net.minecraft.client.model.Model;
import net.minecraft.client.renderer.RenderType;

public abstract class MobHeadModelBase extends Model {
    public MobHeadModelBase() {
        super(RenderType::entityTranslucent);
    }

    public abstract void setupAnim(float p_170950_, float p_170951_, float p_170952_);
}