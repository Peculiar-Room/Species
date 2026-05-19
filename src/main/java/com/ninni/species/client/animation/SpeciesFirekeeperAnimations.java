package com.ninni.species.client.animation;

import net.minecraft.client.animation.AnimationChannel;
import net.minecraft.client.animation.AnimationDefinition;
import net.minecraft.client.animation.Keyframe;
import net.minecraft.client.animation.KeyframeAnimations;

/** Firekeeper disguise animation suite swapped into Spawn's AWAKENING slot by {@code FirekeeperModelMixin}:
 *  the non-shifted ANCHOR_DOWN&rarr;SPIN_LOOP&rarr;BRAKE&rarr;RECOVER chain + {@link #AWAKENING_REVERSE} for the shifted dormant route. */
public final class SpeciesFirekeeperAnimations {

    // ---- Phase lengths ----
    private static final float ANCHOR_DOWN_LENGTH = 0.75F;
    private static final float SPIN_LOOP_LENGTH = 0.333F;
    private static final float BRAKE_SPIN_END = 0.333F;
    private static final float BRAKE_LENGTH = 0.667F;
    private static final float RECOVER_LENGTH = 0.8F;
    private static final float AWAKENING_REVERSE_LENGTH = 2.0F;

    // ---- ANCHOR_DOWN alternating side-by-side timing ----
    // leftArm sinks (0.0→0.15) → leftLeg retracts (0.15→0.25) → rightArm sinks (0.25→0.4)
    // → rightLeg retracts (0.4→0.5) → tail retracts (0.5→0.75, exact reverse of RECOVER release).
    private static final float ANCHOR_LEFT_ARM_END = 0.15F;
    private static final float ANCHOR_LEFT_LEG_START = 0.15F;
    private static final float ANCHOR_LEFT_LEG_END = 0.25F;
    private static final float ANCHOR_RIGHT_ARM_START = 0.25F;
    private static final float ANCHOR_RIGHT_ARM_END = 0.4F;
    private static final float ANCHOR_RIGHT_LEG_START = 0.4F;
    private static final float ANCHOR_RIGHT_LEG_END = 0.5F;
    /** Tail retraction = exact time-reverse of the RECOVER release (same 0.25s window, proportions
     *  inverted): rest → lift → back+thin → disappear. */
    private static final float ANCHOR_TAIL_START = 0.5F;
    private static final float ANCHOR_TAIL_LIFT = 0.6F;
    private static final float ANCHOR_TAIL_BACK = 0.7F;
    private static final float ANCHOR_TAIL_END = 0.75F;

    // ---- RECOVER sequential timing ----
    private static final float RECOVER_LEFT_LEG_END = 0.15F;
    private static final float RECOVER_RIGHT_LEG_START = 0.15F;
    private static final float RECOVER_RIGHT_LEG_END = 0.3F;
    private static final float RECOVER_ARMS_START = 0.3F;
    private static final float RECOVER_ARMS_END = 0.5F;
    /** Tail expel pattern matching AWAKENING: appear thin/back → grow → slide forward → rest. */
    private static final float RECOVER_TAIL_START = 0.5F;
    private static final float RECOVER_TAIL_THIN = 0.55F;
    private static final float RECOVER_TAIL_GROW = 0.65F;
    private static final float RECOVER_TAIL_END = 0.75F;

    // ---- Spikes appear/collapse via Spawn AWAKENING's exact proportions ----
    // Forward (RECOVER): XZ pops 0→1 over 0.0833s, then Y pops 0→1 over 0.125s, then hold.
    // Reverse (ANCHOR_DOWN): Y collapses 1→0 over 0.125s, then XZ collapses 1→0 over 0.0833s.
    private static final float RECOVER_SPIKES_START = 0.5F;
    private static final float RECOVER_SPIKES_XZ = 0.5833F;
    private static final float RECOVER_SPIKES_Y = 0.7083F;
    private static final float ANCHOR_SPIKES_START = 0.5417F;
    private static final float ANCHOR_SPIKES_Y = 0.6667F;

    // ---- Anchored pose constants ----
    /** Arms hang straight down at rest; lowering by 5 puts the arm-end exactly at feet level
     *  (Y_local 19 + 5 = 24 = feet). My previous -11 sank them 6 units below ground. */
    private static final float ARM_ANCHOR_POS_Y = -5F;
    /** Legs slide up into body during anchor. */
    private static final float LEG_RETRACT_POS_Y = 8F;
    /** Tail anchored values (matches AWAKENING's mid-expel held pose). */
    private static final float TAIL_LIFTED_Y = 7F;
    private static final float TAIL_BACK_Y = 5F;
    private static final float TAIL_BACK_Z = -8F;
    private static final float TAIL_THIN_Z = 0.2F;

    // ---- Arm elastic (lively anticipation/overshoot, mirroring the spikes/torso tilt) ----
    /** Units the arm travels UP (positive posVec Y) for the elastic move: a small hop before it
     *  plants in ANCHOR_DOWN, and an overshoot above rest before settling in RECOVER. */
    private static final float ARM_ELASTIC_UP = 2F;
    /** ANCHOR_DOWN hop peaks just before each arm plants (left plants 0.15, right plants 0.4). */
    private static final float ANCHOR_LEFT_ARM_RAISE = 0.06F;
    private static final float ANCHOR_RIGHT_ARM_RAISE = 0.31F;
    /** RECOVER arms settle to rest after overshooting above it at {@link #RECOVER_ARMS_END}. */
    private static final float RECOVER_ARMS_SETTLE = 0.65F;

    // ============================================================================================
    // Phase 1: ANCHOR_DOWN — sequential (arms → legs → tail)
    // ============================================================================================

    public static final AnimationDefinition ANCHOR_DOWN = AnimationDefinition.Builder
            .withLength(ANCHOR_DOWN_LENGTH)
            // leftArm sinks first (0 → 0.15).
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F,                  KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(ANCHOR_LEFT_ARM_RAISE, KeyframeAnimations.posVec(0F, ARM_ELASTIC_UP, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(ANCHOR_LEFT_ARM_END,   KeyframeAnimations.posVec(0F, ARM_ANCHOR_POS_Y, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_DOWN_LENGTH,    KeyframeAnimations.posVec(0F, ARM_ANCHOR_POS_Y, 0F),
                            AnimationChannel.Interpolations.LINEAR)))
            // rightArm sinks AFTER leftLeg retracts (0.25 → 0.4).
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F,                    KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(ANCHOR_RIGHT_ARM_START,  KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(ANCHOR_RIGHT_ARM_RAISE,  KeyframeAnimations.posVec(0F, ARM_ELASTIC_UP, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(ANCHOR_RIGHT_ARM_END,    KeyframeAnimations.posVec(0F, ARM_ANCHOR_POS_Y, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_DOWN_LENGTH,      KeyframeAnimations.posVec(0F, ARM_ANCHOR_POS_Y, 0F),
                            AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F,                  KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_LEFT_LEG_START, KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_LEFT_LEG_END,   KeyframeAnimations.scaleVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_DOWN_LENGTH,    KeyframeAnimations.scaleVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F,                  KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_LEFT_LEG_START, KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_LEFT_LEG_END,   KeyframeAnimations.posVec(0F, LEG_RETRACT_POS_Y, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_DOWN_LENGTH,    KeyframeAnimations.posVec(0F, LEG_RETRACT_POS_Y, 0F),
                            AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F,                   KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_RIGHT_LEG_START, KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_RIGHT_LEG_END,   KeyframeAnimations.scaleVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_DOWN_LENGTH,     KeyframeAnimations.scaleVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F,                   KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_RIGHT_LEG_START, KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_RIGHT_LEG_END,   KeyframeAnimations.posVec(0F, LEG_RETRACT_POS_Y, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_DOWN_LENGTH,     KeyframeAnimations.posVec(0F, LEG_RETRACT_POS_Y, 0F),
                            AnimationChannel.Interpolations.LINEAR)))
            // Tail retraction (0.5→0.75): EXACT time-reverse of RECOVER's release. POS CATMULLROM +
            // SCALE LINEAR mirror the release's interpolations so the motion reads identical
            // backwards: rest → lift → back+thin → disappear.
            .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F,              KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(ANCHOR_TAIL_START, KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(ANCHOR_TAIL_LIFT,  KeyframeAnimations.posVec(0F, TAIL_LIFTED_Y, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(ANCHOR_TAIL_END,   KeyframeAnimations.posVec(0F, TAIL_BACK_Y, TAIL_BACK_Z),
                            AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F,              KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_TAIL_LIFT,  KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_TAIL_BACK,  KeyframeAnimations.scaleVec(1F, 1F, TAIL_THIN_Z),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(ANCHOR_TAIL_END,   KeyframeAnimations.scaleVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR)))
            // Spikes collapse — reverse of Spawn AWAKENING's spikes channel (CATMULLROM elastic):
            // Y collapses first via the (1,0,1) intermediate, then XZ.
            .addAnimation("spikes", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F,                KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(ANCHOR_SPIKES_START, KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(ANCHOR_SPIKES_Y,     KeyframeAnimations.scaleVec(1F, 0F, 1F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(ANCHOR_DOWN_LENGTH,  KeyframeAnimations.scaleVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM)))
            // frontPlane handled by FirekeeperModelMixin @Inject TAIL (IDLE animates it every tick,
            // so a normal channel would additively stack to a mirrored xScale=-1).
            .build();

    // ============================================================================================
    // Phase 2: ANCHORED_SPIN_LOOP
    // ============================================================================================

    public static final AnimationDefinition ANCHORED_SPIN_LOOP = AnimationDefinition.Builder
            .withLength(SPIN_LOOP_LENGTH)
            .looping()
            .addAnimation("torso", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F,             KeyframeAnimations.degreeVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(SPIN_LOOP_LENGTH, KeyframeAnimations.degreeVec(-360F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F,             KeyframeAnimations.degreeVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(SPIN_LOOP_LENGTH, KeyframeAnimations.degreeVec(0F, 0F, 360F),
                            AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("leftArm", heldArmPosition(SPIN_LOOP_LENGTH))
            .addAnimation("rightArm", heldArmPosition(SPIN_LOOP_LENGTH))
            .addAnimation("leftLeg", heldLegScale(SPIN_LOOP_LENGTH))
            .addAnimation("leftLeg", heldLegPosition(SPIN_LOOP_LENGTH))
            .addAnimation("rightLeg", heldLegScale(SPIN_LOOP_LENGTH))
            .addAnimation("rightLeg", heldLegPosition(SPIN_LOOP_LENGTH))
            .addAnimation("tail", heldTailScale(SPIN_LOOP_LENGTH))
            .addAnimation("tail", heldTailPosition(SPIN_LOOP_LENGTH))
            .addAnimation("spikes", holdScale(KeyframeAnimations.scaleVec(0F, 0F, 0F), SPIN_LOOP_LENGTH))
            // frontPlane handled by FirekeeperModelMixin @Inject TAIL.
            .build();

    // ============================================================================================
    // Phase 3: BRAKE — one final spin + elastic hold
    // ============================================================================================

    public static final AnimationDefinition BRAKE = AnimationDefinition.Builder
            .withLength(BRAKE_LENGTH)
            .addAnimation("torso", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F,           KeyframeAnimations.degreeVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(BRAKE_SPIN_END, KeyframeAnimations.degreeVec(-360F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(BRAKE_LENGTH,   KeyframeAnimations.degreeVec(-360F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F,           KeyframeAnimations.degreeVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(BRAKE_SPIN_END, KeyframeAnimations.degreeVec(0F, 0F, 360F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(BRAKE_LENGTH,   KeyframeAnimations.degreeVec(0F, 0F, 360F),
                            AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("leftArm", heldArmPosition(BRAKE_LENGTH))
            .addAnimation("rightArm", heldArmPosition(BRAKE_LENGTH))
            .addAnimation("leftLeg", heldLegScale(BRAKE_LENGTH))
            .addAnimation("leftLeg", heldLegPosition(BRAKE_LENGTH))
            .addAnimation("rightLeg", heldLegScale(BRAKE_LENGTH))
            .addAnimation("rightLeg", heldLegPosition(BRAKE_LENGTH))
            .addAnimation("tail", heldTailScale(BRAKE_LENGTH))
            .addAnimation("tail", heldTailPosition(BRAKE_LENGTH))
            .addAnimation("spikes", holdScale(KeyframeAnimations.scaleVec(0F, 0F, 0F), BRAKE_LENGTH))
            // frontPlane handled by FirekeeperModelMixin @Inject TAIL.
            .build();

    // ============================================================================================
    // Phase 4: RECOVER — sequential restore styled after AWAKENING
    // ============================================================================================

    public static final AnimationDefinition RECOVER = AnimationDefinition.Builder
            .withLength(RECOVER_LENGTH)
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F,                 KeyframeAnimations.scaleVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_LEFT_LEG_END, KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_LENGTH,       KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F,                 KeyframeAnimations.posVec(0F, LEG_RETRACT_POS_Y, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_LEFT_LEG_END, KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_LENGTH,       KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR)))
            // Deploy kick — same as AWAKENING (leftLeg swings -27.5° Z as the foot drops out).
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F,                 KeyframeAnimations.degreeVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.05F,                KeyframeAnimations.degreeVec(0F, 0F, -27.5F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_LEFT_LEG_END, KeyframeAnimations.degreeVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_LENGTH,       KeyframeAnimations.degreeVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F,                    KeyframeAnimations.scaleVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_RIGHT_LEG_START, KeyframeAnimations.scaleVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_RIGHT_LEG_END,   KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_LENGTH,          KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F,                    KeyframeAnimations.posVec(0F, LEG_RETRACT_POS_Y, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_RIGHT_LEG_START, KeyframeAnimations.posVec(0F, LEG_RETRACT_POS_Y, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_RIGHT_LEG_END,   KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_LENGTH,          KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR)))
            // Deploy kick — same as AWAKENING (rightLeg swings +27.5° Z as the foot drops out).
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F,                    KeyframeAnimations.degreeVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_RIGHT_LEG_START, KeyframeAnimations.degreeVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.2F,                    KeyframeAnimations.degreeVec(0F, 0F, 27.5F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_RIGHT_LEG_END,   KeyframeAnimations.degreeVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_LENGTH,          KeyframeAnimations.degreeVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F,                KeyframeAnimations.posVec(0F, ARM_ANCHOR_POS_Y, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_ARMS_START,  KeyframeAnimations.posVec(0F, ARM_ANCHOR_POS_Y, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_ARMS_END,    KeyframeAnimations.posVec(0F, ARM_ELASTIC_UP, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_ARMS_SETTLE, KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_LENGTH,      KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F,                KeyframeAnimations.posVec(0F, ARM_ANCHOR_POS_Y, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_ARMS_START,  KeyframeAnimations.posVec(0F, ARM_ANCHOR_POS_Y, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_ARMS_END,    KeyframeAnimations.posVec(0F, ARM_ELASTIC_UP, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_ARMS_SETTLE, KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_LENGTH,      KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM)))
            .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F,               KeyframeAnimations.scaleVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_TAIL_START, KeyframeAnimations.scaleVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_TAIL_THIN,  KeyframeAnimations.scaleVec(1F, 1F, TAIL_THIN_Z),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_TAIL_GROW,  KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(RECOVER_LENGTH,     KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.LINEAR)))
            .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F,               KeyframeAnimations.posVec(0F, TAIL_BACK_Y, TAIL_BACK_Z),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_TAIL_START, KeyframeAnimations.posVec(0F, TAIL_BACK_Y, TAIL_BACK_Z),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_TAIL_GROW,  KeyframeAnimations.posVec(0F, TAIL_LIFTED_Y, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_TAIL_END,   KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_LENGTH,     KeyframeAnimations.posVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM)))
            // Spikes appear — forward Spawn AWAKENING spikes channel (CATMULLROM elastic): XZ pops
            // first via the (1,0,1) intermediate, then Y, then a brief hold so the curve overshoots.
            .addAnimation("spikes", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F,                 KeyframeAnimations.scaleVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_SPIKES_START, KeyframeAnimations.scaleVec(0F, 0F, 0F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_SPIKES_XZ,    KeyframeAnimations.scaleVec(1F, 0F, 1F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_SPIKES_Y,     KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(RECOVER_LENGTH,       KeyframeAnimations.scaleVec(1F, 1F, 1F),
                            AnimationChannel.Interpolations.CATMULLROM)))
            // frontPlane handled by FirekeeperModelMixin @Inject TAIL.
            .build();

    // ============================================================================================
    // Shifted route: AWAKENING_REVERSE — literal time-reverse of AWAKENING (T' = 2.0 - T)
    // ============================================================================================

    public static final AnimationDefinition AWAKENING_REVERSE = AnimationDefinition.Builder
            .withLength(AWAKENING_REVERSE_LENGTH)
            .addAnimation("head", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F, 0F, 360F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2917F, KeyframeAnimations.degreeVec(0F, 0F, 360F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.4167F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.625F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7917F, KeyframeAnimations.degreeVec(0F, 0F, 7.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("body", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5833F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7083F, KeyframeAnimations.posVec(0F, -7.33F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7917F, KeyframeAnimations.posVec(0F, -7.33F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.875F, KeyframeAnimations.posVec(0F, -11F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.25F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.625F, KeyframeAnimations.degreeVec(-20F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9583F, KeyframeAnimations.degreeVec(-20F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2083F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5417F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7083F, KeyframeAnimations.posVec(0F, 7F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.posVec(0F, 7F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2083F, KeyframeAnimations.posVec(0F, 5F, -8F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.9583F, KeyframeAnimations.posVec(0F, 5F, -8F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("tail", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.6667F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9167F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.125F, KeyframeAnimations.scaleVec(1F, 1F, 0.2F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.9583F, KeyframeAnimations.scaleVec(1F, 1F, 0.2F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.scaleVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("tailTip", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.625F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.8333F, KeyframeAnimations.scaleVec(1F, 1F, 0.2F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8333F, KeyframeAnimations.degreeVec(0F, 0F, 27.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.875F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.875F, KeyframeAnimations.posVec(0F, 8F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("rightLeg", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8333F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.875F, KeyframeAnimations.scaleVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.degreeVec(0F, 0F, -27.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7917F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7917F, KeyframeAnimations.posVec(0F, 8F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("leftLeg", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7917F, KeyframeAnimations.scaleVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("all", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.375F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4167F, KeyframeAnimations.posVec(1F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.4583F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5F, KeyframeAnimations.posVec(-1F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.5417F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.6667F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7083F, KeyframeAnimations.posVec(1F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7917F, KeyframeAnimations.posVec(-1F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.8333F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.125F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.1667F, KeyframeAnimations.posVec(-1F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.2083F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4167F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.4583F, KeyframeAnimations.posVec(1F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.625F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.7083F, KeyframeAnimations.posVec(0F, 1F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.75F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2083F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.5F, KeyframeAnimations.degreeVec(0F, 0F, -27.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.5833F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.0F, KeyframeAnimations.posVec(0F, 8F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.2083F, KeyframeAnimations.posVec(-5F, 8F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.375F, KeyframeAnimations.posVec(-13F, 8F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.9583F, KeyframeAnimations.posVec(-13F, 8F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("leftArm", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.9583F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.scaleVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.9167F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.2083F, KeyframeAnimations.degreeVec(0F, 0F, 27.5F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.2917F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.POSITION,
                    new Keyframe(0.0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.7083F, KeyframeAnimations.posVec(0F, 8F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.9167F, KeyframeAnimations.posVec(5F, 8F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.0833F, KeyframeAnimations.posVec(13F, 8F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.9583F, KeyframeAnimations.posVec(13F, 8F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.posVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("rightArm", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(1.9583F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.scaleVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("spikes", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.625F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.7917F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(1.9167F, KeyframeAnimations.scaleVec(1F, 0F, 1F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(2.0F, KeyframeAnimations.scaleVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("frontPlane", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.75F, KeyframeAnimations.scaleVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(0.7917F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.LINEAR)
            ))
            .addAnimation("torso", new AnimationChannel(AnimationChannel.Targets.ROTATION,
                    new Keyframe(0.0F, KeyframeAnimations.degreeVec(-360F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.2083F, KeyframeAnimations.degreeVec(-360F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.5417F, KeyframeAnimations.degreeVec(-360F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM),
                    new Keyframe(0.875F, KeyframeAnimations.degreeVec(0F, 0F, 0F), AnimationChannel.Interpolations.CATMULLROM)
            ))
            .addAnimation("moltenOverlay", new AnimationChannel(AnimationChannel.Targets.SCALE,
                    new Keyframe(0.0F, KeyframeAnimations.scaleVec(0F, 0F, 0F), AnimationChannel.Interpolations.LINEAR),
                    new Keyframe(2.0F, KeyframeAnimations.scaleVec(1F, 1F, 1F), AnimationChannel.Interpolations.LINEAR)
            ))
            .build();

    // ============================================================================================
    // Helpers
    // ============================================================================================

    private static AnimationChannel heldArmPosition(float length) {
        return holdPosition(KeyframeAnimations.posVec(0F, ARM_ANCHOR_POS_Y, 0F), length);
    }
    private static AnimationChannel heldLegScale(float length) {
        return holdScale(KeyframeAnimations.scaleVec(0F, 0F, 0F), length);
    }
    private static AnimationChannel heldLegPosition(float length) {
        return holdPosition(KeyframeAnimations.posVec(0F, LEG_RETRACT_POS_Y, 0F), length);
    }
    private static AnimationChannel heldTailScale(float length) {
        return holdScale(KeyframeAnimations.scaleVec(0F, 0F, 0F), length);
    }
    private static AnimationChannel heldTailPosition(float length) {
        return holdPosition(KeyframeAnimations.posVec(0F, TAIL_BACK_Y, TAIL_BACK_Z), length);
    }

    private static AnimationChannel holdScale(org.joml.Vector3f value, float length) {
        return new AnimationChannel(AnimationChannel.Targets.SCALE,
                new Keyframe(0.0F,   value, AnimationChannel.Interpolations.LINEAR),
                new Keyframe(length, value, AnimationChannel.Interpolations.LINEAR));
    }

    private static AnimationChannel holdPosition(org.joml.Vector3f value, float length) {
        return new AnimationChannel(AnimationChannel.Targets.POSITION,
                new Keyframe(0.0F,   value, AnimationChannel.Interpolations.LINEAR),
                new Keyframe(length, value, AnimationChannel.Interpolations.LINEAR));
    }

    private SpeciesFirekeeperAnimations() {}
}
