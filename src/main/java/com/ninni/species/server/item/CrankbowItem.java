package com.ninni.species.server.item;

import com.ninni.species.registry.*;
import com.ninni.species.server.entity.util.AbstractArrowAccess;
import com.ninni.species.server.packet.CrankbowPullPacket;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.inventory.tooltip.BundleTooltip;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.network.PacketDistributor;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class CrankbowItem extends ProjectileWeaponItem implements Vanishable {
    public static final String TAG_ITEMS = "Items";
    public static final String TAG_SHOTS_FIRED = "Speed";
    public static final String TAG_COOLDOWN = "Cooldown";

    public CrankbowItem() {
        super(new Properties().stacksTo(1).durability(865));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.hasTag() && stack.getTag().contains(TAG_ITEMS)) {
            CompoundTag tag = stack.getTag();
            tag.remove(TAG_SHOTS_FIRED);
            tag.remove(TAG_COOLDOWN);
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

        CompoundTag tag = stack.getOrCreateTag();

        if (tag.contains(TAG_COOLDOWN)) {
            int cooldown = tag.getInt(TAG_COOLDOWN);
            if (cooldown > 0) tag.putInt(TAG_COOLDOWN, cooldown - 1);
        }

        if (tag.contains(TAG_ITEMS)) {
            if (!tag.contains(TAG_COOLDOWN)) {
                if (stack.getEnchantmentLevel(SpeciesEnchantments.QUICK_CRANK.get()) > 0) entity.playSound(SpeciesSoundEvents.CRANKBOW_PULL_QUICK.get());
                else entity.playSound(SpeciesSoundEvents.CRANKBOW_PULL.get());
                tag.putInt(TAG_COOLDOWN, getShootingCooldown(stack));
                tag.putInt(TAG_SHOTS_FIRED, 0);
            } else {
                if (tag.getInt(TAG_COOLDOWN) == 0) {
                    if (stack.getEnchantmentLevel(SpeciesEnchantments.QUICK_CRANK.get()) > 0) entity.playSound(SpeciesSoundEvents.CRANKBOW_PULL_QUICK.get());
                    else entity.playSound(SpeciesSoundEvents.CRANKBOW_PULL.get());
                    shoot(level, entity, entity.getUsedItemHand(), stack);
                    tag.putInt(TAG_COOLDOWN, getShootingCooldown(stack));
                    int shotsFired = tag.getInt(TAG_SHOTS_FIRED);
                    if (shotsFired < 40) tag.putInt(TAG_SHOTS_FIRED, shotsFired + 1);
                }
            }

            if (!level.isClientSide && entity instanceof ServerPlayer serverPlayer) {
                if (entity.isUsingItem() && entity.getUseItem() == stack) {
                    float progress = 1.0f - (tag.getInt(TAG_COOLDOWN) / (float)Math.max(getShootingCooldown(stack), 1));
                    SpeciesNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayer), new CrankbowPullPacket(entity.getUUID(), progress));

                }
            }
        } else {
            if (entity instanceof Player player) {
                tag.remove(TAG_SHOTS_FIRED);
                tag.remove(TAG_COOLDOWN);
                player.stopUsingItem();
            }
        }
    }

    @Override
    public void releaseUsing(ItemStack stack, Level level, LivingEntity livingEntity, int timeLeft) {
        CompoundTag tag = stack.getOrCreateTag();
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
            if (!level.isClientSide && livingEntity instanceof ServerPlayer serverPlayer) {
                SpeciesNetwork.INSTANCE.send(PacketDistributor.TRACKING_ENTITY_AND_SELF.with(() -> serverPlayer), new CrankbowPullPacket(livingEntity.getUUID(), -1.0f));
            }
        }
    }

    private boolean shoot(Level level, LivingEntity entity, InteractionHand hand, ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_ITEMS) && tag.contains(TAG_SHOTS_FIRED)) {
            ListTag items = tag.getList(TAG_ITEMS, 10);
            if (items.isEmpty()) return false;

            ItemStack arrowStack = ItemStack.of(items.getCompound(0));
            int shotsFired = tag.getInt(TAG_SHOTS_FIRED);
            float v = shotsFired / 20f;
            float velocity = v + 1.15f;

            if (shotsFired % 5 == 0 && shotsFired != 0 && shotsFired <= 30) {
                entity.playSound(SpeciesSoundEvents.CRANKBOW_SPEED.get(), 0.5F,v + 0.5F);
            }

            float spread = (2 + (getMaxSpeed(stack) - getShootingCooldown(stack)) / (float) getMaxSpeed(stack)) / 2f;
            if (level instanceof ServerLevel serverLevel) {
                for (int i = 0; i < spread * 10; i++) {
                    serverLevel.sendParticles(
                            SpeciesParticles.BEWEREAGER_SPEED.get(),
                            entity.getRandomX(0.35D),
                            entity.getY(0.35D) + entity.getRandom().nextFloat(),
                            entity.getRandomZ(0.35D),
                            1, 0.3, 0.3, 0.3, 1.0D
                    );
                }
            }

            if (stack.getEnchantmentLevel(SpeciesEnchantments.SCATTERSHOT.get()) > 0) {
                if (shotsFired <= 10) {
                    float z = (v * -15) + 10;
                    shootProjectile(level, entity, hand, stack, arrowStack, 1.0F, velocity, 1.0F, z, true);
                    shootProjectile(level, entity, hand, stack, arrowStack, 1.0F, velocity, 1.0F, -z, true);
                }

                if (shotsFired <= 5) {
                    float z = (v * -40) + 20;
                    shootProjectile(level, entity, hand, stack, arrowStack, 1.0F, velocity,1.0F, z, true);
                    shootProjectile(level, entity, hand, stack, arrowStack, 1.0F, velocity,1.0F, -z, true);
                }
            }
            shootProjectile(level, entity, hand, stack, arrowStack, 1.0F, velocity,1.0F, 0, false);
            return true;
        }
        return false;
    }

    public static int getShootingCooldown(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(TAG_SHOTS_FIRED)) {
            int shots = tag.getInt(TAG_SHOTS_FIRED);
            int level = stack.getEnchantmentLevel(SpeciesEnchantments.QUICK_CRANK.get());
            int i = level == 0 ? 3 : 4 - level;

            int cooldown = getMinSpeed(stack);
            if (shots == 0) cooldown = getMinSpeed(stack);
            else if (shots <= 1) cooldown -= i;
            else if (shots <= 5) cooldown -= 2 * i;
            else if (shots <= 10) cooldown -= 5 * i;
            else if (shots <= 20) cooldown -= 10 * i;
            else if (shots <= 30) cooldown -= 20 * i;
            else cooldown -= 30 * i;

            return Math.max(getMaxSpeed(stack), Math.min(40, cooldown));
        }
        return getMinSpeed(stack);
    }

    public static int getMaxSpeed(ItemStack stack) {
        return switch (stack.getEnchantmentLevel(SpeciesEnchantments.QUICK_CRANK.get())) {
            case 1, 2 -> 6;
            case 3 -> 5;
            default -> 7;
        };
    }

    public static int getMinSpeed(ItemStack stack) {
        return switch (stack.getEnchantmentLevel(SpeciesEnchantments.QUICK_CRANK.get())) {
            case 1 -> 25;
            case 2 -> 20;
            case 3 -> 15;
            default -> 30;
        };
    }

    public static int getMaxWeight(ItemStack stack) {
        return 128 + (stack.getEnchantmentLevel(SpeciesEnchantments.CAPACITY.get()) * 64);
    }

    @Override
    public int getDefaultProjectileRange() {
        return 15;
    }

    public int getUseDuration(ItemStack stack) {
        return 72000;
    }



    private static void shootProjectile(Level level, LivingEntity livingEntity, InteractionHand hand, ItemStack stack, ItemStack stack1, float pitch, float x, float y, float z, boolean fromScattershot) {
        if (!level.isClientSide) {
            ArrowItem arrowitem = (ArrowItem)(stack1.getItem() instanceof ArrowItem ? stack1.getItem() : Items.ARROW);
            AbstractArrow abstractarrow = arrowitem.createArrow(level, stack1, livingEntity);
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
                    int sparingLevel = stack.getEnchantmentLevel(SpeciesEnchantments.SPARING.get());
                    int i = sparingLevel == 1 ? 10 : sparingLevel == 2 ? 5 : sparingLevel == 3 ? 3 : 0;
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

            stack.hurtAndBreak(1, livingEntity, (livingEntity1) -> livingEntity1.broadcastBreakEvent(hand));
            level.addFreshEntity(abstractarrow);
        }

    }


    @Override
    public boolean overrideStackedOnOther(ItemStack p_150733_, Slot p_150734_, ClickAction p_150735_, Player p_150736_) {
        if (p_150733_.getCount() == 1 && p_150735_ == ClickAction.SECONDARY) {
            ItemStack itemstack = p_150734_.getItem();
            if (itemstack.isEmpty()) {
                this.playRemoveOneSound(p_150736_);
                removeOne(p_150733_).ifPresent((p_150740_) -> {
                    add(p_150733_, p_150734_.safeInsert(p_150740_));
                });
            } else if (itemstack.getItem().canFitInsideContainerItems() && getAllSupportedProjectiles().test(itemstack)) {
                int i = (getMaxWeight(p_150733_) - getContentWeight(p_150733_));
                int j = add(p_150733_, p_150734_.safeTake(itemstack.getCount(), i, p_150736_));
                if (j > 0) {
                    this.playInsertSound(p_150736_);
                }
            }

            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean overrideOtherStackedOnMe(ItemStack p_150742_, ItemStack added, Slot p_150744_, ClickAction p_150745_, Player p_150746_, SlotAccess p_150747_) {
        if (p_150742_.getCount() != 1) {
            return false;
        } else if (p_150745_ == ClickAction.SECONDARY && p_150744_.allowModification(p_150746_)) {
            if (added.isEmpty()) {
                removeOne(p_150742_).ifPresent((p_186347_) -> {
                    this.playRemoveOneSound(p_150746_);
                    p_150747_.set(p_186347_);
                });
            } else {
                if (getAllSupportedProjectiles().test(added)) {
                    int i = add(p_150742_, added);
                    if (i > 0) {
                        this.playInsertSound(p_150746_);
                        added.shrink(i);
                    }
                }
            }

            return true;
        } else {
            return false;
        }
    }

    public static int add(ItemStack stack, ItemStack stack1) {
        if (!stack1.isEmpty() && stack1.getItem().canFitInsideContainerItems()) {
            CompoundTag compoundtag = stack.getOrCreateTag();
            if (!compoundtag.contains(TAG_ITEMS)) compoundtag.put(TAG_ITEMS, new ListTag());

            int i = getContentWeight(stack);
            int k = Math.min(stack1.getCount(), getMaxWeight(stack) - i);

            if (k == 0) return 0;
            else {
                ListTag listtag = compoundtag.getList(TAG_ITEMS, 10);
                Optional<CompoundTag> optional = getMatchingItem(stack1, listtag);
                if (optional.isPresent()) {
                    CompoundTag compoundtag1 = optional.get();
                    ItemStack itemstack = ItemStack.of(compoundtag1);

                    if (itemstack.getCount() + k > 64) {
                        int i2 = itemstack.getCount() + k;
                        itemstack.setCount(64);
                        itemstack.save(compoundtag1);
                        listtag.remove(compoundtag1);
                        listtag.add(0, compoundtag1);

                        ItemStack itemstack1 = stack1.copyWithCount(k);
                        CompoundTag compoundtag2 = new CompoundTag();
                        itemstack1.setCount(i2-64);
                        itemstack1.save(compoundtag2);
                        listtag.add(0, compoundtag2);
                    } else {
                        itemstack.grow(k);
                        itemstack.save(compoundtag1);
                        listtag.remove(compoundtag1);
                        listtag.add(0, compoundtag1);
                    }
                } else {
                    ItemStack itemstack1 = stack1.copyWithCount(k);
                    CompoundTag compoundtag2 = new CompoundTag();
                    itemstack1.save(compoundtag2);
                    listtag.add(0, compoundtag2);
                }
                return k;
            }
        } else return 0;
    }


    private static Optional<CompoundTag> getMatchingItem(ItemStack stack, ListTag tags) {
        Optional var10000;
        if (stack.is(Items.BUNDLE)) {
            var10000 = Optional.empty();
        } else {
            Stream var2 = tags.stream();
            Objects.requireNonNull(CompoundTag.class);
            var2 = var2.filter(CompoundTag.class::isInstance);
            Objects.requireNonNull(CompoundTag.class);
            var10000 = var2.map(CompoundTag.class::cast).filter((itemStack) -> ItemStack.isSameItemSameTags(ItemStack.of((CompoundTag)itemStack), stack)).findFirst();
        }

        return var10000;
    }

    public static int getContentWeight(ItemStack itemStack2) {
        return CrankbowItem.getContents(itemStack2).mapToInt(ItemStack::getCount).sum();
    }

    private static Optional<ItemStack> removeOne(ItemStack itemStack) {
        CompoundTag compoundtag = itemStack.getOrCreateTag();
        if (!compoundtag.contains(TAG_ITEMS)) {
            return Optional.empty();
        } else {
            ListTag listtag = compoundtag.getList(TAG_ITEMS, 10);
            if (listtag.isEmpty()) {
                return Optional.empty();
            } else {
                CompoundTag compoundtag1 = listtag.getCompound(0);
                ItemStack itemstack = ItemStack.of(compoundtag1);
                listtag.remove(0);
                if (listtag.isEmpty()) itemStack.removeTagKey(TAG_ITEMS);
                return Optional.of(itemstack);
            }
        }
    }


    private static Optional<ItemStack> removeOneItem(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getOrCreateTag();
        if (compoundTag.contains(TAG_ITEMS)) {
            ListTag itemList = compoundTag.getList(TAG_ITEMS, 10);
            if (!itemList.isEmpty()) {
                CompoundTag itemTag = itemList.getCompound(0);
                ItemStack itemStack2 = ItemStack.of(itemTag);
                ItemStack itemStack3 = itemStack2.copyWithCount(itemStack2.getCount()-1);

                CompoundTag itemTag3 = new CompoundTag();
                itemStack3.save(itemTag3);

                itemList.set(0, itemTag3);

                if (itemStack2.getCount() == 1) {
                    ItemStack itemStack1 = ItemStack.of(itemList.getCompound(1));
                    if (itemStack1.isEmpty()) compoundTag.remove(TAG_ITEMS);
                    itemList.remove(0);
                    return Optional.empty();
                }
                return Optional.of(itemStack3);
            }
        }
        return Optional.empty();
    }

    private static Stream<ItemStack> getContents(ItemStack itemStack) {
        CompoundTag compoundTag = itemStack.getTag();
        if (compoundTag == null) return Stream.empty();
        ListTag listTag = compoundTag.getList(TAG_ITEMS, 10);
        return listTag.stream().map(CompoundTag.class::cast).map(ItemStack::of);
    }

    @Override
    public void onDestroyed(ItemEntity itemEntity) {
        ItemUtils.onContainerDestroyed(itemEntity, CrankbowItem.getContents(itemEntity.getItem()));
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
        return Optional.of(new BundleTooltip(nonNullList, CrankbowItem.getContentWeight(itemStack)));
    }

    @Override
    public void appendHoverText(ItemStack itemStack, Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.translatable("item.species.crankbow.fullness", CrankbowItem.getContentWeight(itemStack), CrankbowItem.getMaxWeight(itemStack)).withStyle(ChatFormatting.GRAY));
        list.add(Component.translatable("item.species.crankbow.desc").withStyle(Style.EMPTY.withColor(0x723548)));
        super.appendHoverText(itemStack, level, list, tooltipFlag);
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
