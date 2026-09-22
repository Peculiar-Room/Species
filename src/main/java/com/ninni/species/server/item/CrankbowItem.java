package com.ninni.species.server.item;

import com.ninni.species.mixin_util.AbstractArrowAccess;
import com.ninni.species.registry.SpeciesEnchantments;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.BundleContents;
import net.minecraft.world.item.component.ChargedProjectiles;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CrankbowItem extends ProjectileWeaponItem {
    public static final String TAG_SHOTS_FIRED = "Speed";
    public static final String TAG_COOLDOWN = "Cooldown";
    public static final String TAG_USING = "IsUsing";

    public CrankbowItem() {
        super(new Properties().stacksTo(1).durability(865));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY).isEmpty()) {
            CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
            tag.remove(TAG_SHOTS_FIRED);
            tag.remove(TAG_COOLDOWN);
            CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);

            player.startUsingItem(hand);
            return InteractionResultHolder.consume(stack);
        }
        return InteractionResultHolder.fail(stack);
    }

    @Override
    public void onUseTick(Level level, LivingEntity entity, ItemStack stack, int time) {
        if (stack.getDamageValue() >= stack.getMaxDamage()) {
            if (entity instanceof Player player) player.stopUsingItem();
            return;
        }

        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();

        if (tag.contains(TAG_COOLDOWN)) {
            int cooldown = tag.getInt(TAG_COOLDOWN);
            if (cooldown > 0) tag.putInt(TAG_COOLDOWN, cooldown - 1);
        }

        var chargedProjectiles = stack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        if (!chargedProjectiles.isEmpty()) {
            if (!tag.contains(TAG_COOLDOWN)) {
                if (stack.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(SpeciesEnchantments.QUICK_CRANK)) > 0) entity.playSound(SpeciesSoundEvents.CRANKBOW_PULL_QUICK.get());
                else entity.playSound(SpeciesSoundEvents.CRANKBOW_PULL.get());
                tag.putInt(TAG_COOLDOWN, getShootingCooldown(stack, level.registryAccess()));
                tag.putInt(TAG_SHOTS_FIRED, 0);
            } else {
                if (tag.getInt(TAG_COOLDOWN) == 0) {
                    if (stack.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(SpeciesEnchantments.QUICK_CRANK)) > 0) entity.playSound(SpeciesSoundEvents.CRANKBOW_PULL_QUICK.get());
                    else entity.playSound(SpeciesSoundEvents.CRANKBOW_PULL.get());

                    int shotsFired = tag.getInt(TAG_SHOTS_FIRED);
                    float v = shotsFired / 20f;

                    if (shotsFired % 5 == 0 && shotsFired != 0 && shotsFired <= 30) {
                        entity.playSound(SpeciesSoundEvents.CRANKBOW_SPEED.get(), 0.5F,v + 0.5F);
                    }

                    if (level instanceof ServerLevel serverLevel) {
                        shoot(serverLevel, entity, entity.getUsedItemHand(), stack, chargedProjectiles.getItems(), 1f, 1f, false, null);
                    }
                    tag.putInt(TAG_COOLDOWN, getShootingCooldown(stack, level.registryAccess()));
                    if (shotsFired < 40) tag.putInt(TAG_SHOTS_FIRED, shotsFired + 1);
                }
            }
            if (!level.isClientSide) tag.putBoolean(TAG_USING, true);
        } else {
            if (entity instanceof Player player) {
                tag.remove(TAG_SHOTS_FIRED);
                tag.remove(TAG_COOLDOWN);
                player.stopUsingItem();
            }
        }

        CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        tag.remove(TAG_COOLDOWN);

        if (livingEntity instanceof Player player && tag.contains(TAG_SHOTS_FIRED)) {
            int shots = tag.getInt(TAG_SHOTS_FIRED);
            player.getCooldowns().addCooldown(this, shots * 8);
            if (shots > 7) livingEntity.playSound(SpeciesSoundEvents.CRANKBOW_STOP.get());
            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < shots / 2; i++) {
                    serverLevel.sendParticles(
                            SpeciesParticles.BEWEREAGER_SLOW.get(),
                            livingEntity.getX() + livingEntity.getRandom().nextGaussian() * 0.5,
                            livingEntity.getY(1F) + livingEntity.getRandom().nextFloat(),
                            livingEntity.getZ() + livingEntity.getRandom().nextGaussian() * 0.5,
                            1, 0.3, 0.3, 0.3, 1.0D
                    );
                }
            }
            tag.remove(TAG_SHOTS_FIRED);
            tag.remove(TAG_USING);
            CustomData.set(DataComponents.CUSTOM_DATA, stack, tag);
        }
    }

    protected void shoot(ServerLevel level, LivingEntity shooter, InteractionHand hand, ItemStack weapon, List<ItemStack> projectileItems, float originalVelocity, float inaccuracy, boolean isCrit, @Nullable LivingEntity target) {
        CompoundTag tag = weapon.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (weapon.has(DataComponents.CUSTOM_DATA) && weapon.has(DataComponents.CHARGED_PROJECTILES) && tag.contains(TAG_SHOTS_FIRED)) {
            var projectiles = weapon.get(DataComponents.CHARGED_PROJECTILES);
            if (projectiles.isEmpty() || projectiles.getItems().isEmpty()) return;

            ItemStack arrowStack = projectiles.getItems().getFirst();
            int shotsFired = tag.getInt(TAG_SHOTS_FIRED);
            float v = shotsFired / 20f;
            float velocity = v + 1.15f;

            float spread = (2 + (getMaxSpeed(weapon, level.registryAccess()) - getShootingCooldown(weapon, level.registryAccess())) / (float) getMaxSpeed(weapon, level.registryAccess())) / 2f;
            for (int i = 0; i < spread * 10; i++) {
                level.sendParticles(
                        SpeciesParticles.BEWEREAGER_SPEED.get(),
                        shooter.getRandomX(0.35D),
                        shooter.getY(0.35D) + shooter.getRandom().nextFloat(),
                        shooter.getRandomZ(0.35D),
                        1, 0.3, 0.3, 0.3, 1.0D
                );
            }

            if (weapon.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(SpeciesEnchantments.SCATTERSHOT)) > 0) {
                if (shotsFired <= 10) {
                    float z = (v * -15) + 10;
                    shootProjectile(level, shooter, hand, weapon, arrowStack, 1.0F, velocity, 1.0F, z, true);
                    shootProjectile(level, shooter, hand, weapon, arrowStack, 1.0F, velocity, 1.0F, -z, true);
                }

                if (shotsFired <= 5) {
                    float z = (v * -40) + 20;
                    shootProjectile(level, shooter, hand, weapon, arrowStack, 1.0F, velocity,1.0F, z, true);
                    shootProjectile(level, shooter, hand, weapon, arrowStack, 1.0F, velocity,1.0F, -z, true);
                }
            }
            shootProjectile(level, shooter, hand, weapon, arrowStack, 1.0F, velocity,1.0F, 0, false);
        }
    }

    @Override
    protected void shootProjectile(LivingEntity livingEntity, Projectile projectile, int i, float v, float v1, float v2, @Nullable LivingEntity livingEntity1) {

    }

    public static int getShootingCooldown(ItemStack stack, RegistryAccess access) {
        CompoundTag tag = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (tag.contains(TAG_SHOTS_FIRED)) {
            int shots = tag.getInt(TAG_SHOTS_FIRED);
            int level = stack.getEnchantmentLevel(access.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(SpeciesEnchantments.QUICK_CRANK));
            int i = level == 0 ? 3 : Math.max(1, 4 - level);

            int cooldown = getMinSpeed(stack, access);
            if (shots == 0) cooldown = getMinSpeed(stack, access);
            else if (shots <= 1) cooldown -= i;
            else if (shots <= 5) cooldown -= 2 * i;
            else if (shots <= 10) cooldown -= 5 * i;
            else if (shots <= 20) cooldown -= 10 * i;
            else if (shots <= 30) cooldown -= 20 * i;
            else cooldown -= 30 * i;

            return Math.max(getMaxSpeed(stack, access), Math.min(40, cooldown));
        }
        return getMinSpeed(stack, access);
    }

    public static int getMaxSpeed(ItemStack stack, RegistryAccess access) {
        return switch (stack.getEnchantmentLevel(access.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(SpeciesEnchantments.QUICK_CRANK))) {
            case 1, 2 -> 6;
            case 3 -> 5;
            default -> 7;
        };
    }

    public static int getMinSpeed(ItemStack stack, RegistryAccess access) {
        return switch (stack.getEnchantmentLevel(access.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(SpeciesEnchantments.QUICK_CRANK))) {
            case 1 -> 25;
            case 2 -> 20;
            case 3 -> 15;
            default -> 30;
        };
    }

    public static int getMaxWeight(ItemStack stack, HolderLookup.Provider access) {
        return 128 + (stack.getEnchantmentLevel(access.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(SpeciesEnchantments.CAPACITY)) * 64);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }

    public int getUseDuration(ItemStack stack, LivingEntity entity) {
        return 72000;
    }



    private static void shootProjectile(Level level, LivingEntity livingEntity, InteractionHand hand, ItemStack stack, ItemStack stack1, float pitch, float x, float y, float z, boolean fromScattershot) {
        if (!level.isClientSide) {
            ArrowItem arrowitem = (ArrowItem)(stack1.getItem() instanceof ArrowItem ? stack1.getItem() : Items.ARROW);
            AbstractArrow abstractarrow = arrowitem.createArrow(level, stack1, livingEntity, stack);
            if (abstractarrow instanceof AbstractArrowAccess access) access.setTgnoreImmunityFrame(true);

            Vec3 vec31 = livingEntity.getUpVector(1.0F);
            Quaternionf quaternionf = (new Quaternionf()).setAngleAxis((z * 0.017453292F), vec31.x, vec31.y, vec31.z);
            Vec3 vec3 = livingEntity.getViewVector(1.0F);
            Vector3f vector3f = vec3.toVector3f().rotate(quaternionf);
            abstractarrow.shoot(vector3f.x(), vector3f.y(), vector3f.z(), x, y);

            if (livingEntity instanceof Player player) {
                boolean flag1 = player.getAbilities().instabuild || arrowitem.isInfinite(stack1, stack, player) || fromScattershot;
                if (flag1 || player.getAbilities().instabuild && (stack1.is(Items.SPECTRAL_ARROW) || stack1.is(Items.TIPPED_ARROW))) {
                    abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }

                if (!flag1 && !player.getAbilities().instabuild) {
                    int sparingLevel = stack.getEnchantmentLevel(level.registryAccess().lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(SpeciesEnchantments.SPARING));
                    int i = sparingLevel == 1 ? 10 : sparingLevel == 2 ? 5 : 3;
                    if (sparingLevel > 0 && level.random.nextInt(i) == 0) {
                        abstractarrow.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                        level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(),  SpeciesSoundEvents.CRANKBOW_SHOOT_SPARING.get(), SoundSource.PLAYERS, 1.0F, pitch);
                    } else {
                        removeOneItem(stack);
                        level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SpeciesSoundEvents.CRANKBOW_SHOOT.get(), SoundSource.PLAYERS, 1.0F, pitch);
                    }
                } else {
                    if (!fromScattershot) level.playSound(null, livingEntity.getX(), livingEntity.getY(), livingEntity.getZ(), SpeciesSoundEvents.CRANKBOW_SHOOT.get(), SoundSource.PLAYERS, 1.0F, pitch);
                }
            }

            stack.hurtAndBreak(1, livingEntity, livingEntity.getEquipmentSlotForItem(livingEntity.getItemInHand(hand)));
            level.addFreshEntity(abstractarrow);
        }

    }


    @Override
    public boolean overrideStackedOnOther(ItemStack stack, Slot slot, ClickAction clickAction, Player player) {
        if (stack.getCount() == 1 && clickAction == ClickAction.SECONDARY) {
            ItemStack itemstack = slot.getItem();
            if (itemstack.isEmpty()) {
                this.playRemoveOneSound(player);
                removeOne(stack).ifPresent((p_150740_) -> {
                    add(stack, slot.safeInsert(p_150740_), player.registryAccess());
                });
            } else if (itemstack.getItem().canFitInsideContainerItems() && getAllSupportedProjectiles().test(itemstack)) {
                int i = (getMaxWeight(stack, player.registryAccess()) - getContentWeight(stack));
                int j = add(stack, slot.safeTake(itemstack.getCount(), i, player), player.registryAccess());
                if (j > 0) {
                    this.playInsertSound(player);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack p_150742_, ItemStack added, Slot p_150744_, ClickAction p_150745_, Player player, SlotAccess p_150747_) {
        if (p_150742_.getCount() != 1) {
            return false;
        } else if (p_150745_ == ClickAction.SECONDARY && p_150744_.allowModification(player)) {
            if (added.isEmpty()) {
                removeOne(p_150742_).ifPresent((p_186347_) -> {
                    this.playRemoveOneSound(player);
                    p_150747_.set(p_186347_);
                });
            } else {
                if (getAllSupportedProjectiles().test(added)) {
                    int i = add(p_150742_, added, player.registryAccess());
                    if (i > 0) {
                        this.playInsertSound(player);
                        added.shrink(i);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static int add(ItemStack weapon, ItemStack stack, RegistryAccess access) {
        if (!stack.isEmpty() && stack.getItem().canFitInsideContainerItems()) {
            var projectiles = weapon.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
            var itemList = new ArrayList<>(projectiles.getItems());

            int i = getContentWeight(weapon);
            int k = Math.min(stack.getCount(), getMaxWeight(weapon, access) - i);

            if (k == 0) return 0;
            else {
                Optional<ItemStack> optional = getMatchingItem(stack, itemList);
                if (optional.isPresent()) {
                    ItemStack itemStack = optional.get();

                    if (itemStack.getCount() + k > 64) {
                        int i2 = itemStack.getCount() + k;
                        itemStack.setCount(64);
                        itemList.remove(itemStack);
                        itemList.addFirst(itemStack);

                        ItemStack copiedItemStack = stack.copyWithCount(k);
                        copiedItemStack.setCount(i2-64);
                        itemList.addFirst(copiedItemStack);
                    } else {
                        itemStack.grow(k);
                        itemList.remove(itemStack);
                        itemList.addFirst(itemStack);
                    }
                } else {
                    ItemStack copiedItemStack = stack.copyWithCount(k);
                    itemList.addFirst(copiedItemStack);
                }
                weapon.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(itemList));
                return k;
            }
        } else return 0;
    }


    private static Optional<ItemStack> getMatchingItem(ItemStack stack, List<ItemStack> items) {
        Optional<ItemStack> optional;
        if (stack.is(Items.BUNDLE)) {
            optional = Optional.empty();
        } else {
            Stream<ItemStack> stream = items.stream();
            Objects.requireNonNull(CompoundTag.class);
            stream = stream.filter(CompoundTag.class::isInstance);
            Objects.requireNonNull(CompoundTag.class);
            optional = stream.filter((itemStack) -> ItemStack.isSameItemSameComponents(itemStack, stack)).findFirst();
        }

        return optional;
    }

    public static int getContentWeight(ItemStack itemStack2) {
        return CrankbowItem.getContents(itemStack2).mapToInt(ItemStack::getCount).sum();
    }

    private static Optional<ItemStack> removeOne(ItemStack itemStack) {
        var projectiles = itemStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        if (projectiles.isEmpty()) {
            return Optional.empty();
        } else {
            if (projectiles.getItems().isEmpty()) {
                return Optional.empty();
            } else {
                var itemList = new ArrayList<>(projectiles.getItems());

                ItemStack itemstack = itemList.getFirst();
                itemList.removeFirst();

                itemStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(itemList));
                return Optional.of(itemstack);
            }
        }
    }


    private static Optional<ItemStack> removeOneItem(ItemStack itemStack) {
        var projectiles = itemStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        if (!projectiles.isEmpty() && !projectiles.getItems().isEmpty()) {
            var itemList = new ArrayList<>(projectiles.getItems());
            ItemStack firstStack = itemList.getFirst();

            if (firstStack.getCount() == 1) {
                if (itemList.size() == 1) {
                    itemList.clear();
                }
                else {
                    itemList.removeFirst();
                }
                itemStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(itemList));
                return Optional.empty();
            }

            ItemStack reducedStack = firstStack.copyWithCount(firstStack.getCount()-1);
            itemList.set(0, reducedStack);
            itemStack.set(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.of(itemList));
            return Optional.of(reducedStack);
        }
        return Optional.empty();
    }

    private static Stream<ItemStack> getContents(ItemStack itemStack) {
        var projectiles = itemStack.getOrDefault(DataComponents.CHARGED_PROJECTILES, ChargedProjectiles.EMPTY);
        if (projectiles.isEmpty()) return Stream.empty();
        return projectiles.getItems().stream();
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        ItemUtils.onContainerDestroyed(itemEntity, CrankbowItem.getContents(itemEntity.getItem()).toList());
    }

    private void playRemoveOneSound(Entity entity) {
        entity.playSound(SpeciesSoundEvents.CRANKBOW_REMOVE_ARROW.get(), 0.8f, 0.8f + entity.level().getRandom().nextFloat() * 0.4f);
    }

    private void playInsertSound(Entity entity) {
        entity.playSound(SpeciesSoundEvents.CRANKBOW_LOAD_ARROW.get(), 0.8f, 0.8f + entity.level().getRandom().nextFloat() * 0.4f);
    }

    @Override
    public Optional<TooltipComponent> getTooltipImage(ItemStack itemStack) {
        NonNullList<ItemStack> nonNullList = NonNullList.create();
        CrankbowItem.getContents(itemStack).forEach(nonNullList::add);
        return Optional.of(new BundleTooltip(new BundleContents(nonNullList)));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, TooltipContext context, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.species.crankbow.fullness", CrankbowItem.getContentWeight(itemStack), CrankbowItem.getMaxWeight(itemStack, context.registries())).withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("item.species.crankbow.desc").withStyle(Style.EMPTY.withColor(0x723548)));
        super.appendHoverText(itemStack, context, list, tooltipFlag);
    }

    @Override
    public Predicate<ItemStack> getAllSupportedProjectiles() {
        return stack -> stack.getItem() instanceof ArrowItem;
    }

    @Override
    public boolean isValidRepairItem(ItemStack stack, ItemStack stack1) {
        return stack1.is(SpeciesItems.WEREFANG.get());
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }
}
