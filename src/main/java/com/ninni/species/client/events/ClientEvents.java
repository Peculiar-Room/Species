package com.ninni.species.client.events;

import com.mojang.blaze3d.platform.InputConstants;
import com.ninni.species.Species;
import com.ninni.species.client.model.mob.update_1.*;
import com.ninni.species.client.model.mob.update_2.*;
import com.ninni.species.client.model.mob.update_3.*;
import com.ninni.species.client.model.mob_heads.BewereagerHeadModel;
import com.ninni.species.client.model.mob_heads.GhoulHeadModel;
import com.ninni.species.client.model.mob_heads.QuakeHeadModel;
import com.ninni.species.client.model.mob_heads.WickedHeadModel;
import com.ninni.species.client.renderer.item.HarpoonRenderer;
import com.ninni.species.client.screen.BloodLustOverlay;
import com.ninni.species.client.particles.*;
import com.ninni.species.client.renderer.block.*;
import com.ninni.species.client.renderer.entity.*;
import com.ninni.species.client.renderer.item.WickedFireballRenderer;
import com.ninni.species.client.renderer.item.WickedSwapperProjectileRenderer;
import com.ninni.species.client.screen.ScreenShakeEvent;
import com.ninni.species.registry.*;
import com.ninni.species.server.entity.mob.update_2.Springling;
import com.ninni.species.server.entity.mob.update_3.Harpoon;
import com.ninni.species.mixin_util.PlayerAccess;
import com.ninni.species.server.item.CrankbowItem;
import com.ninni.species.server.item.SpectreLightBlockItem;
import com.ninni.species.server.packet.HarpoonInputPacket;
import com.ninni.species.server.packet.UpdateSpringlingDataPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.FlameParticle;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.util.MutableHashedLinkedMap;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.util.*;

import static com.ninni.species.client.events.ClientEventsHandler.isValidKey;

@OnlyIn(Dist.CLIENT)
@Mod.EventBusSubscriber(modid = Species.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientEvents {
    private static final ResourceLocation SPECIES_ICONS = new ResourceLocation(Species.MOD_ID, "textures/gui/icons.png");
    public static final List<ScreenShakeEvent> SCREEN_SHAKE_EVENTS = new ArrayList<>();
    private static float shakeAmount;
    private static float prevShakeAmount;
    static float smoothPitch;
    static float smoothRoll;

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        IEventBus eventBus = MinecraftForge.EVENT_BUS;

        eventBus.addListener((TickEvent.ClientTickEvent clientTickEvent) -> {
            Minecraft client = Minecraft.getInstance();
            Player player = client.player;
            if (player != null && player.getVehicle() instanceof Springling springling && !springling.isRetracting()) {
                float extendedAmount = springling.getExtendedAmount();
                if (!SpeciesKeyMappings.RETRACT_KEY.isDown() && !springling.level().getBlockState(player.blockPosition().above()).isSolid() && !springling.level().getBlockState(player.blockPosition().above(2)).isSolid() && SpeciesKeyMappings.EXTEND_KEY.isDown()) {
                    SpeciesNetwork.INSTANCE.sendToServer(new UpdateSpringlingDataPacket(0.1F, extendedAmount < springling.getMaxExtendedAmount()));
                }
                if (SpeciesKeyMappings.RETRACT_KEY.isDown() && !SpeciesKeyMappings.EXTEND_KEY.isDown()) {
                    SpeciesNetwork.INSTANCE.sendToServer(new UpdateSpringlingDataPacket(-0.25F, extendedAmount > 0));
                }
            }
        });

        Species.CALLBACKS.forEach(Runnable::run);
        Species.CALLBACKS.clear();
        eventBus.register(new BloodLustOverlay());
    }

    @SubscribeEvent
    public void clientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();

        //Code taken and modified from Alex
        if (event.phase == TickEvent.Phase.END) {
            Entity cameraEntity = mc.getCameraEntity();
            prevShakeAmount = shakeAmount;
            float shake = 0.0F;
            Iterator<ScreenShakeEvent> groundShakeMomentIterator = SCREEN_SHAKE_EVENTS.iterator();
            while (groundShakeMomentIterator.hasNext()) {
                ScreenShakeEvent groundShakeMoment = groundShakeMomentIterator.next();
                groundShakeMoment.tick();
                if (groundShakeMoment.isDone()) groundShakeMomentIterator.remove();
                else shake = Math.max(shake, groundShakeMoment.getDegree(cameraEntity, 1.0F));
            }
            shakeAmount = shake * mc.options.screenEffectScale().get().floatValue();
        }


        if (mc.player != null && mc.level != null && event.phase == TickEvent.Phase.END) {

            InputConstants.Key keyForward = mc.options.keyUp.getKey();
            InputConstants.Key keyBack = mc.options.keyDown.getKey();
            InputConstants.Key keyLeft = mc.options.keyLeft.getKey();
            InputConstants.Key keyRight = mc.options.keyRight.getKey();
            InputConstants.Key jump = mc.options.keyJump.getKey();
            InputConstants.Key sneak = mc.options.keyShift.getKey();

            long window = mc.getWindow().getWindow();

            boolean forward = isValidKey(keyForward) && InputConstants.isKeyDown(window, keyForward.getValue());
            boolean back = isValidKey(keyBack) && InputConstants.isKeyDown(window, keyBack.getValue());
            boolean left = isValidKey(keyLeft) && InputConstants.isKeyDown(window, keyLeft.getValue());
            boolean right = isValidKey(keyRight) && InputConstants.isKeyDown(window, keyRight.getValue());
            boolean jumpKeyDown = isValidKey(jump) && InputConstants.isKeyDown(window, jump.getValue());
            boolean sneakKeyDown = isValidKey(sneak) && InputConstants.isKeyDown(window, sneak.getValue());

            float x = (right ? 1 : 0) - (left ? 1 : 0);
            float z = (forward ? 1 : 0) - (back ? 1 : 0);
            float y = (jumpKeyDown ? 1 : 0) - (sneakKeyDown ? 1 : 0);

            if (mc.player.isUsingItem() && mc.player.getUseItem().is(SpeciesItems.HARPOON.get()) && mc.player instanceof PlayerAccess playerAccess) {
                SpeciesNetwork.INSTANCE.sendToServer(new HarpoonInputPacket(playerAccess.getHarpoonId(), x, y, z));
            }
        }
    }

    @SubscribeEvent
    public void computeCameraAngles(ViewportEvent.ComputeCameraAngles event) {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        float partialTicks = (float) event.getPartialTick();

        //Code taken and modified from Alex
        float lerpedShakeAmount = Mth.clamp(prevShakeAmount + (shakeAmount - prevShakeAmount) * partialTicks, 0, 4.0F);
        if (lerpedShakeAmount > 0) {
            float time = mc.cameraEntity == null ? 0.0F : mc.cameraEntity.tickCount + mc.getPartialTick();
            event.setRoll((float) (lerpedShakeAmount * Math.sin(2.0F * time)));
        }

        float targetPitch = 0.0F;
        float targetRoll = 0.0F;

        if (player instanceof PlayerAccess access) {
            Entity harpoonEntity = mc.level.getEntity(access.getHarpoonId());
            if (harpoonEntity instanceof Harpoon harpoon && harpoon.isAnchored() && harpoon.getAnchorPos() != null) {

                Vec3 anchor = Vec3.atCenterOf(harpoon.getAnchorPos());

                double px = Mth.lerp(partialTicks, player.xo, player.getX());
                double py = Mth.lerp(partialTicks, player.yo, player.getY());
                double pz = Mth.lerp(partialTicks, player.zo, player.getZ());

                Vec3 playerPos = new Vec3(px, py + player.getEyeHeight(), pz);

                Vec3 toAnchor = anchor.subtract(playerPos).normalize();

                float yawRad = -player.getYRot() * Mth.DEG_TO_RAD;
                float pitchRad = -player.getXRot() * Mth.DEG_TO_RAD;
                double forwardsInfluence = new Vec3(Math.sin(yawRad), 0, -Math.cos(yawRad)).dot(toAnchor);
                double lateralInfluence = -(new Vec3(Math.sin(pitchRad) + Math.cos(yawRad), 0, Math.cos(pitchRad) - Math.cos(yawRad)).dot(toAnchor));

                float forwardsMaxTilt = (float) (5.0F * mc.options.fovEffectScale().get());
                float lateralMaxTilt = (float) (5.0F * mc.options.fovEffectScale().get());

                targetPitch = (float) Mth.clamp(forwardsInfluence * forwardsMaxTilt, -forwardsMaxTilt, forwardsMaxTilt);
                targetRoll = (float) Mth.clamp(lateralInfluence * lateralMaxTilt, -lateralMaxTilt, lateralMaxTilt);
            }
        }

        smoothPitch = Mth.lerp(0.15F, smoothPitch, targetPitch);
        smoothRoll = Mth.lerp(0.15F, smoothRoll, targetRoll);
        event.setPitch(event.getPitch() + smoothPitch);
        event.setRoll(event.getRoll() + smoothRoll);
    }

    @SubscribeEvent
    public void preRenderGuiOverlay(RenderGuiOverlayEvent.Pre event) {
        Player player = Minecraft.getInstance().player;

        if (event.getOverlay().id().equals(VanillaGuiOverlay.EXPERIENCE_BAR.id()) && player.getVehicle() instanceof Springling) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void postRenderGuiOverlay(RenderGuiOverlayEvent.Post event) {
        Player player = Minecraft.getInstance().player;

        if (player.getVehicle() instanceof Springling springling) {
            int screenWidth = event.getWindow().getGuiScaledWidth();
            int screenHeight = event.getWindow().getGuiScaledHeight();
            int j = screenWidth / 2 - 91;
            int k = screenHeight - 32 + 3;

            float progress = (springling.getMaxExtendedAmount() - springling.getExtendedAmount())/springling.getMaxExtendedAmount();
            int progressScaled = 182 - Mth.ceil(progress * 182);

            event.getGuiGraphics().pose().pushPose();
            event.getGuiGraphics().blit(SPECIES_ICONS, j, k, 50, 0, 64, 182, 5, 256, 256);
            event.getGuiGraphics().blit(SPECIES_ICONS, j, k, 50, 0, 69, progressScaled, 5, 256, 256);
            event.getGuiGraphics().pose().popPose();
        }
    }

    @SubscribeEvent
    public static void registerCreativeModeTab(BuildCreativeModeTabContentsEvent event) {
        ResourceKey<CreativeModeTab> key = event.getTabKey();
        MutableHashedLinkedMap<ItemStack, CreativeModeTab.TabVisibility> entries = event.getEntries();

        if (key == SpeciesCreativeModeTabs.SPECIES.getKey()) {
            entries.putAfter(new ItemStack(SpeciesItems.GHOUL_TONGUE.get()), PotionUtils.setPotion(new ItemStack(Items.POTION), SpeciesPotions.BLOODLUST.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(PotionUtils.setPotion(new ItemStack(Items.POTION), SpeciesPotions.BLOODLUST.get()), PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), SpeciesPotions.BLOODLUST.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(PotionUtils.setPotion(new ItemStack(Items.SPLASH_POTION), SpeciesPotions.BLOODLUST.get()), PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), SpeciesPotions.BLOODLUST.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(PotionUtils.setPotion(new ItemStack(Items.LINGERING_POTION), SpeciesPotions.BLOODLUST.get()), PotionUtils.setPotion(new ItemStack(Items.TIPPED_ARROW), SpeciesPotions.BLOODLUST.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putAfter(new ItemStack(SpeciesItems.WEREFANG.get()), ClientEventsHandler.getHopefulBannerInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(SpeciesItems.CRANKBOW.get()), EnchantedBookItem.createForEnchantment(new EnchantmentInstance(SpeciesEnchantments.SPARING.get(), SpeciesEnchantments.SPARING.get().getMaxLevel())), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(SpeciesItems.CRANKBOW.get()), EnchantedBookItem.createForEnchantment(new EnchantmentInstance(SpeciesEnchantments.CAPACITY.get(), SpeciesEnchantments.CAPACITY.get().getMaxLevel())), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(SpeciesItems.CRANKBOW.get()), EnchantedBookItem.createForEnchantment(new EnchantmentInstance(SpeciesEnchantments.QUICK_CRANK.get(), SpeciesEnchantments.QUICK_CRANK.get().getMaxLevel())), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(SpeciesItems.CRANKBOW.get()), EnchantedBookItem.createForEnchantment(new EnchantmentInstance(SpeciesEnchantments.SCATTERSHOT.get(), SpeciesEnchantments.SCATTERSHOT.get().getMaxLevel())), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);

            entries.putAfter(new ItemStack(SpeciesItems.HARPOON.get()), ClientEventsHandler.getSpeciesPainting(SpeciesPaintingVariants.THE_COMPOSITION.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

        if (key == CreativeModeTabs.NATURAL_BLOCKS) {
            entries.putAfter(new ItemStack(Items.HAY_BLOCK), new ItemStack(SpeciesItems.BIRT_DWELLING.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.SNIFFER_EGG), new ItemStack(SpeciesItems.WRAPTOR_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            entries.putAfter(new ItemStack(Items.MUSIC_DISC_RELIC), new ItemStack(SpeciesItems.MUSIC_DISC_DIAL.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(SpeciesItems.MUSIC_DISC_DIAL.get()), new ItemStack(SpeciesItems.MUSIC_DISC_LAPIDARIAN.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(SpeciesItems.MUSIC_DISC_LAPIDARIAN.get()), new ItemStack(SpeciesItems.MUSIC_DISK_SPAWNER.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.PUFFERFISH_BUCKET), new ItemStack(SpeciesItems.DEEPFISH_BUCKET.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putBefore(new ItemStack(Items.ENDER_PEARL), new ItemStack(SpeciesItems.WICKED_MASK.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.ENDER_PEARL), new ItemStack(SpeciesItems.WICKED_SWAPPER.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.COMBAT) {
            entries.putAfter(new ItemStack(Items.EGG), new ItemStack(SpeciesItems.BIRT_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.DIAMOND_HORSE_ARMOR), new ItemStack(SpeciesItems.DEFLECTOR_DUMMY.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.SHIELD), new ItemStack(SpeciesItems.RICOSHIELD.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.CROSSBOW), new ItemStack(SpeciesItems.CRANKBOW.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putBefore(new ItemStack(Items.SNOWBALL), new ItemStack(SpeciesItems.SMOKE_BOMB.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putBefore(new ItemStack(Items.WOODEN_AXE), new ItemStack(SpeciesItems.SPECTRALIBUR.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.INGREDIENTS) {
            entries.putAfter(new ItemStack(Items.EGG), new ItemStack(SpeciesItems.BIRT_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.SPIDER_EYE), new ItemStack(SpeciesItems.GHOUL_TONGUE.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putBefore(new ItemStack(Items.NETHER_STAR), new ItemStack(SpeciesItems.KINETIC_CORE.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.STRING), new ItemStack(SpeciesItems.WICKED_WAX.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.STRING), new ItemStack(SpeciesItems.WEREFANG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.STRING), new ItemStack(SpeciesItems.BROKEN_LINKS.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.ENDER_PEARL), new ItemStack(SpeciesItems.WICKED_SWAPPER.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.REDSTONE_BLOCKS) {
            entries.putAfter(new ItemStack(Items.ARMOR_STAND), new ItemStack(SpeciesItems.DEFLECTOR_DUMMY.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.FOOD_AND_DRINKS) {
            entries.putAfter(new ItemStack(Items.SPIDER_EYE), new ItemStack(SpeciesItems.GHOUL_TONGUE.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.ROTTEN_FLESH), new ItemStack(SpeciesItems.MONSTER_MEAL.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.ENCHANTED_GOLDEN_APPLE), new ItemStack(SpeciesItems.WICKED_DOPE.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(SpeciesItems.WICKED_DOPE.get()), new ItemStack(SpeciesItems.CRACKED_WRAPTOR_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.SPIDER_EYE), new ItemStack(SpeciesItems.WICKED_TREAT.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.FUNCTIONAL_BLOCKS) {
            entries.putAfter(new ItemStack(Items.ZOMBIE_HEAD), new ItemStack(SpeciesItems.WICKED_CANDLE.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.CREEPER_HEAD), new ItemStack(SpeciesItems.BEWEREAGER_HEAD.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(SpeciesItems.BEWEREAGER_HEAD.get()), new ItemStack(SpeciesItems.QUAKE_HEAD.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putBefore(new ItemStack(Items.SKELETON_SKULL), ClientEventsHandler.getHopefulBannerInstance(), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(SpeciesItems.QUAKE_HEAD.get()), new ItemStack(SpeciesItems.GHOUL_HEAD.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.LODESTONE), new ItemStack(SpeciesItems.SPECTRALIBUR_PEDESTAL.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.PEARLESCENT_FROGLIGHT), new ItemStack(SpeciesItems.CHAINDELIER.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.PEARLESCENT_FROGLIGHT), new ItemStack(SpeciesItems.SPECLIGHT.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.PEARLESCENT_FROGLIGHT), new ItemStack(SpeciesItems.HOPELIGHT.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }
        if (key == CreativeModeTabs.SPAWN_EGGS) {
            entries.putAfter(new ItemStack(Items.WITHER_SKELETON_SPAWN_EGG), new ItemStack(SpeciesItems.WRAPTOR_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.LLAMA_SPAWN_EGG), new ItemStack(SpeciesItems.LIMPET_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.DROWNED_SPAWN_EGG), new ItemStack(SpeciesItems.DEEPFISH_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.BEE_SPAWN_EGG), new ItemStack(SpeciesItems.BIRT_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.RABBIT_SPAWN_EGG), new ItemStack(SpeciesItems.STACKATICK_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.GHAST_SPAWN_EGG), new ItemStack(SpeciesItems.GHOUL_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.PUFFERFISH_SPAWN_EGG), new ItemStack(SpeciesItems.QUAKE_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putBefore(new ItemStack(Items.SPIDER_SPAWN_EGG), new ItemStack(SpeciesItems.SPECTRE_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putBefore(new ItemStack(Items.WITCH_SPAWN_EGG), new ItemStack(SpeciesItems.WICKED_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
            entries.putAfter(new ItemStack(Items.BEE_SPAWN_EGG), new ItemStack(SpeciesItems.BEWEREAGER_SPAWN_EGG.get()), CreativeModeTab.TabVisibility.PARENT_AND_SEARCH_TABS);
        }

    }

    @SubscribeEvent
    public static void registerParticleTypes(RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(SpeciesParticles.SNORING.get(), SnoringParticle.Factory::new);
        event.registerSpriteSet(SpeciesParticles.BIRTD.get(), RotatingParticle.BirtdFactory::new);
        event.registerSpriteSet(SpeciesParticles.FOOD.get(), RotatingParticle.FoodFactory::new);
        event.registerSpriteSet(SpeciesParticles.ASCENDING_DUST.get(), AscendingDustParticle.Factory::new);
        event.registerSpriteSet(SpeciesParticles.TREEPER_LEAF.get(), TreeperLeafParticle.Factory::new);
        event.registerSpriteSet(SpeciesParticles.YOUTH_POTION.get(), IchorBottleParticle.Factory::new);
        event.registerSpriteSet(SpeciesParticles.ICHOR.get(), IchorParticle.Factory::new);
        event.registerSpriteSet(SpeciesParticles.DRIPPING_PELLET_DRIP.get(), DripParticle.PelletDripHangProvider::new);
        event.registerSpriteSet(SpeciesParticles.FALLING_PELLET_DRIP.get(), DripParticle.PelletDripFallProvider::new);
        event.registerSpriteSet(SpeciesParticles.LANDING_PELLET_DRIP.get(), DripParticle.PelletDripLandProvider::new);
        event.registerSpriteSet(SpeciesParticles.GHOUL_SEARCHING.get(), GhoulSearchParticle.Provider::new);
        event.registerSpriteSet(SpeciesParticles.GHOUL_SEARCHING2.get(), GhoulSearchParticle.Provider::new);
        event.registerSpriteSet(SpeciesParticles.KINETIC_ENERGY.get(), KineticEnergyParticle.BigProvider::new);
        event.registerSpriteSet(SpeciesParticles.SMALL_KINETIC_ENERGY.get(), KineticEnergyParticle.SmallProvider::new);
        event.registerSpriteSet(SpeciesParticles.WICKED_FLAME.get(), FlameParticle.Provider::new);
        event.registerSpriteSet(SpeciesParticles.WICKED_EMBER.get(), WickedEmberParticle.Factory::new);
        event.registerSpriteSet(SpeciesParticles.POOF.get(), PoofParticle.Provider::new);
        event.registerSpriteSet(SpeciesParticles.SPECTRALIBUR.get(), SpectraliburParticle.Provider::new);
        event.registerSpriteSet(SpeciesParticles.SPECTRALIBUR_INVERTED.get(), SpectraliburParticle.ProviderInverted::new);
        event.registerSpriteSet(SpeciesParticles.SPECTRALIBUR_RELEASED.get(), SpectraliburReleasedParticle.Provider::new);
        event.registerSpriteSet(SpeciesParticles.SPECTRE_SMOKE.get(), SpectreSmokeParticle.Factory::new);
        event.registerSpriteSet(SpeciesParticles.ASCENDING_SPECTRE_SMOKE.get(), AscendingSpectreParticle.SmokeFactory::new);
        event.registerSpriteSet(SpeciesParticles.SPECTRE_POP.get(), SpectrePopParticle.Factory::new);
        event.registerSpriteSet(SpeciesParticles.BROKEN_LINK.get(), BrokenLinkParticle.Factory::new);
        event.registerSpriteSet(SpeciesParticles.COLLECTED_SOUL.get(), AscendingSpectreParticle.SoulFactory::new);
        event.registerSpriteSet(SpeciesParticles.BEWEREAGER_HOWL.get(), BewereagerHowlParticle.Provider::new);
        event.registerSpriteSet(SpeciesParticles.BEWEREAGER_SPEED.get(), BewereagerParticle.Provider::new);
        event.registerSpriteSet(SpeciesParticles.BEWEREAGER_SLOW.get(), BewereagerParticle.ProviderInverted::new);
        event.registerSpriteSet(SpeciesParticles.DRIPPING_HANGER_SALIVA.get(), DripParticle.HangerSalivaHangProvider::new);
        event.registerSpriteSet(SpeciesParticles.FALLING_HANGER_SALIVA.get(), DripParticle.HangerSalivaFallProvider::new);
        event.registerSpriteSet(SpeciesParticles.LANDING_HANGER_SALIVA.get(), DripParticle.HangerSalivaLandProvider::new);
        event.registerSpriteSet(SpeciesParticles.HANGER_CRIT.get(), HangerCritParticle.Provider::new);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(SpeciesEntities.WRAPTOR.get(), WraptorRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.STACKATICK.get(), StackatickRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.DEEPFISH.get(), DeepfishRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.BIRT.get(), BirtRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.BIRT_EGG.get(), ThrownItemRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.LIMPET.get(), LimpetRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.TREEPER.get(), TreeperRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.TROOPER.get(), TrooperRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.GOOBER.get(), GooberRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.GOOBER_GOO.get(), GooberGooRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.CRUNCHER.get(), CruncherRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.MAMMUTILATION.get(), MammutilationRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.SPRINGLING.get(), SpringlingRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.CRUNCHER_PELLET.get(), FallingBlockRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.GHOUL.get(), GhoulRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.QUAKE.get(), QuakeRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.DEFLECTOR_DUMMY.get(), DeflectorDummyRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.SPECTRE.get(), SpectreRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.WICKED.get(), WickedRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.WICKED_FIREBALL.get(), WickedFireballRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.WICKED_SWAPPER.get(), WickedSwapperProjectileRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.BEWEREAGER.get(), BewereagerRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.LEAF_HANGER.get(), LeafHangerRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.CLIFF_HANGER.get(), CliffHangerRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.COIL.get(), CoilRenderer::new);
        event.registerEntityRenderer(SpeciesEntities.HARPOON.get(), HarpoonRenderer::new);
    }

    @SubscribeEvent
    public static void registerEntityLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(SpeciesEntityModelLayers.LIMPET, LimpetModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.DEEPFISH, DeepfishModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.STACKATICK, StackatickModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.BIRT, BirtModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.WRAPTOR, WraptorModel::createBodyLayer);
        event.registerLayerDefinition(SpeciesEntityModelLayers.TREEPER, TreeperModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.TROOPER, TrooperModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.GOOBER, GooberModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.GOOBER_GOO, GooberGooModel::createLayer);
        event.registerLayerDefinition(SpeciesEntityModelLayers.CRUNCHER, CruncherModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.MAMMUTILATION, MammutilationModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.SPRINGLING, SpringlingModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.GHOUL, GhoulModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.GHOUL_HEAD, GhoulHeadModel::createBodyLayer);
        event.registerLayerDefinition(SpeciesEntityModelLayers.QUAKE, QuakeModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.QUAKE_HEAD, QuakeHeadModel::createBodyLayer);
        event.registerLayerDefinition(SpeciesEntityModelLayers.DEFLECTOR_DUMMY, DeflectorDummyModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.BEWEREAGER, BewereagerModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.BEWEREAGER_HEAD, BewereagerHeadModel::createBodyLayer);
        event.registerLayerDefinition(SpeciesEntityModelLayers.SPECTRE, SpectreModel::createBodyLayer);
        event.registerLayerDefinition(SpeciesEntityModelLayers.SABLE_SPECTRE, SpectreModel::createSableBodyLayer);
        event.registerLayerDefinition(SpeciesEntityModelLayers.JOUSTING_SPECTRE, SpectreModel::createJoustingBodyLayer);
        event.registerLayerDefinition(SpeciesEntityModelLayers.WICKED, WickedModel::getLayerDefinition);
        event.registerLayerDefinition(SpeciesEntityModelLayers.WICKED_FIREBALL, WickedFireballRenderer::createLayer);
        event.registerLayerDefinition(SpeciesEntityModelLayers.WICKED_CANDLE, WickedHeadModel::createBodyLayer);
        event.registerLayerDefinition(SpeciesEntityModelLayers.LEAF_HANGER, LeafHangerModel::createBodyLayer);
        event.registerLayerDefinition(SpeciesEntityModelLayers.CLIFF_HANGER, CliffHangerModel::createBodyLayer);
        event.registerLayerDefinition(SpeciesEntityModelLayers.COIL, CoilRenderer::createBodyLayer);
        event.registerLayerDefinition(SpeciesEntityModelLayers.COIL_KNOT, CoilRenderer::createKnotBodyLayer);
    }

    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> {
            if (tintIndex == 0) {
                if (stack.getItem() instanceof SpectreLightBlockItem spectreLightBlockItem) {
                    return spectreLightBlockItem.getColor(stack);
                } return 0x7CF2F5;
            }
            return 0xFFFFFF;
        },
                SpeciesBlocks.SPECLIGHT.get(),
                SpeciesBlocks.HOPELIGHT.get()
        );
    }

    @SubscribeEvent
    public static void registerBlockEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerBlockEntityRenderer(SpeciesBlockEntities.SPECTRALIBUR.get(), SpectraliburPedestalBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(SpeciesBlockEntities.SPECLIGHT.get(), SpeclightBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(SpeciesBlockEntities.HOPELIGHT.get(), HopelightBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(SpeciesBlockEntities.CHAINDELIER.get(), ChaindelierBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(SpeciesBlockEntities.MOB_HEAD.get(), MobHeadBlockEntityRenderer::new);
        event.registerBlockEntityRenderer(SpeciesBlockEntities.BIRTDAY_CAKE.get(), BirtdayCakeBlockEntityRenderer::new);
    }

    @SubscribeEvent
    public static void registerKeys(RegisterKeyMappingsEvent event) {
        event.register(SpeciesKeyMappings.EXTEND_KEY);
        event.register(SpeciesKeyMappings.RETRACT_KEY);
    }

    @SubscribeEvent
    public static void onRegisterSpectatorShaders(RegisterEntitySpectatorShadersEvent event) {
        event.register(SpeciesEntities.GHOUL.get(), new ResourceLocation(Species.MOD_ID, "shaders/post/blind.json"));
        event.register(SpeciesEntities.BEWEREAGER.get(), new ResourceLocation(Species.MOD_ID, "shaders/post/dog_vision.json"));
        event.register(SpeciesEntities.WICKED.get(), new ResourceLocation(Species.MOD_ID, "shaders/post/shadow.json"));
        event.register(SpeciesEntities.QUAKE.get(), new ResourceLocation(Species.MOD_ID, "shaders/post/clank.json"));
    }

}
