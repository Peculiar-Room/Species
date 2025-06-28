package com.ninni.species.server.block.property;

import net.minecraft.util.StringRepresentable;

public enum BoneSpikeThickness implements StringRepresentable
{
    TIP("tip"),
    MIDDLE("middle"),
    BASE("base");

    private final String name;

    BoneSpikeThickness(String string2) {
        this.name = string2;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String getSerializedName() {
        return this.name;
    }
}
