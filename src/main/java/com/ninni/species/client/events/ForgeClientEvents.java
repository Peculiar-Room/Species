package com.ninni.species.client.events;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.Species;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.disguise.DisguiseCosmetics;
import com.ninni.species.api.disguise.DisguiseRenderLayer;
import com.ninni.species.api.disguise.DisguiseRenderer;
import com.ninni.species.mixin.client.EntityRenderDispatcherShadowAccessor;
import com.ninni.species.mixin.client.EntityRendererShadowAccessor;
import com.ninni.species.mixin_util.EntityRenderDispatcherAccess;
import com.ninni.species.mixin_util.LivingEntityAccess;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.server.disguise.DisguiseBehaviorRegistry;
import com.ninni.species.server.disguise.DisguiseLogging;
import com.ninni.species.server.disguise.cosmetic.DisguiseCosmeticContext;
import com.ninni.species.server.disguise.cosmetic.DisguiseCosmeticRegistry;
import com.ninni.species.server.disguise.dsl.WearerPredicates;
import com.ninni.species.server.disguise.panacea.DisguiseTopology;
import com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry;
import com.ninni.species.server.disguise.util.BehaviorHooks;
import com.ninni.species.server.entity.util.SpeciesPose;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeClientEvents {


    /** Behavior-owned poses skipped by {@link #syncWearerStateToDisguise}'s pose resync so
     *  {@code onSyncedDataUpdated} animation hooks don't cancel mid-frame. */
    private static final java.util.Set<net.minecraft.world.entity.Pose> BEHAVIOR_OWNED_POSES;
    static {
        java.util.Set<net.minecraft.world.entity.Pose> set =
                java.util.EnumSet.noneOf(net.minecraft.world.entity.Pose.class);
        for (SpeciesPose sp :
                SpeciesPose.values()) {
            set.add(sp.get());
        }
        set.add(net.minecraft.world.entity.Pose.ROARING);
        // Behaviors that signal "wearer is flying" by setting FALL_FLYING (e.g. Birt) need this
        // protected from the per-frame wearer-pose resync — otherwise creative/swimming wearers
        // (getPose()=STANDING/SWIMMING) erase it and onSyncedDataUpdated kills the animation.
        set.add(net.minecraft.world.entity.Pose.FALL_FLYING);
        BEHAVIOR_OWNED_POSES = java.util.Collections.unmodifiableSet(set);
    }

    @SubscribeEvent
    public static void onRenderLivingSpecialPre(RenderLivingEvent.Pre<?, ?> event) {
        LivingEntity entity = event.getEntity();

        if (Minecraft.getInstance().getEntityRenderDispatcher() instanceof EntityRenderDispatcherAccess access && access.getRenderingInventoryEntity()) {
            if (entity.getItemBySlot(EquipmentSlot.HEAD).is(SpeciesItems.WICKED_MASK.get())) {
                LivingEntity disguise = ((LivingEntityAccess) entity).getDisguisedEntity();
                if (disguise != null) {
                    // Y offset is applied pre-scale (world units) so the model centres in the inventory frame.
                    float yOffset = DisguiseTopology.getInventoryYOffset(disguise);
                    if (yOffset != 0.0F) {
                        event.getPoseStack().translate(0.0F, yOffset, 0.0F);
                    }
                    float scale = DisguiseTopology.getInventoryScale(disguise);
                    if (scale != 1.0F) {
                        event.getPoseStack().scale(scale, scale, scale);
                    }
                }
            }
        }
    }

    @SubscribeEvent
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void livingEntityRenderer(RenderLivingEvent<LivingEntity, EntityModel<LivingEntity>> event) {
        LivingEntity entity = event.getEntity();
        ItemStack headItem = entity.getItemBySlot(EquipmentSlot.HEAD);
        LivingEntity disguise = ((LivingEntityAccess) entity).getDisguisedEntity();

        boolean shouldDisguise = headItem.is(SpeciesItems.WICKED_MASK.get())
                && headItem.hasTag()
                && headItem.getTag().contains("id")
                && disguise != null
                && !disguise.isRemoved()
                && disguise.getType() != null
                && !entity.hasEffect(MobEffects.INVISIBILITY)
                && !(entity instanceof Player player && player.isSpectator());

        if (shouldDisguise) {
            event.setCanceled(true);

            EntityRenderDispatcher dispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
            EntityRenderer<?> baseRenderer = dispatcher.getRenderer(disguise);

            if (baseRenderer != null) {
                PoseStack poseStack = event.getPoseStack();
                MultiBufferSource buffer = event.getMultiBufferSource();
                int light = event.getPackedLight();
                // Armor stand statue: pin partialTick=0 so time-driven micro-anims don't oscillate on the frozen tickCount.
                boolean wearerIsArmorStand = entity instanceof net.minecraft.world.entity.decoration.ArmorStand;
                float partialTick = wearerIsArmorStand ? 0.0F : event.getPartialTick();

                boolean inInventory = Minecraft.getInstance().getEntityRenderDispatcher() instanceof EntityRenderDispatcherAccess access
                        && access.getRenderingInventoryEntity();

                syncWearerStateToDisguise(entity, disguise);

                DisguiseBehavior behavior =
                        DisguiseBehaviorRegistry.get(disguise);

                // Snake/worm disguises pin yBodyRot=getYRot(); using the wearer's lagging yBodyRot re-introduces lag, so read camera yaw when the behavior opts in.
                float bodyYaw = behavior.bodyYawTracksCamera(disguise)
                        ? Mth.rotLerp(partialTick, entity.yRotO, entity.getYRot())
                        : Mth.rotLerp(partialTick, entity.yBodyRotO, entity.yBodyRot);

                // Inventory render is fullbright GUI context; in-world recomputes per the disguise's renderer.
                final int disguiseLight;
                if (inInventory) {
                    disguiseLight = net.minecraft.client.renderer.LightTexture.FULL_BRIGHT;
                } else {
                    int computed = light;
                    try {
                        computed = ((EntityRenderer) baseRenderer).getPackedLightCoords(disguise, partialTick);
                    } catch (Throwable ignored) {}
                    disguiseLight = computed;
                }
                BehaviorHooks.run(
                        "disguise.preRender:" + disguise.getType(),
                        () -> behavior.preRender(entity, disguise, partialTick, inInventory));
                if (inInventory) {
                    BehaviorHooks.run(
                            "disguise.preInventoryPose:" + disguise.getType(),
                            () -> behavior.preInventoryPose(entity, disguise, partialTick));
                }

                // Push wearer onto DisguiseCosmeticContext for the texture-override mixin.
                DisguiseCosmeticContext.push(entity);
                poseStack.pushPose();
                // Per-type world Y offset for ground-flat models (e.g. Triops) that would otherwise render below sightlines.
                float worldYOffset = DisguiseTopologyRegistry.getWorldYOffset(disguise);
                if (worldYOffset != 0.0F) poseStack.translate(0.0, worldYOffset, 0.0);
                // Per-behavior dynamic Y offset for animation-driven hops the disguise can't physically apply.
                float behaviorYOffset = behavior.renderYOffset(entity, disguise, partialTick);
                if (behaviorYOffset != 0.0F) poseStack.translate(0.0, behaviorYOffset, 0.0);
                // bbHeight compensation: only ceiling-anchored types in hanging state.
                if (isHeadAnchoredDisguise(disguise)
                        && WearerPredicates.CEILING_HANGING.test(entity)) {
                    float bbDelta = entity.getBbHeight() - disguise.getBbHeight();
                    if (bbDelta > 0.0F) poseStack.translate(0.0, bbDelta, 0.0);
                }
                try {
                    DisguiseCosmetics cosmetics =
                            DisguiseCosmeticRegistry.get(disguise);
                    float worldScale = cosmetics.overrideWorldScale(entity, disguise);
                    if (!Float.isNaN(worldScale) && worldScale > 0.0F && worldScale != 1.0F) {
                        poseStack.scale(worldScale, worldScale, worldScale);
                    }
                    net.minecraft.network.chat.Component nameOverride = cosmetics.overrideNameTag(entity, disguise);
                    if (nameOverride != null) {
                        disguise.setCustomName(nameOverride);
                        disguise.setCustomNameVisible(true);
                    }
                    DisguiseRenderer renderOverride =
                            cosmetics.overrideRenderer(entity, disguise);
                    if (renderOverride != null) {
                        BehaviorHooks.run(
                                "disguise.renderOverride:" + disguise.getType(),
                                () -> renderOverride.render(entity, disguise, baseRenderer, bodyYaw, partialTick, poseStack, buffer, disguiseLight));
                    } else {
                        ((EntityRenderer) baseRenderer).render(disguise, bodyYaw, partialTick, poseStack, buffer, disguiseLight);
                    }
                } catch (Throwable t) {
                    DisguiseLogging.rateLimited(
                            "disguise.render:" + disguise.getType(), t);
                } finally {
                    poseStack.popPose();
                    DisguiseCosmeticContext.pop();
                }

                // Extra render layers drawn after the body (registered via DisguiseTopologyRegistry.registerRenderLayer).
                java.util.List<DisguiseRenderLayer> renderLayers =
                        DisguiseTopologyRegistry.getRenderLayers(disguise);
                if (!renderLayers.isEmpty()) {
                    // Layer crashes are visual-only; continue rendering. Rate-limited dedup keeps the
                    // log signal useful for layer authors without spamming the console per frame.
                    for (DisguiseRenderLayer layer : renderLayers) {
                        BehaviorHooks.run(
                                "disguise.renderLayer:" + disguise.getType(),
                                () -> layer.render(entity, disguise, poseStack, buffer, partialTick, disguiseLight, inInventory));
                    }
                }

                // Sub-entities (Gum Worm segments, Boundroid companion) aren't in ClientLevel, so render them manually; multipart parts are drawn by the parent renderer.
                final EntityRenderDispatcher subDispatcher = dispatcher;
                final PoseStack subPoseStack = poseStack;
                final MultiBufferSource subBuffer = buffer;
                final int subBaseLight = light;
                final float subPartialTick = partialTick;
                DisguiseTopology.forEachRenderableSubEntity(
                        entity, disguise,
                        (sub, dx, dy, dz) -> {
                            EntityRenderer<?> subRenderer = subDispatcher.getRenderer(sub);
                            if (subRenderer == null) return;
                            int subLight = subBaseLight;
                            try {
                                subLight = ((EntityRenderer) subRenderer).getPackedLightCoords(sub, subPartialTick);
                            } catch (Throwable ignored) {}
                            subPoseStack.pushPose();
                            subPoseStack.translate(dx, dy, dz);
                            final int subLightFinal = subLight;
                            BehaviorHooks.run(
                                    "disguise.subEntityRender:" + sub.getType(),
                                    () -> ((EntityRenderer) subRenderer).render(sub, sub.getYRot(), subPartialTick, subPoseStack, subBuffer, subLightFinal));
                            // Per-segment shadow — sub-entities bypass the dispatcher, so its
                            // shadow block never fires for them; mirror the draw here.
                            renderSubEntityShadow(sub, subRenderer, subPoseStack, subBuffer, subPartialTick);
                            subPoseStack.popPose();
                        });

                if (inInventory) {
                    BehaviorHooks.run(
                            "disguise.postInventoryPose:" + disguise.getType(),
                            () -> behavior.postInventoryPose(entity, disguise, partialTick));
                }
                BehaviorHooks.run(
                        "disguise.postRender:" + disguise.getType(),
                        () -> behavior.postRender(entity, disguise, partialTick, inInventory));
            }
        }

        if (((LivingEntityAccess) entity).hasTanked()) event.getPoseStack().scale(1.35F, 1.125F, 1.35F);
        if (((LivingEntityAccess) entity).hasSnatched()) event.getPoseStack().scale(0.85F, 1.125F, 0.85F);
    }

    /** Per-segment shadow for chain sub-entities that bypass the dispatcher; PoseStack must be segment-relative-to-camera (what {@code renderShadow} expects). */
    private static void renderSubEntityShadow(net.minecraft.world.entity.Entity sub,
                                              EntityRenderer<?> subRenderer,
                                              PoseStack poseStack, MultiBufferSource buffer,
                                              float partialTick) {
        if (sub.isInvisible()) return;
        float radius = ((EntityRendererShadowAccessor) subRenderer)
                .species$getShadowRadius();
        if (radius <= 0.0F) return;
        float strength = ((EntityRendererShadowAccessor) subRenderer)
                .species$getShadowStrength();
        net.minecraft.world.phys.Vec3 cam =
                Minecraft.getInstance().gameRenderer.getMainCamera().getPosition();
        double distSq = cam.distanceToSqr(sub.getX(), sub.getY(), sub.getZ());
        float weight = (float) ((1.0 - distSq / 256.0) * strength);
        if (weight <= 0.0F) return;
        EntityRenderDispatcherShadowAccessor.species$renderShadow(
                poseStack, buffer, sub, weight, partialTick, sub.level(), Math.min(radius, 32.0F));
    }

    /** True for ceiling-anchored types (Winch, Vesper, Cliff Hanger) needing bbHeight render
     *  compensation so the flipped/anchored body seats against the ceiling above the taller wearer. */
    private static boolean isHeadAnchoredDisguise(LivingEntity disguise) {
        if (disguise == null) return false;
        net.minecraft.resources.ResourceLocation key =
                net.minecraft.core.registries.BuiltInRegistries.ENTITY_TYPE.getKey(disguise.getType());
        if (key == null) return false;
        String namespace = key.getNamespace();
        String path = key.getPath();
        if ("species".equals(namespace)) return "cliff_hanger".equals(path);
        if (!"alexscaves".equals(namespace)) return false;
        return "boundroid_winch".equals(path) || "vesper".equals(path);
    }

    private static void syncWearerStateToDisguise(LivingEntity wearer, LivingEntity disguise) {
        DisguiseBehavior behavior =
                DisguiseBehaviorRegistry.get(disguise);

        boolean inInventory = Minecraft.getInstance().getEntityRenderDispatcher() instanceof EntityRenderDispatcherAccess access
                && access.getRenderingInventoryEntity();

        float yawOffset = behavior.yawOffset(disguise);
        // Armor stand pose drives head/pitch — the wearer's xRot would point wherever the player
        // happened to be looking when nudging the stand.
        boolean isArmorStand = wearer instanceof net.minecraft.world.entity.decoration.ArmorStand;
        float xRotForDisguise;
        float xRotOForDisguise;
        if (isArmorStand) {
            net.minecraft.core.Rotations head = ((net.minecraft.world.entity.decoration.ArmorStand) wearer).getHeadPose();
            xRotForDisguise = head.getX();
            xRotOForDisguise = head.getX();
        } else {
            boolean shouldApplyXRot = behavior.shouldApplyXRot(wearer, disguise, inInventory);
            xRotForDisguise = shouldApplyXRot ? wearer.getXRot() : 0.0F;
            xRotOForDisguise = shouldApplyXRot ? wearer.xRotO : 0.0F;
        }
        // Snake/worm disguises pin yBodyRot=getYRot() in their tick; sourcing the wearer's lagging yBodyRot re-introduces frame lag.
        boolean bodyTracksCamera = behavior.bodyYawTracksCamera(disguise);
        float yBodyRotForDisguise = (bodyTracksCamera ? wearer.getYRot() : wearer.yBodyRot) + yawOffset;
        float yBodyRotOForDisguise = (bodyTracksCamera ? wearer.yRotO : wearer.yBodyRotO) + yawOffset;

        disguise.setPos(wearer.getX(), wearer.getY(), wearer.getZ());
        disguise.xo = wearer.xo;
        disguise.yo = wearer.yo;
        disguise.zo = wearer.zo;
        // Some renderers read xOld/yOld/zOld for world-space trail effects; without this sync they render at origin (0,0,0).
        disguise.xOld = wearer.xOld;
        disguise.yOld = wearer.yOld;
        disguise.zOld = wearer.zOld;
        disguise.setDeltaMovement(wearer.getDeltaMovement());

        disguise.setXRot(xRotForDisguise);
        disguise.xRotO = xRotOForDisguise;
        // Armor stand: HeadPose Y delta drives whole-body facing so chains follow the head; other wearers keep the body/head yaw split.
        if (isArmorStand) {
            float headYawDelta = ((net.minecraft.world.entity.decoration.ArmorStand) wearer).getHeadPose().getY();
            float poseYaw = wearer.getYRot() + headYawDelta + yawOffset;
            float poseYawO = wearer.yRotO + headYawDelta + yawOffset;
            disguise.setYRot(poseYaw);
            disguise.yRotO = poseYawO;
            disguise.yBodyRot = poseYaw;
            disguise.yBodyRotO = poseYawO;
            disguise.yHeadRot = poseYaw;
            disguise.yHeadRotO = poseYawO;
        } else {
            disguise.setYRot(wearer.getYRot() + yawOffset);
            disguise.yRotO = wearer.yRotO + yawOffset;
            disguise.yBodyRot = yBodyRotForDisguise;
            disguise.yBodyRotO = yBodyRotOForDisguise;
            disguise.yHeadRot = wearer.yHeadRot + yawOffset;
            disguise.yHeadRotO = wearer.yHeadRotO + yawOffset;
        }

        // Armor stand statue: freeze tickCount so setupAnim's ageInTicks stays constant.
        if (!isArmorStand) {
            disguise.tickCount = wearer.tickCount;
        }
        disguise.hurtTime = wearer.hurtTime;
        disguise.hurtDuration = wearer.hurtDuration;
        disguise.deathTime = wearer.deathTime;
        disguise.attackAnim = wearer.attackAnim;
        disguise.oAttackAnim = wearer.oAttackAnim;
        disguise.swinging = wearer.swinging;
        disguise.swingTime = wearer.swingTime;
        disguise.swingingArm = wearer.swingingArm;
        // Skip pose resync while a behavior-managed pose is active (e.g. SpeciesPose.ATTACK); otherwise it flickers back to STANDING every frame.
        if (!BEHAVIOR_OWNED_POSES.contains(disguise.getPose())) {
            disguise.setPose(wearer.getPose());
        }

        WalkAnimationState src = wearer.walkAnimation;
        WalkAnimationState dst = disguise.walkAnimation;
        dst.speed = src.speed;
        dst.speedOld = src.speedOld;
        dst.position = src.position;

        if (disguise instanceof Mob mobDisguise) {
            if (wearer instanceof Mob mobWearer) {
                mobDisguise.setLeftHanded(mobWearer.isLeftHanded());
            } else if (wearer instanceof Player playerWearer) {
                mobDisguise.setLeftHanded(playerWearer.getMainArm() == HumanoidArm.LEFT);
            }
        }

        if (disguise instanceof EnderMan enderMan) {
            enderMan.setCarriedBlock(pickCarriedBlock(wearer));
        }
    }


    private static BlockState pickCarriedBlock(LivingEntity wearer) {
        ItemStack mainHand = wearer.getMainHandItem();
        if (!mainHand.isEmpty() && mainHand.getItem() instanceof BlockItem blockItem) {
            BlockState state = blockItem.getBlock().defaultBlockState();
            if (!state.is(Blocks.AIR)) return state;
        }
        return null;
    }
}
