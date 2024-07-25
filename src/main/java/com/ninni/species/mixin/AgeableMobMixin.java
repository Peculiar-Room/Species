package com.ninni.species.mixin;

import com.ninni.species.registry.SpeciesItems;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableMob.class)
public abstract class AgeableMobMixin extends PathfinderMob {
    @Shadow public abstract boolean isBaby();

    @Unique
    public boolean potion;

    protected AgeableMobMixin(EntityType<? extends PathfinderMob> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(at = @At("TAIL"), method = "addAdditionalSaveData")
    private void S$addAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        compoundTag.putBoolean("YouthPotion", this.potion);
    }

    @Inject(at = @At("TAIL"), method = "readAdditionalSaveData")
    private void S$readAdditionalSaveData(CompoundTag compoundTag, CallbackInfo ci) {
        this.potion = compoundTag.getBoolean("YouthPotion");
    }

    @Inject(at = @At("HEAD"), method = "aiStep", cancellable = true)
    private void S$aiStep(CallbackInfo ci) {
        if (potion) {
            ci.cancel();
        }
    }

    @Override
    public InteractionResult interactAt(Player player, Vec3 vec3, InteractionHand interactionHand) {
        if (player.getItemInHand(interactionHand).is(SpeciesItems.YOUTH_POTION) && this.isBaby() && !this.potion) {
            this.potion = true;
            return InteractionResult.SUCCESS;
        }
        if (potion && player.getItemInHand(interactionHand).is(Items.MILK_BUCKET)) {
            this.potion = false;
        }
        return super.interactAt(player, vec3, interactionHand);
    }
}
