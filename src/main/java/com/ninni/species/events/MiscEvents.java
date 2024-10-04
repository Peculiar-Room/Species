package com.ninni.species.events;

import com.ninni.species.Species;
import com.ninni.species.data.CruncherPelletManager;
import com.ninni.species.data.GooberGooManager;
import com.ninni.species.entity.BirtEggEntity;
import com.ninni.species.init.SpeciesItems;
import net.minecraft.Util;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.AbstractProjectileDispenseBehavior;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class MiscEvents {

    @SubscribeEvent
    public void onTagsUpdated(TagsUpdatedEvent event) {
        DispenserBlock.registerBehavior(SpeciesItems.BIRT_EGG.get(), new AbstractProjectileDispenseBehavior() {
            @Override
            protected Projectile getProjectile(Level world, Position position, ItemStack stack) {
                return Util.make(new BirtEggEntity(world, position.x(), position.y(), position.z()), entity -> entity.setItem(stack));
            }
        });
    }

    @SubscribeEvent
    public void register(AddReloadListenerEvent event) {
        event.addListener(new GooberGooManager());
        event.addListener(new CruncherPelletManager());
    }

}
