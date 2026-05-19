package com.ninni.species.server.disguise;

import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.server.disguise.behaviors.AmSealBehavior;
import com.ninni.species.server.disguise.behaviors.BatBehavior;
import com.ninni.species.server.disguise.behaviors.BewereagerSpecialActionBehavior;
import com.ninni.species.server.disguise.behaviors.BirtFlyAnimationBehavior;
import com.ninni.species.server.disguise.behaviors.BirtSignalSpecialActionBehavior;
import com.ninni.species.server.disguise.behaviors.CliffHangerBehavior;
import com.ninni.species.server.disguise.behaviors.CruncherSpecialActionBehavior;
import com.ninni.species.server.disguise.behaviors.LeafHangerBehavior;
import com.ninni.species.server.disguise.behaviors.EnderDragonBehavior;
import com.ninni.species.server.disguise.behaviors.FlightPitchOverrideBehavior;
import com.ninni.species.server.disguise.behaviors.GhoulSpecialActionBehavior;
import com.ninni.species.server.disguise.behaviors.GooberSpecialActionBehavior;
import com.ninni.species.server.disguise.behaviors.HippogryphBehavior;
import com.ninni.species.server.disguise.behaviors.HullbackBehavior;
import com.ninni.species.server.disguise.behaviors.IafDragonBehavior;
import com.ninni.species.server.disguise.behaviors.IafFlightRollOverrideBehavior;
import com.ninni.species.server.disguise.behaviors.IgnisPhaseCycleBehavior;
import com.ninni.species.server.disguise.behaviors.LuxtructosaurusBehavior;
import com.ninni.species.server.disguise.behaviors.MammutilationSpecialActionBehavior;
import com.ninni.species.server.disguise.behaviors.QuakeSpecialActionBehavior;
import com.ninni.species.server.disguise.behaviors.SauropodBehavior;
import com.ninni.species.server.disguise.behaviors.SpawnBoobyBehavior;
import com.ninni.species.server.disguise.behaviors.SpawnBoobyDanceBehavior;
import com.ninni.species.server.disguise.behaviors.SpawnClamOpenBehavior;
import com.ninni.species.server.disguise.behaviors.SpawnCrabBehavior;
import com.ninni.species.server.disguise.behaviors.SpawnDodoPeckBehavior;
import com.ninni.species.server.disguise.behaviors.SpawnFirekeeperSpecialActionBehavior;
import com.ninni.species.server.disguise.behaviors.SpawnSealBehavior;
import com.ninni.species.server.disguise.behaviors.SpawnStickbugBehavior;
import com.ninni.species.server.disguise.behaviors.SpawnStickbugDanceBehavior;
import com.ninni.species.server.disguise.behaviors.StackatickStackBehavior;
import com.ninni.species.server.disguise.behaviors.StraightenViaBridgeBehavior;
import com.ninni.species.server.disguise.behaviors.SubterranodonBackwardFlapBehavior;
import com.ninni.species.server.disguise.behaviors.TreeperSpecialActionBehavior;
import com.ninni.species.server.disguise.behaviors.TremorsaurusBehavior;
import com.ninni.species.server.disguise.behaviors.TriopsBehavior;
import com.ninni.species.server.disguise.behaviors.WickedSpecialActionBehavior;
import com.ninni.species.server.disguise.behaviors.WraptorSpecialActionBehavior;
import com.ninni.species.server.disguise.dsl.AutoFlightSyncBehavior;
import com.ninni.species.server.disguise.dsl.WalkAnimationDampingBehavior;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;

/**
 * Registers Species' built-in {@link DisguiseBehavior}s during setup; modded entries use
 * {@link #registerIfPresent} so soft-deps register only when the entity type exists.
 */
public final class DisguiseBehaviors {

    private DisguiseBehaviors() {}

    public static void registerDefaults() {
        // Force class load so static initialisers of self-registering global behaviors run.
        try {
            Class.forName(AutoFlightSyncBehavior.class.getName());
        } catch (ClassNotFoundException ignored) {}

        // Segment-chain managers self-register as global DisguiseBehaviors + sub-entity providers.
        // Each gates onCreated/preTick/onDestroyed on its own entity-type id, so ordering doesn't matter.
        GumWormSegmentManager.INSTANCE.register();
        AnacondaSegmentManager.INSTANCE.register();
        BoneSerpentSegmentManager.INSTANCE.register();
        CentipedeSegmentManager.INSTANCE.register();
        VoidWormSegmentManager.INSTANCE.register();
        MurmurPairManager.INSTANCE.register();

        // Vanilla
        DisguiseBehaviorRegistry.register(EntityType.ENDER_DRAGON, EnderDragonBehavior.INSTANCE);
        // Bat: ceiling/flying/ground state machine + smooth Y-offset interpolation between states.
        DisguiseBehaviorRegistry.register(EntityType.BAT, BatBehavior.INSTANCE);

        // Species-internal mobs: special-action keybind hooks. Each toggles or fires a
        // signature animation on press — see the per-behavior class for the exact pose cycle.
        registerIfPresent("species", "quake", QuakeSpecialActionBehavior.INSTANCE);
        registerIfPresent("species", "ghoul", GhoulSpecialActionBehavior.INSTANCE);
        registerIfPresent("species", "goober", GooberSpecialActionBehavior.INSTANCE);
        registerIfPresent("species", "cruncher", CruncherSpecialActionBehavior.INSTANCE);
        registerIfPresent("species", "treeper", TreeperSpecialActionBehavior.INSTANCE);
        registerIfPresent("species", "bewereager", BewereagerSpecialActionBehavior.INSTANCE);
        registerIfPresent("species", "wraptor", WraptorSpecialActionBehavior.INSTANCE);
        registerIfPresent("species", "wicked", WickedSpecialActionBehavior.INSTANCE);
        registerIfPresent("species", "mammutilation", MammutilationSpecialActionBehavior.INSTANCE);
        registerIfPresent("species", "stackatick", StackatickStackBehavior.INSTANCE);
        // Birt: fly-pose sync (drives flyingAnimationState while wearer is airborne) + signal keybind
        // (antenna-glow pulse via the existing antennaTicks/BIRT_SEARCH surface).
        composeStackIfPresent("species", "birt",
                BirtFlyAnimationBehavior.INSTANCE,
                BirtSignalSpecialActionBehavior.INSTANCE);
        // Cliff Hanger: clings upside-down to ceilings — forces the ATTACHED flag from the wearer's
        // ceiling state, and the model self-flips about its origin to seat the body on the ceiling.
        registerIfPresent("species", "cliff_hanger", CliffHangerBehavior.INSTANCE);
        // Leaf Hanger: in water, dangles a lily pad up to the surface and turns the body translucent,
        // matching the native mob's bait pose.
        registerIfPresent("species", "leaf_hanger", LeafHangerBehavior.INSTANCE);

        // Modded — soft-dep registration. Only register if the entity type exists.
        registerIfPresent("whaleborne", "hullback", HullbackBehavior.INSTANCE);

        // Ice and Fire dragons — every dragon family share EntityDragonBase, so the
        // same behavior plugs in for fire/ice/lightning variants.
        registerIfPresent("iceandfire", "fire_dragon", IafDragonBehavior.INSTANCE);
        registerIfPresent("iceandfire", "ice_dragon", IafDragonBehavior.INSTANCE);
        registerIfPresent("iceandfire", "lightning_dragon", IafDragonBehavior.INSTANCE);
        // Roll/pitch override for IFChainBuffer-backed flyers (Amphithere is composed below).
        composeIfPresent("iceandfire", "fire_dragon", IafFlightRollOverrideBehavior.INSTANCE);
        composeIfPresent("iceandfire", "ice_dragon", IafFlightRollOverrideBehavior.INSTANCE);
        composeIfPresent("iceandfire", "lightning_dragon", IafFlightRollOverrideBehavior.INSTANCE);

        // IaF flight mobs: AutoFlightSync handles flag-sync globally; only the rotation lock
        // remains type-specific. Hippogryph keeps its bespoke entry for the airBorneCounter clamp.
        registerIfPresent("iceandfire", "amphithere", StraightenViaBridgeBehavior.INSTANCE);
        composeIfPresent("iceandfire", "amphithere", IafFlightRollOverrideBehavior.INSTANCE);
        registerIfPresent("iceandfire", "hippogryph", HippogryphBehavior.INSTANCE);
        registerIfPresent("iceandfire", "myrmex_royal", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "stymphalian_bird", StraightenViaBridgeBehavior.INSTANCE);

        // IaF entities needing only the rotation lock: each has unconditional faceTarget in setupAnim
        // that distorts head/neck in inventory preview without it. Straighten flag is a no-op for these
        // models but the rotation lock alone is sufficient.
        registerIfPresent("iceandfire", "cockatrice", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "cyclops", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "gorgon", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "troll", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "siren", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "ghost", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "pixie", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "myrmex_worker", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "myrmex_soldier", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "myrmex_sentinel", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "myrmex_queen", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "dread_thrall", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "dread_ghoul", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "dread_lich", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("iceandfire", "dread_knight", StraightenViaBridgeBehavior.INSTANCE);

        // AC sauropod-class. Atlatitan: plain walk fix. Luxtructosaurus: same + ENRAGED-flag
        // suppressor (tick spawns fire-spit particles and glow when isEnraged()=true, leaking into the disguise).
        registerIfPresent("alexscaves", "atlatitan", SauropodBehavior.INSTANCE);
        registerIfPresent("alexscaves", "luxtructosaurus", LuxtructosaurusBehavior.INSTANCE);

        // AC Subterranodon + Vesper: AutoFlightSync handles flight + ceiling-cling flags;
        // rotation lock + body pitch/roll override (paired-prev cache in postTick, since the
        // native formulas read near-zero deltaMovement.y and yaw-delta on a goalless disguise).
        // Subterranodon also gets a backward-flap override so reverse flight shows wing-beats.
        composeStackIfPresent("alexscaves", "subterranodon",
                StraightenViaBridgeBehavior.INSTANCE,
                new FlightPitchOverrideBehavior(1F),
                SubterranodonBackwardFlapBehavior.INSTANCE);
        composeStackIfPresent("alexscaves", "vesper",
                StraightenViaBridgeBehavior.INSTANCE,
                new FlightPitchOverrideBehavior(1F));

        // AC inventory rotation lock + straighten-bridge flag (one behavior, both effects).
        registerIfPresent("alexscaves", "hullbreaker", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("alexscaves", "tremorzilla", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("alexscaves", "gossamer_worm", StraightenViaBridgeBehavior.INSTANCE);
        // Additional AC mobs with unconditional faceTarget in setupAnim.
        // Models verified: MagnetronModel:79, RaycatModel:132, TeletorModel:140,
        // UnderzealotModel:194, VallumraptorModel:586.
        registerIfPresent("alexscaves", "magnetron", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("alexscaves", "raycat", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("alexscaves", "teletor", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("alexscaves", "underzealot", StraightenViaBridgeBehavior.INSTANCE);
        registerIfPresent("alexscaves", "vallumraptor", StraightenViaBridgeBehavior.INSTANCE);
        // Gloomoth: FLYING flag handled by AutoFlightSync. Pitch + roll come from the generic
        // flier behavior so they survive aiStep's rotation-delta zeroing.
        registerIfPresent("alexscaves", "gloomoth", new FlightPitchOverrideBehavior(1F));
        // Forsaken: unconditional faceTarget(netHeadYaw, headPitch, 1, neck, skull) in
        // ForsakenModel sinks the skull into the chest during inventory preview.
        // Rotation-lock forces netHeadYaw→0; straighten flag is a no-op (no field).
        registerIfPresent("alexscaves", "forsaken", StraightenViaBridgeBehavior.INSTANCE);
        // Tremorsaurus: bridge fix + dampened walkSpeed=0.8F leg cycle.
        registerIfPresent("alexscaves", "tremorsaurus", TremorsaurusBehavior.INSTANCE);

        // AlexsMobs: ModelTriops rotates the body -180° around Z when on land. Pin onLandProgress=0
        // so the disguise stays in swim pose; otherwise the model flips belly-up at the wearer's feet.
        registerIfPresent("alexsmobs", "triops", TriopsBehavior.INSTANCE);

        // AM Anaconda: ModelAnaconda's head adds Mth.sin(limbSwing) * 2 * limbSwingAmount lateral
        // slide → wobble at player walk speed. Zeroing walkAnim collapses that term; jaw idle +
        // head-turn (driven by ageInTicks/netHeadYaw) are unaffected.
        registerIfPresent("alexsmobs", "anaconda", new WalkAnimationDampingBehavior(0.0F, 0.0F));

        // AM Seal: pin swimAngle to zero (stale post-water lean) and force basking/digging
        // off while the wearer is walking so idle poses don't blend into the locomotion rig.
        registerIfPresent("alexsmobs", "seal", AmSealBehavior.INSTANCE);

        // Spawn (soft-dep) — fixes for goal-stripped animation triggers.
        registerIfPresent("spawn", "seal", SpawnSealBehavior.INSTANCE);
        // Booby: flap/glide alternation + flight roll/pitch override + dance keybind. Footprints come
        // from Spawn's own handler via the disguise tick-event re-fire in LivingEntityMixin.
        composeStackIfPresent("spawn", "booby",
                SpawnBoobyBehavior.INSTANCE,
                new FlightPitchOverrideBehavior(1F),
                SpawnBoobyDanceBehavior.INSTANCE);
        registerIfPresent("spawn", "coastal_crab", SpawnCrabBehavior.INSTANCE);
        // Stickbug: form-toggle on crouch (mob ↔ stick) + dance keybind.
        composeStackIfPresent("spawn", "stickbug",
                SpawnStickbugBehavior.INSTANCE,
                SpawnStickbugDanceBehavior.INSTANCE);
        registerIfPresent("spawn", "clam", SpawnClamOpenBehavior.INSTANCE);
        registerIfPresent("spawn", "dodo", SpawnDodoPeckBehavior.INSTANCE);
        // Firekeeper: keybind plays AWAKENING + modulates molten overlay (rock <-> glow over 6 spins).
        registerIfPresent("spawn", "firekeeper", SpawnFirekeeperSpecialActionBehavior.INSTANCE);

        // Cataclysm bosses with multi-stage models: shift+keybind cycles phases.
        registerIfPresent("cataclysm", "ignis", IgnisPhaseCycleBehavior.INSTANCE);
    }

    /**
     * Soft-dep registration that REPLACES any prior entry. Use {@link #composeIfPresent} when
     * multiple behaviors must coexist on the same entity-type — {@code register} alone overwrites.
     */
    private static boolean registerIfPresent(String namespace, String path, DisguiseBehavior behavior) {
        ResourceLocation id = new ResourceLocation(namespace, path);
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
        if (type != null) {
            DisguiseBehaviorRegistry.register(type, behavior);
            return true;
        }
        return false;
    }

    /** Soft-dep registration that STACKS on top of any prior entry (composes via wrapper). */
    private static boolean composeIfPresent(String namespace, String path, DisguiseBehavior behavior) {
        ResourceLocation id = new ResourceLocation(namespace, path);
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
        if (type != null) {
            DisguiseBehaviorRegistry.compose(type, behavior);
            return true;
        }
        return false;
    }

    /** Soft-dep stacked composition: applies all behaviors in order onto the same entity type. */
    private static boolean composeStackIfPresent(String namespace, String path, DisguiseBehavior... behaviors) {
        ResourceLocation id = new ResourceLocation(namespace, path);
        EntityType<?> type = BuiltInRegistries.ENTITY_TYPE.getOptional(id).orElse(null);
        if (type == null) return false;
        for (DisguiseBehavior behavior : behaviors) {
            DisguiseBehaviorRegistry.compose(type, behavior);
        }
        return true;
    }
}
