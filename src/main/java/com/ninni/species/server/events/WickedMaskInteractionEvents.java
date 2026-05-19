package com.ninni.species.server.events;

import com.ninni.species.Species;
import com.ninni.species.server.disguise.ChainHeadRegistry;
import com.ninni.species.server.item.WickedMaskItem;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.entity.PartEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Routes sneak-right-click on multipart bodies and chain segments to {@code tryImprint}.
 *  Without this, {@link PartEntity} children skip {@code interactLivingEntity} entirely and
 *  chain segments forward {@code interact()} to the head before we see it. */
@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WickedMaskInteractionEvents {

    private WickedMaskInteractionEvents() {}

    @SubscribeEvent
    public static void onMaskInteract(PlayerInteractEvent.EntityInteractSpecific event) {
        // Server-only: cancelling client-side suppresses the packet, leaving the server unaware.
        if (event.getLevel().isClientSide) return;

        ItemStack stack = event.getItemStack();
        if (!(stack.getItem() instanceof WickedMaskItem)) return;

        Entity target = event.getTarget();
        Player player = event.getEntity();

        // Forge PartEntity → forward to parent LivingEntity.
        if (target instanceof PartEntity<?> part) {
            if (part.getParent() instanceof LivingEntity living
                    && WickedMaskItem.tryImprint(stack, living, player, event.getHand())) {
                event.setCancellationResult(InteractionResult.SUCCESS);
                event.setCanceled(true);
            }
            return;
        }

        // Chain segment that overrides interact() to forward to the head — pre-empt the forward
        // and capture the segment itself, identified via the registry populated by SegmentChainManager.
        if (target instanceof LivingEntity segment
                && ChainHeadRegistry.isSegment(segment.getType())
                && WickedMaskItem.tryImprint(stack, segment, player, event.getHand())) {
            event.setCancellationResult(InteractionResult.SUCCESS);
            event.setCanceled(true);
        }
    }
}
