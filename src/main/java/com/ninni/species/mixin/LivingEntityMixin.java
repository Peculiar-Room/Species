package com.ninni.species.mixin;

import com.ninni.species.server.entity.util.CustomDeathParticles;
import com.ninni.species.mixin_util.LivingEntityAccess;
import com.ninni.species.server.packet.SnatchedPacket;
import com.ninni.species.server.packet.TankedPacket;
import com.ninni.species.registry.*;
import com.ninni.species.server.disguise.DisguiseTickContext;
import com.ninni.species.server.disguise.DisguiseLogging;
import com.ninni.species.server.disguise.util.BehaviorHooks;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.PacketDistributor;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.Optional;


@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LivingEntityAccess {
    @Shadow public abstract boolean hasEffect(MobEffect effect);
    @Shadow @Nullable public abstract MobEffectInstance getEffect(MobEffect p_21125_);
    @Shadow public abstract RandomSource getRandom();
    @Shadow public abstract ItemStack getItemBySlot(EquipmentSlot p_21127_);
    @OnlyIn(Dist.CLIENT)
    private @Unique boolean snatched;
    @OnlyIn(Dist.CLIENT)
    private @Unique boolean tanked;
    private @Unique EntityType disguisedEntityType;
    private @Unique LivingEntity disguisedEntity;
    private @Unique String lastDisguiseId;
    private @Unique CompoundTag lastDisguiseTag;

    /** Snapshot pre-baseTick rotation deltas; baseTick zeroes them and Citadel chain buffers
     *  (IaF dragon tail/neck) need the delta to survive into post-super.tick logic. */
    private @Unique float species$snapshotYBodyRotO;
    private @Unique float species$snapshotYHeadRotO;
    private @Unique float species$snapshotYRotO;
    private @Unique float species$snapshotXRotO;
    private @Unique boolean species$shouldRestoreRotationDelta;

    public LivingEntityMixin(EntityType<?> p_19870_, Level p_19871_) {
        super(p_19870_, p_19871_);
    }


    @Inject(method = "baseTick", at = @At("HEAD"))
    private void species$snapshotRotationDelta(CallbackInfo ci) {
        species$shouldRestoreRotationDelta = false;
        LivingEntity self = (LivingEntity) (Object) this;
        if (!self.getTags().contains(com.ninni.species.client.events.WickedMaskDisguiseEvents.DISGUISE_TAG)) return;
        try {
            com.ninni.species.api.disguise.DisguiseBehavior behavior =
                    com.ninni.species.server.disguise.DisguiseBehaviorRegistry.get(self);
            if (!behavior.preserveRotationDeltaInAiStep()) return;
        } catch (Throwable ignored) {
            return;
        }
        species$shouldRestoreRotationDelta = true;
        species$snapshotYBodyRotO = self.yBodyRotO;
        species$snapshotYHeadRotO = self.yHeadRotO;
        species$snapshotYRotO = self.yRotO;
        species$snapshotXRotO = self.xRotO;
    }

    @Inject(method = "baseTick", at = @At("RETURN"))
    private void species$restoreRotationDelta(CallbackInfo ci) {
        if (!species$shouldRestoreRotationDelta) return;
        species$shouldRestoreRotationDelta = false;
        LivingEntity self = (LivingEntity) (Object) this;
        self.yBodyRotO = species$snapshotYBodyRotO;
        self.yHeadRotO = species$snapshotYHeadRotO;
        self.yRotO = species$snapshotYRotO;
        self.xRotO = species$snapshotXRotO;
    }

    @Inject(method = "tickEffects", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/LivingEntity;updateGlowingStatus()V", ordinal = 0))
    private void onStatusEffectChange(CallbackInfo ci) {
        SpeciesNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new SnatchedPacket(this.getId(), this.hasEffect(SpeciesStatusEffects.SNATCHED.get())));
        SpeciesNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> this), new TankedPacket(this.getId(), this.hasEffect(SpeciesStatusEffects.TANKED.get())));
    }

    @Inject(method = "tickEffects", at = @At("HEAD"))
    public void applySpeciesEffects(CallbackInfo ci) {
        Level level = this.level();

        if (this.hasEffect(SpeciesStatusEffects.GUT_FEELING.get()) ) {
            if (this.getEffect(SpeciesStatusEffects.GUT_FEELING.get()).getDuration() < 20 * 60 * 5) {
                if (this.getRandom().nextInt(200) == 0) this.playSound(SpeciesSoundEvents.GUT_FEELING_ROAR.get(), 0.2f, 0);
            }
            else {
                if (this.getRandom().nextInt(800) == 0) this.playSound(SpeciesSoundEvents.GUT_FEELING_ROAR.get(), 0.2f, 0);
            }
        }

        if (this.hasEffect(SpeciesStatusEffects.BIRTD.get()) && level instanceof ServerLevel world) {
            if (this.tickCount % 10 == 1) {
                world.sendParticles(SpeciesParticles.BIRTD.get(), this.getX(), this.getEyeY() + 0.5F, this.getZ() - 0.5, 1,0, 0, 0, 0);
            }
        }

    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick(CallbackInfo ci) {
        // Fast-path: skip head-slot lookup and tag parsing for non-wearers.
        // The lastDisguiseId check ensures entry to the slow path on the first tick
        // after a mask is equipped (head slot non-empty, lastDisguiseId still null).
        ItemStack headItem = this.getItemBySlot(EquipmentSlot.HEAD);
        if (headItem.isEmpty() && this.lastDisguiseId == null
                && this.getDisguisedEntity() == null
                && this.getDisguisedEntityType() == null) {
            return;
        }
        CompoundTag tag = headItem.getTag();
        String capturedId = (headItem.is(SpeciesItems.WICKED_MASK.get()) && tag != null && tag.contains("id"))
                ? tag.getString("id") : null;

        if (capturedId == null) {
            if (this.getDisguisedEntity() != null || this.getDisguisedEntityType() != null) {
                species$cleanupOldDisguise(this.getDisguisedEntity());
                this.setDisguisedEntity(null);
                this.setDisguisedEntityType(null);
                this.lastDisguiseId = null;
                this.lastDisguiseTag = null;
                com.ninni.species.server.disguise.DisguiseLifecycleTracker.clear((LivingEntity) (Object) this);
            }
            return;
        }

        // Segment-to-head redirect: chain segments imprint with both ids; the config flag picks
        // which one drives the disguise. lastDisguiseId tracks the effective id, so toggling the
        // config triggers reinstantiation automatically.
        String currentId = capturedId;
        if (tag != null && tag.contains("species:head_id")) {
            boolean redirect;
            try { redirect = com.ninni.species.registry.SpeciesConfig.REDIRECT_SEGMENT_TO_HEAD.get(); }
            catch (Throwable ignored) { redirect = true; }
            if (redirect) currentId = tag.getString("species:head_id");
        }

        // Imprint NBT that actually configures the disguise — empty on redirect: a fresh segment
        // head must not load the imprinted save data. Rebuild also when this NBT changes for an
        // unchanged type id, so swapping between two masks of the same entity type but different
        // captured state (variant/phase/dimension texture, ...) refreshes the model.
        boolean redirected = !currentId.equals(capturedId);
        CompoundTag effectiveTag = (redirected || tag == null) ? new CompoundTag() : tag;

        if (!currentId.equals(this.lastDisguiseId) || this.getDisguisedEntity() == null
                || !effectiveTag.equals(this.lastDisguiseTag)) {
            species$cleanupOldDisguise(this.getDisguisedEntity());
            Optional<EntityType<?>> entityType = EntityType.byString(currentId);
            if (entityType.isPresent()) {
                Entity rawEntity = entityType.get().create(this.level());
                if (rawEntity instanceof LivingEntity living) {
                    // A non-null carryover (matched by id) means the body is being REBUILT for a
                    // still-active disguise (e.g. dimension change): load its runtime NBT and route
                    // to onBodyRebuilt so one-shot onCreated setup (intros/folds) isn't replayed.
                    CompoundTag rebuildNbt = com.ninni.species.server.disguise.DisguiseLifecycleTracker
                            .takeRebuildNbt((LivingEntity) (Object) this, currentId);
                    boolean rebuild = rebuildNbt != null;
                    species$configureNewDisguise(living, rebuild ? rebuildNbt : effectiveTag, rebuild);
                    this.setDisguisedEntity(living);
                    this.setDisguisedEntityType(entityType.get());
                    this.lastDisguiseId = currentId;
                    this.lastDisguiseTag = effectiveTag.copy();
                    species$wireNewDisguise(living);
                } else {
                    this.setDisguisedEntity(null);
                    this.setDisguisedEntityType(null);
                    this.lastDisguiseId = currentId;
                    this.lastDisguiseTag = effectiveTag.copy();
                }
            } else {
                this.setDisguisedEntity(null);
                this.setDisguisedEntityType(null);
                this.lastDisguiseId = currentId;
                this.lastDisguiseTag = effectiveTag.copy();
            }
        }

        tickDisguise();
    }

    @Unique
    private void tickDisguise() {
        LivingEntity disguise = this.getDisguisedEntity();
        if (disguise == null || disguise.isRemoved()) return;

        ItemStack disguiseHead = disguise.getItemBySlot(EquipmentSlot.HEAD);
        if (disguiseHead.is(SpeciesItems.WICKED_MASK.get())) return;

        LivingEntity self = (LivingEntity) (Object) this;

        // Armor stand statue: skip behavior + disguise.tick; HeadPose drives rotation.
        if (self instanceof net.minecraft.world.entity.decoration.ArmorStand armorStand) {
            tickStaticDisguiseFromArmorStand(armorStand, disguise);
            return;
        }

        // Snapshot wearer pos to isolate disguise's aiStep pushEntities impulse.
        double wearerX = self.getX();
        double wearerY = self.getY();
        double wearerZ = self.getZ();
        Vec3 wearerDelta = self.getDeltaMovement();

        com.ninni.species.api.disguise.DisguiseBehavior behavior = com.ninni.species.server.disguise.DisguiseBehaviorRegistry.get(disguise);
        float yawOffset = behavior.yawOffset(disguise);
        boolean shouldApplyXRot = behavior.shouldApplyXRot(self, disguise, false);
        float xRotForDisguise = shouldApplyXRot ? self.getXRot() : 0.0F;
        float xRotOForDisguise = shouldApplyXRot ? self.xRotO : 0.0F;
        // Snake/worm chains need camera-yaw to keep the head from freezing while segments swing.
        boolean bodyTracksCamera = behavior.bodyYawTracksCamera(disguise);
        float yBodyRotForDisguise = (bodyTracksCamera ? self.getYRot() : self.yBodyRot) + yawOffset;
        float yBodyRotOForDisguise = (bodyTracksCamera ? self.yRotO : self.yBodyRotO) + yawOffset;

        disguise.setPos(self.getX(), self.getY(), self.getZ());
        disguise.xo = self.xo;
        disguise.yo = self.yo;
        disguise.zo = self.zo;
        disguise.setDeltaMovement(self.getDeltaMovement());
        disguise.setYRot(self.getYRot() + yawOffset);
        disguise.yRotO = self.yRotO + yawOffset;
        disguise.setXRot(xRotForDisguise);
        disguise.xRotO = xRotOForDisguise;
        disguise.yBodyRot = yBodyRotForDisguise;
        disguise.yBodyRotO = yBodyRotOForDisguise;
        disguise.yHeadRot = self.yHeadRot + yawOffset;
        disguise.yHeadRotO = self.yHeadRotO + yawOffset;
        disguise.setOnGround(self.onGround());

        // Sync movement flags so renderers pick the right animation track via setupAnim.
        disguise.setSharedFlag(7, self.isFallFlying()); // FALL_FLYING
        disguise.setSwimming(self.isSwimming());
        disguise.setSprinting(self.isSprinting());

        // Reset health/animation state not covered by setInvulnerable.
        disguise.fallDistance = 0;
        disguise.hurtTime = 0;
        disguise.deathTime = 0;
        if (disguise.getHealth() < disguise.getMaxHealth()) {
            disguise.setHealth(disguise.getMaxHealth());
        }

        BehaviorHooks.run(
                "behavior.preTick:" + behavior.getClass().getName(),
                () -> behavior.preTick(self, disguise));

        // Suppress disguise.tick particles near camera for first-person wearer (per-thread context).
        boolean suppressDisguiseParticles = self.level().isClientSide
                && com.ninni.species.client.events.LocalFirstPersonCheck.isLocalFirstPersonWearer(self);
        if (suppressDisguiseParticles) {
            DisguiseTickContext.pushSuppress();
        }
        DisguiseTickContext.pushTick();
        try {
            // Refresh the portal cooldown each tick so the proxy body can never teleport across
            // dimensions and spawn a real mob in the destination.
            disguise.setPortalCooldown();
            disguise.tick();
        } catch (Throwable t) {
            DisguiseLogging.rateLimited(
                    "disguise.tick:" + disguise.getType().toString(), t);
        } finally {
            DisguiseTickContext.popTick();
            if (suppressDisguiseParticles) {
                DisguiseTickContext.popSuppress();
            }
        }

        BehaviorHooks.run(
                "behavior.postTick:" + behavior.getClass().getName(),
                () -> behavior.postTick(self, disguise));

        // Restore wearer state — neutralize push/teleport from disguise's aiStep.
        if (self.getX() != wearerX || self.getY() != wearerY || self.getZ() != wearerZ) {
            self.setPos(wearerX, wearerY, wearerZ);
        }
        self.setDeltaMovement(wearerDelta);

        // Realign multipart parts to wearer position (AI-moved otherwise).
        double dx = self.getX() - disguise.getX();
        double dy = self.getY() - disguise.getY();
        double dz = self.getZ() - disguise.getZ();
        if (dx != 0 || dy != 0 || dz != 0) {
            if (disguise.isMultipartEntity()) {
                net.minecraftforge.entity.PartEntity<?>[] parts = disguise.getParts();
                if (parts != null) {
                    for (net.minecraftforge.entity.PartEntity<?> part : parts) {
                        part.setPos(part.getX() + dx, part.getY() + dy, part.getZ() + dz);
                    }
                }
            }
            disguise.setPos(self.getX(), self.getY(), self.getZ());
        }

        // Restore rotation state zeroed by disguise.tick.
        disguise.setDeltaMovement(self.getDeltaMovement());
        disguise.yRotO = self.yRotO + yawOffset;
        disguise.setYRot(self.getYRot() + yawOffset);
        disguise.xRotO = xRotOForDisguise;
        disguise.setXRot(xRotForDisguise);
        disguise.yBodyRotO = yBodyRotOForDisguise;
        disguise.yBodyRot = yBodyRotForDisguise;
        disguise.yHeadRotO = self.yHeadRotO + yawOffset;
        disguise.yHeadRot = self.yHeadRot + yawOffset;

        // Disguise bodies never enter the level, so other mods' per-entity tick handlers (footsteps,
        // etc.) never fire. Re-fire the tick event server-side (body pinned to the wearer); guarded.
        if (!self.level().isClientSide) {
            try {
                net.minecraftforge.common.MinecraftForge.EVENT_BUS.post(
                        new net.minecraftforge.event.entity.living.LivingEvent.LivingTickEvent(disguise));
            } catch (Throwable t) {
                DisguiseLogging.rateLimited("disguise.tickEventReplay:" + disguise.getType(), t);
            }
        }

        com.ninni.species.server.disguise.BoundroidPairManager.tickCompanion(self);
        com.ninni.species.server.disguise.MineGuardianAnchorManager.tickAnchor(self);

        // Cosmetic particle hook (client-side only).
        if (self.level().isClientSide) {
            try {
                com.ninni.species.api.disguise.DisguiseCosmetics cosmetics =
                        com.ninni.species.server.disguise.cosmetic.DisguiseCosmeticRegistry.get(disguise);
                cosmetics.emitParticles(self, disguise, self.level(), self.getRandom());
            } catch (Throwable ignored) {}
        }

        // Ambient sound: ~once every ~5 seconds (matches Mob.playAmbientSound cadence).
        if (!self.level().isClientSide && self.tickCount > 0 && self.getRandom().nextInt(100) == 0) {
            try {
                com.ninni.species.api.disguise.DisguiseCosmetics cosmetics =
                        com.ninni.species.server.disguise.cosmetic.DisguiseCosmeticRegistry.get(disguise);
                net.minecraft.sounds.SoundEvent ambient = cosmetics.overrideAmbientSound(self, disguise);
                if (ambient != null) self.playSound(ambient, 1.0F, 1.0F);
            } catch (Throwable ignored) {}
        }

    }

    /** Static armor-stand tick: position/rotation from the stand, no {@code disguise.tick};
     *  {@code behavior.preTick} still runs for segment-chain managers. */
    @Unique
    private void tickStaticDisguiseFromArmorStand(net.minecraft.world.entity.decoration.ArmorStand stand, LivingEntity disguise) {
        com.ninni.species.api.disguise.DisguiseBehavior behavior =
                com.ninni.species.server.disguise.DisguiseBehaviorRegistry.get(disguise);
        float yawOffset = behavior.yawOffset(disguise);

        disguise.setPos(stand.getX(), stand.getY(), stand.getZ());
        disguise.xo = stand.xo;
        disguise.yo = stand.yo;
        disguise.zo = stand.zo;
        disguise.xOld = stand.xOld;
        disguise.yOld = stand.yOld;
        disguise.zOld = stand.zOld;
        disguise.setDeltaMovement(Vec3.ZERO);

        net.minecraft.core.Rotations head = stand.getHeadPose();
        // HeadPose drives the whole-body facing direction so segment chains (Centipede, Anaconda,
        // VoidWorm, GumWorm) trail in the direction the head is looking. Non-chain mobs end up
        // with head/body aligned (head-bone delta = 0), which reads as "the disguise faces where
        // the stand's head pose points" — coherent with statue intent.
        float poseYaw = stand.getYRot() + head.getY() + yawOffset;
        float poseYawO = stand.yRotO + head.getY() + yawOffset;

        disguise.setYRot(poseYaw);
        disguise.yRotO = poseYawO;
        disguise.yBodyRot = poseYaw;
        disguise.yBodyRotO = poseYawO;
        disguise.yHeadRot = poseYaw;
        disguise.yHeadRotO = poseYawO;
        disguise.setXRot(head.getX());
        disguise.xRotO = head.getX();
        disguise.setOnGround(stand.onGround());

        disguise.walkAnimation.speed = 0;
        disguise.walkAnimation.speedOld = 0;
        disguise.walkAnimation.position = 0;

        disguise.fallDistance = 0;
        disguise.hurtTime = 0;
        disguise.deathTime = 0;
        if (disguise.getHealth() < disguise.getMaxHealth()) {
            disguise.setHealth(disguise.getMaxHealth());
        }

        // Segment-chain managers tick their segments via preTick to follow the stand's pose.
        // disguise.tick() is still skipped so idle anims/particles/cooldowns stay frozen.
        BehaviorHooks.run(
                "behavior.preTick(armorStand):" + behavior.getClass().getName(),
                () -> behavior.preTick((LivingEntity) (Object) this, disguise));
    }

    /** Remove cosmetic wearer-effects. */
    @Unique
    private void species$removeWearerEffects(LivingEntity self, LivingEntity disguise) {
        try {
            com.ninni.species.api.disguise.DisguiseCosmetics cosmetics =
                    com.ninni.species.server.disguise.cosmetic.DisguiseCosmeticRegistry.get(disguise);
            for (net.minecraft.world.effect.MobEffectInstance e : cosmetics.wearerEffectsWhileWorn(self, disguise)) {
                self.removeEffect(e.getEffect());
            }
        } catch (Throwable ignored) {}
    }

    /** Configure a new disguise: load NBT, strip goals, tag, silence, apply effects. */
    @Unique
    private void species$configureNewDisguise(LivingEntity living, CompoundTag tag, boolean rebuild) {
        living.load(tag);
        if (living instanceof Mob mob) {
            // Strip goals so aiStep doesn't drive AI side-effects.
            try {
                mob.goalSelector.removeAllGoals(g -> true);
                mob.targetSelector.removeAllGoals(g -> true);
            } catch (Throwable ignored) {}
        }
        living.addTag(com.ninni.species.client.events.WickedMaskDisguiseEvents.DISGUISE_TAG);
        living.setSilent(!com.ninni.species.registry.SpeciesConfig.PLAY_DISGUISE_SOUNDS.get());
        // Must never die — death triggers dropAllDeathLoot and freezes the tick.
        living.setInvulnerable(true);
        try {
            com.ninni.species.api.disguise.DisguiseBehavior behavior =
                    com.ninni.species.server.disguise.DisguiseBehaviorRegistry.get(living);
            // Rebuild (dimension change, etc.) routes to onBodyRebuilt so behaviors resume their
            // persisted state instead of replaying one-shot onCreated setup.
            if (rebuild) behavior.onBodyRebuilt((LivingEntity) (Object) this, living);
            else behavior.onCreated((LivingEntity) (Object) this, living);
        } catch (Throwable ignored) {}
        if (!this.level().isClientSide) {
            try {
                LivingEntity selfWearer = (LivingEntity) (Object) this;
                com.ninni.species.api.disguise.DisguiseCosmetics cosmetics =
                        com.ninni.species.server.disguise.cosmetic.DisguiseCosmeticRegistry.get(living);
                for (net.minecraft.world.effect.MobEffectInstance e : cosmetics.wearerEffectsWhileWorn(selfWearer, living)) {
                    selfWearer.addEffect(new net.minecraft.world.effect.MobEffectInstance(e));
                }
            } catch (Throwable ignored) {}
        }
    }

    /** Register the disguise body in the global registry + segment/anchor companion managers. */
    @Unique
    private void species$wireNewDisguise(LivingEntity living) {
        com.ninni.species.server.disguise.DisguiseBodyRegistry.register(living, (LivingEntity) (Object) this);
        com.ninni.species.server.disguise.BoundroidPairManager.onDisguiseCreated((LivingEntity) (Object) this, living);
        com.ninni.species.server.disguise.MineGuardianAnchorManager.onDisguiseCreated((LivingEntity) (Object) this, living);
        // Armor stand wearers: disable frustum culling so far-extending segments stay visible.
        if ((Object) this instanceof net.minecraft.world.entity.decoration.ArmorStand) {
            this.noCulling = true;
        }
    }

    /** Clean up a disguise body: fire {@code onDestroyed}, remove cosmetic effects, unregister.
     *  Tolerant to a null {@code oldDisguise}. */
    @Unique
    private void species$cleanupOldDisguise(@Nullable LivingEntity oldDisguise) {
        if (oldDisguise != null) {
            BehaviorHooks.run("behavior.onDestroyed",
                    () -> com.ninni.species.server.disguise.DisguiseBehaviorRegistry.get(oldDisguise)
                            .onDestroyed((LivingEntity) (Object) this, oldDisguise));
            species$removeWearerEffects((LivingEntity) (Object) this, oldDisguise);
        }
        com.ninni.species.server.disguise.BoundroidPairManager.removeCompanion((LivingEntity) (Object) this);
        com.ninni.species.server.disguise.MineGuardianAnchorManager.removeAnchor((LivingEntity) (Object) this);
        com.ninni.species.server.disguise.DisguiseBodyRegistry.unregister(oldDisguise);
        if ((Object) this instanceof net.minecraft.world.entity.decoration.ArmorStand) {
            this.noCulling = false;
        }
    }

    @Inject(method = "jumpFromGround", at = @At("HEAD"), cancellable = true)
    public void applyBirtd(CallbackInfo ci) {
        if (this.hasEffect(SpeciesStatusEffects.BIRTD.get()) || this.hasEffect(SpeciesStatusEffects.STUCK.get())) ci.cancel();
    }

    @Inject(method = "makePoofParticles", at = @At("HEAD"), cancellable = true)
    public void S$makePoofParticles(CallbackInfo ci) {
        if (this instanceof CustomDeathParticles customDeathParticles) {
            ci.cancel();
            customDeathParticles.makeDeathParticles();
        }
    }


    @Inject(method = "isPushable", at = @At("HEAD"), cancellable = true)
    public void S$isPushable(CallbackInfoReturnable<Boolean> cir) {
        if (this.hasEffect(SpeciesStatusEffects.BIRTD.get()) || this.hasEffect(SpeciesStatusEffects.STUCK.get())) {
            cir.setReturnValue(false);
        }
    }

    @Inject(method = "getVisibilityPercent", at = @At("RETURN"), cancellable = true)
    private void onGetVisibilityPercent(@Nullable Entity observer, CallbackInfoReturnable<Double> cir) {
        LivingEntity self = (LivingEntity)(Object)this;
        double visibility = cir.getReturnValue();

        if (observer == null) return;

        ItemStack headItem = self.getItemBySlot(EquipmentSlot.HEAD);
        EntityType<?> observerType = observer.getType();

        if (observerType == SpeciesEntities.GHOUL.get() && headItem.is(SpeciesItems.GHOUL_HEAD.get()) ||
                observerType == SpeciesEntities.WICKED.get() && headItem.is(SpeciesItems.WICKED_CANDLE.get()) ||
                observerType == SpeciesEntities.BEWEREAGER.get() && headItem.is(SpeciesItems.BEWEREAGER_HEAD.get()) ||
                observerType == SpeciesEntities.QUAKE.get() && headItem.is(SpeciesItems.QUAKE_HEAD.get())) {
            visibility *= 0.5D;
            cir.setReturnValue(visibility);
        }
    }

    @Override
    public @Unique boolean hasSnatched() {
        return this.snatched;
    }
    @Override
    public @Unique void setSnatched(boolean snatched) {
        this.snatched = snatched;
    }

    @Override
    public @Unique boolean hasTanked() {
        return this.tanked;
    }
    @Override
    public @Unique void setTanked(boolean tanked) {
        this.tanked = tanked;
    }

    @Override
    public @Unique EntityType getDisguisedEntityType() {
        return disguisedEntityType;
    }
    @Override
    public @Unique void setDisguisedEntityType(EntityType disguisedEntityType) {
        this.disguisedEntityType = disguisedEntityType;
    }

    @Override
    public @Unique LivingEntity getDisguisedEntity() {
        return disguisedEntity;
    }
    @Override
    public @Unique void setDisguisedEntity(LivingEntity disguisedEntity) {
        this.disguisedEntity = disguisedEntity;
    }
}
