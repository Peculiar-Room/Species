package com.ninni.species.server.entity.mob.update_3;

import com.ninni.species.server.entity.util.SpeciesPose;
import com.ninni.species.registry.*;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.DustParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.ForgeMod;

import javax.annotation.Nullable;
import java.util.List;

import static net.minecraft.world.entity.EntitySelector.NO_CREATIVE_OR_SPECTATOR;

public class DeflectorDummy extends ArmorStand {
    public static final EntityDataAccessor<Float> STORED_DAMAGE = SynchedEntityData.defineId(DeflectorDummy.class, EntityDataSerializers.FLOAT);
    public static final EntityDataAccessor<Boolean> POWERED = SynchedEntityData.defineId(DeflectorDummy.class, EntityDataSerializers.BOOLEAN);
    public final AnimationState redirectAnimationState = new AnimationState();
    public final AnimationState absorbAnimationState = new AnimationState();
    public final AnimationState releaseAnimationState = new AnimationState();
    private int redirectCooldown = 0;
    private int absorbCooldown = 0;
    private int releaseCooldown = 0;

    public DeflectorDummy(EntityType<DeflectorDummy> entityEntityType, Level level) {
        super(entityEntityType, level);
        this.setMaxUpStep(0.0F);
    }

    public static AttributeSupplier.Builder createLivingAttributes() {
        return AttributeSupplier.builder().add(Attributes.MAX_HEALTH).add(Attributes.KNOCKBACK_RESISTANCE, 1).add(Attributes.MOVEMENT_SPEED).add(Attributes.ARMOR).add(Attributes.ARMOR_TOUGHNESS).add((Attribute) ForgeMod.SWIM_SPEED.get()).add((Attribute)ForgeMod.NAMETAG_DISTANCE.get()).add((Attribute)ForgeMod.ENTITY_GRAVITY.get()).add((Attribute)ForgeMod.STEP_HEIGHT_ADDITION.get());
    }

    @Override
    public boolean hurt(DamageSource damageSource, float amount) {

        if (damageSource.getDirectEntity() instanceof Player player && player.isShiftKeyDown() || damageSource.getEntity() == null) {
            return breakDummy(damageSource);
        } else {
            if (this.isPowered()) {
                //Store damage
                this.setPose(SpeciesPose.ABSORB_DAMAGE.get());
                this.addStoredDamage(amount);
                this.playSound(SpeciesSoundEvents.DEFLECTOR_DUMMY_ABSORB.get(), 1, this.getStoredDamage() * 0.025F);
            } else {
                //Redirect attack immediately
                if (redirectCooldown == 0 && this.getPose() != SpeciesPose.REDIRECT_DAMAGE.get()) {
                    this.setPose(SpeciesPose.REDIRECT_DAMAGE.get());
                    this.playSound(SpeciesSoundEvents.DEFLECTOR_DUMMY_DEFLECT.get(), 2, 1);
                    this.damageTargets(amount, damageSource.getEntity());
                }
            }
            return true;
        }
    }

    private void damageTargets(float amount, @Nullable Entity attacker) {
        List<LivingEntity> list = level().getEntitiesOfClass(LivingEntity.class, this.getBoundingBox().inflate(4D), NO_CREATIVE_OR_SPECTATOR);

        //Particle
        if (this.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(SpeciesParticles.SMALL_KINETIC_ENERGY.get(), this.position().x, this.position().y + 0.01, this.position().z, 1, 0, 0, 0, 0.5F);
        }

        //List entities nearby and choose the cause of the attack
        for (LivingEntity target : list) {
            if (!(target.getType().is(SpeciesTags.CANT_BE_DAMAGED_BY_DUMMY))) {
                if ((attacker instanceof LivingEntity livingAttacker)) {
                    damage(target, livingAttacker, amount);
                } else if (attacker instanceof Projectile projectile && projectile.getOwner() instanceof LivingEntity projectileOwner) {
                    damage(target, projectileOwner, amount);
                } else if (attacker == null) {
                    damage(target, null, amount);
                }
            }
        }
    }

    private void damage(LivingEntity target, @Nullable LivingEntity attacker, float amount) {

        //Handle knockback
        Vec3 enemyPos = target.position().subtract(this.position());
        Vec3 normalizedDirection = enemyPos.normalize();

        double knockbackXZ = 0.75 * (1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
        double knockbackY = 0.15 * (1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));

        target.push(normalizedDirection.x() * knockbackXZ, normalizedDirection.y() * knockbackY, normalizedDirection.z() * knockbackXZ);

        //Scale damage
        double distanceFromEnemy = target.position().distanceTo(this.position());
        float scalingFactor;

        if (distanceFromEnemy <= 1) scalingFactor = 1F;
        else if (distanceFromEnemy <= 2) scalingFactor = 0.8F;
        else if (distanceFromEnemy <= 3) scalingFactor = 0.65F;
        else scalingFactor = 0.5F;

        amount *= scalingFactor;

        //Damage
        if (attacker == null) target.hurt(kinetic(target), amount);
        else target.hurt(kinetic(target, attacker), amount);
        this.doHurtTarget(target);
    }

    @Override
    public void tick() {
        super.tick();

        //Tick cooldowns
        if (redirectCooldown > 0) --redirectCooldown;
        if (redirectCooldown == 0 && this.getPose() == SpeciesPose.REDIRECT_DAMAGE.get()) this.setPose(Pose.STANDING);
        if (absorbCooldown > 0) --absorbCooldown;
        if (absorbCooldown == 0 && this.getPose() == SpeciesPose.ABSORB_DAMAGE.get()) this.setPose(Pose.STANDING);
        if (releaseCooldown > 0) --releaseCooldown;
        if (releaseCooldown == 0 && this.releaseAnimationState.isStarted()) this.releaseAnimationState.stop();

        if (this.isPowered() && this.random.nextInt(10) == 0) {
            this.level().addParticle(DustParticleOptions.REDSTONE, this.getRandomX(0.8D), this.getRandomY(), this.getRandomZ(0.8D), 0.0D, 0.0D, 0.0D);
        }

        //Set powered
        boolean power = this.level().getSignal(this.getOnPos(), Direction.UP) > 0 || this.level().getSignal(this.getOnPos().offset(0, 1, 1), Direction.NORTH) > 0 || this.level().getSignal(this.getOnPos().offset(0, 1, -1), Direction.SOUTH) > 0 || this.level().getSignal(this.getOnPos().offset(1, 1, 0), Direction.EAST) > 0 || this.level().getSignal(this.getOnPos().offset(-1, 1, 0), Direction.WEST) > 0;
        if (power && !this.isPowered()) {
            this.setPowered(true);
        }
        if (!power && this.isPowered()) {
            this.setPowered(false);
            if (this.getStoredDamage() > 0) {
                this.playSound(SpeciesSoundEvents.DEFLECTOR_DUMMY_ATTACK.get(), 3, 1);
                this.releaseCooldown = 10;
                this.releaseAnimationState.start(this.tickCount);
            }
        }

        //Release stored damage
        if (releaseCooldown == 5 && !this.isPowered()) {
            this.damageTargets(this.getStoredDamage(), null);
            this.setStoredDamage(0);
        }
    }

    private boolean breakDummy(DamageSource damageSource) {
        if (!this.level().isClientSide && !this.isRemoved()) {
            if (damageSource.is(DamageTypeTags.BYPASSES_INVULNERABILITY)) {
                this.kill();
                return false;
            } else if (!this.isInvulnerableTo(damageSource) && !this.isMarker()) {
                if (damageSource.is(DamageTypeTags.IS_EXPLOSION)) {
                    this.brokenByAnything(damageSource);
                    this.kill();
                    return false;
                } else {
                    if (damageSource.isCreativePlayer()) {
                        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.DEFLECTOR_DUMMY_BREAK.get(), this.getSoundSource(), 1.0F, 1.0F);
                        this.showBreakingParticles();
                        this.kill();
                        return false;
                    } else {
                        long i = this.level().getGameTime();
                        if (i - this.lastHit > 5L) {
                            this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.DEFLECTOR_DUMMY_HURT.get(), this.getSoundSource(), 1.0F, 1.0F);
                            this.level().broadcastEntityEvent(this, (byte)32);
                            this.gameEvent(GameEvent.ENTITY_DAMAGE, damageSource.getEntity());
                            this.lastHit = i;
                        } else {
                            this.brokenByPlayer(damageSource);
                            this.showBreakingParticles();
                            this.kill();
                        }
                        return true;
                    }
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


    private void brokenByAnything(DamageSource p_31654_) {
        this.level().playSound(null, this.getX(), this.getY(), this.getZ(), SpeciesSoundEvents.DEFLECTOR_DUMMY_BREAK.get(), this.getSoundSource(), 1.0F, 1.0F);
        this.dropAllDeathLoot(p_31654_);
    }

    private void brokenByPlayer(DamageSource p_31647_) {
        ItemStack itemstack = new ItemStack(SpeciesItems.DEFLECTOR_DUMMY.get());
        if (this.hasCustomName()) itemstack.setHoverName(this.getCustomName());
        Block.popResource(this.level(), this.blockPosition(), itemstack);
        this.brokenByAnything(p_31647_);
    }

    private void showBreakingParticles() {
        if (this.level() instanceof ServerLevel) {
            ((ServerLevel)this.level()).sendParticles(new BlockParticleOption(ParticleTypes.BLOCK, SpeciesBlocks.KINETIC_CORE.get().defaultBlockState()), this.getX(), this.getY(0.6666666666666666D), this.getZ(), 10, (this.getBbWidth() / 4.0F), (this.getBbHeight() / 4.0F), (this.getBbWidth() / 4.0F), 0.05D);
        }
    }

    public DamageSource kinetic(LivingEntity livingEntity, LivingEntity livingEntity2) {
        return this.damageSources().source(SpeciesDamageTypes.KINETIC, livingEntity, livingEntity2);
    }

    public DamageSource kinetic(LivingEntity livingEntity) {
        return this.damageSources().source(SpeciesDamageTypes.KINETIC, livingEntity);
    }


    @Override
    public void onSyncedDataUpdated(EntityDataAccessor<?> entityDataAccessor) {
        if (DATA_POSE.equals(entityDataAccessor)) {
            if (this.getPose() == Pose.STANDING) {
                this.redirectAnimationState.stop();
                this.absorbAnimationState.stop();
            }
            else if (this.getPose() == SpeciesPose.REDIRECT_DAMAGE.get()) {
                redirectCooldown = 5;
                this.redirectAnimationState.start(this.tickCount);
            }
            else if (this.getPose() == SpeciesPose.ABSORB_DAMAGE.get()) {
                this.absorbCooldown = 10;
                this.absorbAnimationState.start(this.tickCount);
            }
        }
        super.onSyncedDataUpdated(entityDataAccessor);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(STORED_DAMAGE, 0F);
        this.entityData.define(POWERED, false);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putFloat("StoredDamage", this.getStoredDamage());
        compoundTag.putBoolean("Powered", this.isPowered());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.setStoredDamage(compoundTag.getFloat("StoredDamage"));
        this.setPowered(compoundTag.getBoolean("Powered"));
    }

    public float getStoredDamage() {
        return this.entityData.get(STORED_DAMAGE);
    }
    public void setStoredDamage(float amount) {
        this.entityData.set(STORED_DAMAGE, Math.min(amount, 80));
    }
    public void addStoredDamage(float amount) {
        this.setStoredDamage(this.getStoredDamage() + amount);
    }

    public boolean isPowered() {
        return this.entityData.get(POWERED);
    }
    public void setPowered(boolean powered) {
        this.entityData.set(POWERED, powered);
    }

    @Override
    public InteractionResult interactAt(Player p_31594_, Vec3 p_31595_, InteractionHand p_31596_) {
        return InteractionResult.CONSUME;
    }

    @Override
    public boolean canBreatheUnderwater() {
        return true;
    }

    @Nullable
    protected SoundEvent getHurtSound(DamageSource p_31636_) {
        return SpeciesSoundEvents.DEFLECTOR_DUMMY_HURT.get();
    }

    @Nullable
    protected SoundEvent getDeathSound() {
        return SpeciesSoundEvents.DEFLECTOR_DUMMY_BREAK.get();
    }

    @Override
    public PushReaction getPistonPushReaction() {
        return PushReaction.IGNORE;
    }

    public boolean canBeSeenAsEnemy() {
        return false;
    }

    @Override
    public ItemStack getPickedResult(HitResult target) {
        return new ItemStack(SpeciesItems.DEFLECTOR_DUMMY.get());
    }

    @Override
    public InteractionResult interact(Player p_19978_, InteractionHand p_19979_) {
        return InteractionResult.FAIL;
    }
}
