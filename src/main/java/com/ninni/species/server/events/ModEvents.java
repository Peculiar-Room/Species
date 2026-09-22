package com.ninni.species.server.events;

import com.ninni.species.Species;
import com.ninni.species.registry.*;
import com.ninni.species.server.entity.mob.update_1.*;
import com.ninni.species.server.entity.mob.update_2.*;
import com.ninni.species.server.entity.mob.update_3.*;
import com.ninni.species.server.item.MobHeadItem;
import com.ninni.species.server.item.SpectraliburItem;
import com.ninni.species.server.item.WickedMaskItem;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.Util;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.dispenser.ProjectileDispenseBehavior;
import net.minecraft.core.dispenser.DefaultDispenseItemBehavior;
import net.minecraft.core.dispenser.DispenseItemBehavior;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.DispensibleContainerItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.level.levelgen.Heightmap;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.common.BasicItemListing;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.brewing.RegisterBrewingRecipesEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.RegisterSpawnPlacementsEvent;
import net.neoforged.neoforge.event.entity.living.*;
import net.neoforged.neoforge.event.village.VillagerTradesEvent;

import java.util.List;
import java.util.function.Consumer;

@EventBusSubscriber(modid = Species.MOD_ID)
public class ModEvents {
    static final DispenseItemBehavior dispenseBucket = new DefaultDispenseItemBehavior() {
        private final DefaultDispenseItemBehavior defaultDispenseItemBehavior = new DefaultDispenseItemBehavior();

        public ItemStack execute(BlockSource p_123561_, ItemStack p_123562_) {
            DispensibleContainerItem dispensiblecontaineritem = (DispensibleContainerItem)p_123562_.getItem();
            BlockPos blockpos = p_123561_.pos().relative(p_123561_.state().getValue(DispenserBlock.FACING));
            Level level = p_123561_.level();
            if (dispensiblecontaineritem.emptyContents(null, level, blockpos, null, p_123562_)) {
                dispensiblecontaineritem.checkExtraContent(null, level, p_123562_, blockpos);
                return new ItemStack(Items.BUCKET);
            } else {
                return this.defaultDispenseItemBehavior.dispense(p_123561_, p_123562_);
            }
        }
    };

    @SubscribeEvent
    public static void registerSpawnPlacements(RegisterSpawnPlacementsEvent event) {
        event.register(SpeciesEntities.WRAPTOR.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Wraptor::canSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.DEEPFISH.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Deepfish::canSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.STACKATICK.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, Stackatick::canSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.BIRT.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, Birt::canSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.LIMPET.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Limpet::canSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.TREEPER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Treeper::canSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.GOOBER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, Goober::canSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.CRUNCHER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.WORLD_SURFACE_WG, Cruncher::canSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.MAMMUTILATION.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Mammutilation::canSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.SPRINGLING.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.WORLD_SURFACE, Springling::canSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.GHOUL.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Ghoul::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.QUAKE.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Quake::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.WICKED.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Wicked::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.BEWEREAGER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, Bewereager::checkMonsterSpawnRules, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.CLIFF_HANGER.get(), SpawnPlacementTypes.ON_GROUND, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, CliffHanger::canSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
        event.register(SpeciesEntities.LEAF_HANGER.get(), SpawnPlacementTypes.IN_WATER, Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, LeafHanger::canSpawn, RegisterSpawnPlacementsEvent.Operation.OR);
    }

    @SubscribeEvent
    public static void registerEntityAttributes(EntityAttributeCreationEvent event) {
        event.put(SpeciesEntities.WRAPTOR.get(), Wraptor.createWraptorAttributes().build());
        event.put(SpeciesEntities.DEEPFISH.get(), Deepfish.createDeepfishAttributes().build());
        event.put(SpeciesEntities.STACKATICK.get(), Stackatick.createAttributes().build());
        event.put(SpeciesEntities.BIRT.get(), Birt.createBirtAttributes().build());
        event.put(SpeciesEntities.LIMPET.get(), Limpet.createLimpetAttributes().build());
        event.put(SpeciesEntities.TREEPER.get(), Treeper.createAttributes().build());
        event.put(SpeciesEntities.TROOPER.get(), Trooper.createAttributes().build());
        event.put(SpeciesEntities.GOOBER.get(), Goober.createAttributes().build());
        event.put(SpeciesEntities.CRUNCHER.get(), Cruncher.createAttributes().build());
        event.put(SpeciesEntities.MAMMUTILATION.get(), Mammutilation.createAttributes().build());
        event.put(SpeciesEntities.SPRINGLING.get(), Springling.createAttributes().build());
        event.put(SpeciesEntities.GHOUL.get(), Ghoul.createAttributes().build());
        event.put(SpeciesEntities.QUAKE.get(), Quake.createAttributes().build());
        event.put(SpeciesEntities.DEFLECTOR_DUMMY.get(), DeflectorDummy.createAttributes().build());
        event.put(SpeciesEntities.SPECTRE.get(), Spectre.createAttributes().build());
        event.put(SpeciesEntities.WICKED.get(), Wicked.createAttributes().build());
        event.put(SpeciesEntities.BEWEREAGER.get(), Bewereager.createAttributes().build());
        event.put(SpeciesEntities.LEAF_HANGER.get(), LeafHanger.createAttributes().build());
        event.put(SpeciesEntities.CLIFF_HANGER.get(), CliffHanger.createAttributes().build());
    }

    @SubscribeEvent // on the game event bus
    public static void registerBrewingRecipes(RegisterBrewingRecipesEvent event) {
        PotionBrewing.Builder builder = event.getBuilder();

        builder.addMix(
                Potions.AWKWARD,
                SpeciesItems.GHOUL_TONGUE.get(),
                SpeciesPotions.BLOODLUST
        );
    }

    @SubscribeEvent
    public static void onVillagerTraderInit(VillagerTradesEvent event) {
        VillagerProfession type = event.getType();
        Int2ObjectMap<List<VillagerTrades.ItemListing>> trades = event.getTrades();
        if (type == VillagerProfession.CLERIC) {
            trades.get(4).add(new BasicItemListing(new ItemStack(SpeciesItems.GHOUL_TONGUE.get()), ItemStack.EMPTY, new ItemStack(Items.EMERALD, 3), 12, 8, 0.2F));
        }
    }

    @SubscribeEvent
    public static void onShieldBlock(LivingShieldBlockEvent event) {
        LivingEntity livingEntity = event.getEntity();
        DamageSource damageSource = event.getDamageSource();
        if (livingEntity instanceof Player player && (damageSource.getEntity() instanceof Cruncher || (damageSource.getEntity() instanceof Quake && event.getBlockedDamage() > 40))) {
            player.disableShield();
        }

        if (event.getEntity().getUseItem().is(SpeciesItems.RICOSHIELD.get())) {
            CompoundTag tag = event.getEntity().getUseItem().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            if (tag.contains("StoredDamage")) {
                tag.putFloat("StoredDamage", Math.min(tag.getFloat("StoredDamage") + event.getBlockedDamage(), 40));
            }
            else {
                tag.putFloat("StoredDamage", Math.min(event.getBlockedDamage(), 40));
            }
            event.getEntity().level().playSound(null, event.getEntity().blockPosition(), SpeciesSoundEvents.RICOSHIELD_ABSORB.get(), SoundSource.PLAYERS, 1F, tag.getFloat("StoredDamage") * 0.05F);
            CustomData.set(DataComponents.CUSTOM_DATA, event.getEntity().getUseItem(), tag);
        }
    }

    @SubscribeEvent
    public static void onLivingTick(LivingBreatheEvent event) {
        LivingEntity livingEntity = event.getEntity();
        Level level = livingEntity.level();
        if (!level.isClientSide) {
            if (livingEntity.hasEffect(SpeciesStatusEffects.BLOODLUST)) {
                BlockPos blockpos = BlockPos.containing(livingEntity.getX(), livingEntity.getEyeY(), livingEntity.getZ());
                float f = livingEntity.getLightLevelDependentMagicValue();
                if (f > 0.5F && level.random.nextFloat() * 30.0F < (f - 0.4F) * 2.0F && !livingEntity.isInWaterOrBubble() && level.canSeeSky(blockpos) && level.isDay()) {
                    level.playSound(null, livingEntity, SpeciesSoundEvents.BLOODLUST_REMOVED.get(), SoundSource.PLAYERS, 1.0F, 1.0F);
                    livingEntity.removeEffect(SpeciesStatusEffects.BLOODLUST);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onMobEventApplied(MobEffectEvent.Applicable event) {
        LivingEntity livingEntity = event.getEntity();
        MobEffectInstance mobEffectInstance = event.getEffectInstance();
        if (livingEntity.hasEffect(SpeciesStatusEffects.WITHER_RESISTANCE) && mobEffectInstance.getEffect() == MobEffects.WITHER) {
            event.setResult(MobEffectEvent.Applicable.Result.DO_NOT_APPLY);
        }
    }

    @SubscribeEvent
    public static void onLivingHurt(LivingDamageEvent.Pre event) {
        LivingEntity attacked = event.getEntity();
        DamageSource source = event.getSource();
        float amount = event.getOriginalDamage();


        //Replenish hunger when killing a mob with the Bloodlust effect
        if (source.getEntity() instanceof Player player && player.hasEffect(SpeciesStatusEffects.BLOODLUST)) {
            if (amount > attacked.getHealth()) {
                attacked.level().playSound(null, attacked.getX(), attacked.getY(), attacked.getZ(), SpeciesSoundEvents.BLOODLUST_FEED.get(), attacked.getSoundSource(), 1, 1);
                player.getFoodData().eat((int) (attacked.getMaxHealth() / 5), ((attacked.getMaxHealth() / 5F) * 0.1F));
            }
        }

        //Making mob explode when having the Combustion effect
        if (attacked.hasEffect(SpeciesStatusEffects.COMBUSTION) && amount > attacked.getHealth()) {
            int amplifier = attacked.getEffect(SpeciesStatusEffects.COMBUSTION).getAmplifier();
            attacked.level().explode(attacked, attacked.getX(), attacked.getY(0.0625D), attacked.getZ(), amplifier, Level.ExplosionInteraction.MOB);
            attacked.level().getEntitiesOfClass(LivingEntity.class, attacked.getBoundingBox().inflate(2), (livingEntity) -> livingEntity.isAlive() && !livingEntity.is(attacked)).forEach(livingEntity -> livingEntity.hurt(attacked.level().damageSources().mobAttack(attacked), 6));
            attacked.removeEffect(SpeciesStatusEffects.COMBUSTION);
        }

        //Spectralibur
        if (source.getEntity() instanceof Player player && player.getMainHandItem().getItem() instanceof SpectraliburItem && !(attacked.getType().is(SpeciesTags.SOULLESS))) {
            if (amount > attacked.getHealth()) {

                //Storing souls in Spectralibur
                var tag = player.getMainHandItem().getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
                if (!(tag.contains("Souls") && tag.getInt("Souls") == 5)) {
                    CompoundTag newTag = tag.copy();
                    newTag.putInt("Souls", Math.min ((tag.getInt("Souls") + 1), 5));
                    if (player.level() instanceof ServerLevel serverLevel) {
                        serverLevel.playSound(null, attacked.getX(), attacked.getY(), attacked.getZ(), SpeciesSoundEvents.SPECTRALIBUR_COLLECT_SOUL.get(), SoundSource.PLAYERS, 1, 1);
                        serverLevel.sendParticles(SpeciesParticles.COLLECTED_SOUL.get(), attacked.getX(), attacked.getY() + 0.2F, attacked.getZ(), 1, 0,0,0, 0);
                    }
                    CustomData.set(DataComponents.CUSTOM_DATA, player.getMainHandItem(), newTag);
                }
            }
        }

    }

    @SubscribeEvent
    public static void onTagsUpdated(TagsUpdatedEvent event) {
        //DispenserBlock.registerBehavior(SpeciesItems.BIRT_EGG.get(), new ProjectileDispenseBehavior(SpeciesItems.BIRT_EGG.get()));
        DispenserBlock.registerBehavior(SpeciesItems.DEFLECTOR_DUMMY.get(), new DefaultDispenseItemBehavior() {
            public ItemStack execute(BlockSource blockSource, ItemStack stack) {
                Direction direction = blockSource.state().getValue(DispenserBlock.FACING);
                BlockPos blockpos = blockSource.pos().relative(direction);
                ServerLevel serverlevel = blockSource.level();
                //Consumer<DeflectorDummy> consumer = EntityType.appendDefaultStackConfig((p_277236_) -> p_277236_.setYRot(direction.toYRot()), serverlevel, stack, null);
                DeflectorDummy dummy = SpeciesEntities.DEFLECTOR_DUMMY.get().spawn(serverlevel, stack, null, blockpos, MobSpawnType.DISPENSER, false, false);
                if (dummy != null) stack.shrink(1);
                return stack;
            }
        });
        DispenserBlock.registerBehavior(SpeciesItems.WICKED_MASK.get(), WickedMaskItem.DISPENSE_ITEM_BEHAVIOR);
        DispenserBlock.registerBehavior(SpeciesItems.WICKED_CANDLE.get(), MobHeadItem.DISPENSE_ITEM_BEHAVIOR);
        DispenserBlock.registerBehavior(SpeciesItems.QUAKE_HEAD.get(), MobHeadItem.DISPENSE_ITEM_BEHAVIOR);
        DispenserBlock.registerBehavior(SpeciesItems.GHOUL_HEAD.get(), MobHeadItem.DISPENSE_ITEM_BEHAVIOR);
        DispenserBlock.registerBehavior(SpeciesItems.BEWEREAGER_HEAD.get(), MobHeadItem.DISPENSE_ITEM_BEHAVIOR);
        DispenserBlock.registerBehavior(SpeciesItems.DEEPFISH_BUCKET.get(), dispenseBucket);
    }

    @SubscribeEvent
    public static void register(AddReloadListenerEvent event) {
        event.addListener(Species.PROXY.getLimpetOreManager());
        event.addListener(Species.PROXY.getGooberGooManager());
        event.addListener(Species.PROXY.getCruncherPelletManager());
    }
}
