package com.ninni.species.entity;

import com.ninni.species.criterion.SpeciesCriterion;
import com.ninni.species.entity.effect.SpeciesStatusEffects;
import com.ninni.species.item.SpeciesItems;
import com.ninni.species.sound.SpeciesSoundEvents;
import net.minecraft.core.particles.ItemParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.projectile.ThrowableItemProjectile;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraft.world.phys.HitResult;

public class BirtEggEntity extends ThrowableItemProjectile {

    public BirtEggEntity(EntityType<? extends BirtEggEntity> entityType, Level world) {
        super(entityType, world);
    }

    public BirtEggEntity(Level world, LivingEntity owner) {
        super(SpeciesEntities.BIRT_EGG.get(), owner, world);
    }

    public BirtEggEntity(Level world, double x, double y, double z) {
        super(SpeciesEntities.BIRT_EGG.get(), x, y, z, world);
    }

    @Override
    public void handleEntityEvent(byte status) {
        if (status == 3) {
            for (int i = 0; i < 8; ++i) {
                this.level().addParticle(new ItemParticleOption(ParticleTypes.ITEM, this.getDefaultItem().getDefaultInstance()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08);
            }
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult entityHitResult) {
        super.onHitEntity(entityHitResult);
        if (entityHitResult.getEntity() instanceof LivingEntity entity) {
            if (!entity.hasEffect(SpeciesStatusEffects.BIRTD.get())) this.level().playSound(null, this.blockPosition(), SpeciesSoundEvents.ENTITY_BIRTD.get(), SoundSource.NEUTRAL, 1, 1);
            entity.addEffect(new MobEffectInstance(SpeciesStatusEffects.BIRTD.get(), 20 * 3, 0), this.getOwner());
        }
        if (entityHitResult.getEntity() instanceof Warden && this.getOwner() instanceof ServerPlayer serverPlayer) SpeciesCriterion.BIRT_EGG_AT_WARDEN.trigger(serverPlayer);
        entityHitResult.getEntity().hurt(this.level().damageSources().thrown(this, this.getOwner()), 2.0f);
    }

    @Override
    protected void onHit(HitResult hitResult) {
        super.onHit(hitResult);
        HitResult.Type type = hitResult.getType();
        if (!this.level().isClientSide && type != HitResult.Type.ENTITY) {
            if (this.random.nextInt(8) == 0) {
                int i = 1;
                if (this.random.nextInt(32) == 0) {
                    i = 4;
                }
                for (int j = 0; j < i; ++j) {
                    BirtEntity chick = SpeciesEntities.BIRT.get().create(this.level());
                    assert chick != null;
                    chick.setAge(-24000);
                    chick.moveTo(this.getX(), this.getY(), this.getZ(), this.getYRot(), 0.0f);
                    this.level().addFreshEntity(chick);
                }
            }
        }

        this.level().playSound(null, this.blockPosition(), SpeciesSoundEvents.ITEM_BIRT_EGG_HIT.get(), SoundSource.NEUTRAL, 1, 1);
        this.level().broadcastEntityEvent(this, (byte)3);
        this.discard();
    }

    @Override
    protected Item getDefaultItem() {
        return SpeciesItems.BIRT_EGG.get();
    }
}

