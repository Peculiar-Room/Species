package com.ninni.species.entity;

import com.ninni.species.entity.effect.SpeciesStatusEffects;
import com.ninni.species.item.SpeciesItems;
import com.ninni.species.sound.SpeciesSoundEvents;
import net.minecraft.entity.EntityStatuses;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.sound.SoundCategory;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class BirtEggEntity extends ThrownItemEntity {

    public BirtEggEntity(EntityType<? extends BirtEggEntity> entityType, World world) {
        super(entityType, world);
    }

    public BirtEggEntity(World world, LivingEntity owner) {
        super(SpeciesEntities.BIRT_EGG, owner, world);
    }

    public BirtEggEntity(World world, double x, double y, double z) {
        super(SpeciesEntities.BIRT_EGG, x, y, z, world);
    }


    @Override
    public void handleStatus(byte status) {
        if (status == EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES) {
            for (int i = 0; i < 8; ++i) {
                this.world.addParticle(new ItemStackParticleEffect(ParticleTypes.ITEM, this.getStack()), this.getX(), this.getY(), this.getZ(), ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08, ((double)this.random.nextFloat() - 0.5) * 0.08);
            }
        }
    }

    @Override
    protected void onEntityHit(EntityHitResult entityHitResult) {
        super.onEntityHit(entityHitResult);
        if (entityHitResult.getEntity() instanceof LivingEntity entity) {
            if (!entity.hasStatusEffect(SpeciesStatusEffects.BIRTD)) world.playSound(null, this.getBlockPos(), SpeciesSoundEvents.ENTITY_BIRTD, SoundCategory.NEUTRAL, 1, 1);
            entity.addStatusEffect(new StatusEffectInstance(SpeciesStatusEffects.BIRTD, 20 * 3, 0), this.getOwner());
        }
        entityHitResult.getEntity().damage(DamageSource.thrownProjectile(this, this.getOwner()), 2.0f);
    }

    @Override
    protected void onCollision(HitResult hitResult) {
        super.onCollision(hitResult);
        HitResult.Type type = hitResult.getType();
        if (!this.world.isClient && type != HitResult.Type.ENTITY) {
            if (this.random.nextInt(8) == 0) {
                int i = 1;
                if (this.random.nextInt(32) == 0) {
                    i = 4;
                }
                for (int j = 0; j < i; ++j) {
                    BirtEntity chick = SpeciesEntities.BIRT.create(this.world);
                    assert chick != null;
                    chick.setBreedingAge(-24000);
                    chick.refreshPositionAndAngles(this.getX(), this.getY(), this.getZ(), this.getYaw(), 0.0f);
                    this.world.spawnEntity(chick);
                }
            }
        }
        world.playSound(null, this.getBlockPos(), SpeciesSoundEvents.ITEM_BIRT_EGG_HIT, SoundCategory.NEUTRAL, 1, 1);
        this.world.sendEntityStatus(this, EntityStatuses.PLAY_DEATH_SOUND_OR_ADD_PROJECTILE_HIT_PARTICLES);
        this.discard();
    }

    @Override
    protected Item getDefaultItem() {
        return SpeciesItems.BIRT_EGG;
    }
}

