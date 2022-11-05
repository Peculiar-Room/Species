package com.ninni.species.world.gen.features;

import com.ninni.species.Species;
import com.ninni.species.mixin.TreeDecoratorTypeAccessor;
import net.minecraft.util.Identifier;
import net.minecraft.world.gen.treedecorator.TreeDecoratorType;

public class SpeciesTreeDecorators {
    public static void init() { }

    public static final TreeDecoratorType<BirtDwellingLogDecorator> BIRT_DWELLING = TreeDecoratorTypeAccessor.callRegister(new Identifier(Species.MOD_ID, "birt_dwelling").toString(), BirtDwellingLogDecorator.CODEC);

}
