package com.ninni.species.server.item;

import com.ninni.species.client.renderer.item.SpeciesItemRenderers;
import com.ninni.species.registry.SpeciesDamageTypes;
import com.ninni.species.registry.SpeciesItems;
import com.ninni.species.registry.SpeciesParticles;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

import static net.minecraft.world.entity.EntitySelector.NO_CREATIVE_OR_SPECTATOR;

public class RicoshieldItem extends ShieldItem {
    public RicoshieldItem(Properties p_43089_) {
        super(p_43089_);
    }

    @Override
    public void onStopUsing(ItemStack stack, LivingEntity entity, int count) {
        if (stack.getTag().contains("StoredDamage") && stack.getTag().getFloat("StoredDamage") > 0) {
            this.damageTargets(stack, entity.level(), stack.getTag().getFloat("StoredDamage"), entity);
        }
        super.onStopUsing(stack, entity, count);
    }

    private void damageTargets(ItemStack stack, Level level, float amount, LivingEntity player) {
        List<LivingEntity> list = level.getEntitiesOfClass(LivingEntity.class, player.getBoundingBox().inflate(4D), NO_CREATIVE_OR_SPECTATOR);

        //Particle
        if (level instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(SpeciesParticles.SMALL_KINETIC_ENERGY.get(), player.position().x, player.position().y + 0.01, player.position().z, 1, 0, 0, 0, 0.5F);
        }
        level.playSound(player, player.blockPosition(), SpeciesSoundEvents.RICOSHIELD_ATTACK.get(), SoundSource.PLAYERS, 1, 1);

        //List entities nearby and choose the cause of the attack
        for (LivingEntity target : list) {
            if (target != player && !(target instanceof TamableAnimal tamableAnimal && tamableAnimal.getOwner() == player)) {

                //Handle knockback
                Vec3 enemyPos = target.position().subtract(player.position());
                Vec3 normalizedDirection = enemyPos.normalize();

                double knockbackXZ = 0.75 * (1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));
                double knockbackY = 0.15 * (1 - target.getAttributeValue(Attributes.KNOCKBACK_RESISTANCE));

                target.push(normalizedDirection.x() * knockbackXZ, normalizedDirection.y() * knockbackY, normalizedDirection.z() * knockbackXZ);

                //Scale damage
                double distanceFromEnemy = target.position().distanceTo(player.position());
                float scalingFactor;

                if (distanceFromEnemy <= 1) scalingFactor = 1F;
                else if (distanceFromEnemy <= 2) scalingFactor = 0.8F;
                else if (distanceFromEnemy <= 3) scalingFactor = 0.65F;
                else scalingFactor = 0.5F;

                amount *= scalingFactor;

                //Damage
                target.hurt(kinetic(target, player), amount);
                player.doHurtTarget(target);
            }
        }
        if (player instanceof Player player1) player1.getCooldowns().addCooldown(this, (int) (stack.getOrCreateTag().getFloat("StoredDamage") / 4) * 20);
        stack.getOrCreateTag().putFloat("StoredDamage", 0);
    }

    public DamageSource kinetic(LivingEntity livingEntity, LivingEntity livingEntity2) {
        return livingEntity2.damageSources().source(SpeciesDamageTypes.KINETIC, livingEntity, livingEntity2);
    }

    public boolean isValidRepairItem(ItemStack stack, ItemStack stack1) {
        return stack1.is(SpeciesItems.BROKEN_LINKS.get());
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.literal(""));
        list.add(Component.translatable("item.species.ricoshield.desc.blocking").withStyle(ChatFormatting.GRAY));
        list.add(Component.literal(" ").append(Component.translatable("item.species.ricoshield.desc.damage").withStyle(style -> style.withColor(0xE21447))));

        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public SpeciesItemRenderers getCustomRenderer() {
                return SpeciesItemRenderers.instance;
            }
        });
    }
}
