package com.ninni.species;

import net.minecraft.ChatFormatting;

public enum SpeciesDevelopers {
    NINNI(" Ninni", ChatFormatting.DARK_AQUA),
    REDA(" Floofhips", ChatFormatting.RED),
    NOON(" Noonyeyz", ChatFormatting.AQUA),
    BORNHULU(" Bornhulu", ChatFormatting.GREEN),
    GLADOS(" GLaDOS edition", ChatFormatting.LIGHT_PURPLE);
    //WADOO(" (Guest Artist) Wadoo", ChatFormatting.DARK_GRAY)

    private final String name;
    private final ChatFormatting formatting;

    SpeciesDevelopers(String name, ChatFormatting formatting) {
        this.name = name;
        this.formatting = formatting;
    }

    public String getName() {
        return name;
    }
    public ChatFormatting getFormatting() {
        return formatting;
    }
}
