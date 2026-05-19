package com.ninni.species.client.events;

import com.ninni.species.Species;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.AreaEffectCloud;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.level.Explosion;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.EntityMobGriefingEvent;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.level.ExplosionEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Suppresses world side-effects from disguise ticks (block break, attack, AOE/projectile spawn) by
 * tagging disguises with {@link #DISGUISE_TAG} and cancelling the matching Forge events.
 */
@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WickedMaskDisguiseEvents {

    public static final String DISGUISE_TAG = "species_wicked_mask_disguise";

    public static boolean isDisguise(Entity entity) {
        return entity != null && entity.getTags().contains(DISGUISE_TAG);
    }

    @SubscribeEvent
    public static void onMobGriefing(EntityMobGriefingEvent event) {
        if (isDisguise(event.getEntity())) {
            event.setResult(Event.Result.DENY);
        }
    }

    @SubscribeEvent
    public static void onLivingAttack(LivingAttackEvent event) {
        Entity attacker = event.getSource().getEntity();
        if (isDisguise(attacker) || isDisguise(event.getSource().getDirectEntity())) {
            event.setCanceled(true);
        }
    }

    /** Cancels disguise-driven block destruction (some mobs bypass the mob-griefing gameRule but still fire this hook). */
    @SubscribeEvent
    public static void onLivingDestroyBlock(LivingDestroyBlockEvent event) {
        if (isDisguise(event.getEntity())) {
            event.setCanceled(true);
        }
    }

    /** Cancels explosions sourced by a disguise body (e.g. a boss-phase transition would otherwise detonate at the wearer). */
    @SubscribeEvent
    public static void onExplosion(ExplosionEvent.Start event) {
        Explosion explosion = event.getExplosion();
        if (isDisguise(explosion.getDirectSourceEntity()) || isDisguise(explosion.getIndirectSourceEntity())) {
            event.setCanceled(true);
        }
    }

    /** Cancels AOE clouds / projectiles spawned by a disguise's tick. */
    @SubscribeEvent
    public static void onEntityJoinLevel(EntityJoinLevelEvent event) {
        Entity entity = event.getEntity();
        if (entity instanceof AreaEffectCloud cloud) {
            if (isDisguise(cloud.getOwner())) {
                event.setCanceled(true);
            }
        } else if (entity instanceof Projectile projectile) {
            if (isDisguise(projectile.getOwner())) {
                event.setCanceled(true);
            }
        }
    }
}
