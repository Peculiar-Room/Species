package com.ninni.species.server.item;

import com.ninni.species.server.entity.mob.update_3.DeflectorDummy;
import com.ninni.species.registry.SpeciesEntities;
import com.ninni.species.registry.SpeciesSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;

public class DeflectorDummyItem extends Item {
    public DeflectorDummyItem(Item.Properties p_40503_) {
        super(p_40503_);
    }

    public InteractionResult useOn(UseOnContext useOnContext) {
        Direction direction = useOnContext.getClickedFace();
        if (direction == Direction.DOWN) {
            return InteractionResult.FAIL;
        } else {
            Level level = useOnContext.getLevel();
            BlockPlaceContext blockplacecontext = new BlockPlaceContext(useOnContext);
            BlockPos blockpos = blockplacecontext.getClickedPos();
            ItemStack itemstack = useOnContext.getItemInHand();
            Vec3 vec3 = Vec3.atBottomCenterOf(blockpos);
            AABB aabb = SpeciesEntities.DEFLECTOR_DUMMY.get().getDimensions().makeBoundingBox(vec3.x(), vec3.y(), vec3.z());
            if (level.noCollision(null, aabb) && level.getEntities(null, aabb).isEmpty()) {
                if (level instanceof ServerLevel serverlevel) {
                    Consumer<DeflectorDummy> consumer = EntityType.createDefaultStackConfig(serverlevel, itemstack, useOnContext.getPlayer());
                    DeflectorDummy dummy = SpeciesEntities.DEFLECTOR_DUMMY.get().create(serverlevel, itemstack.getTag(), consumer, blockpos, MobSpawnType.SPAWN_EGG, true, true);
                    if (dummy == null) {
                        return InteractionResult.FAIL;
                    }

                    float f = (float)Mth.floor((Mth.wrapDegrees(useOnContext.getRotation() - 180.0F) + 22.5F) / 45.0F) * 45.0F;
                    dummy.moveTo(dummy.getX(), dummy.getY(), dummy.getZ(), f, 0.0F);
                    serverlevel.addFreshEntityWithPassengers(dummy);
                    level.playSound(null, dummy.getX(), dummy.getY(), dummy.getZ(), SpeciesSoundEvents.DEFLECTOR_DUMMY_PLACE.get(), SoundSource.BLOCKS, 0.75F, 0.8F);
                    dummy.gameEvent(GameEvent.ENTITY_PLACE, useOnContext.getPlayer());
                }

                itemstack.shrink(1);
                return InteractionResult.sidedSuccess(level.isClientSide);
            } else {
                return InteractionResult.FAIL;
            }
        }
    }

    @Override
    public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> list, TooltipFlag tooltipFlag) {
        list.add(Component.literal(""));
        list.add(Component.translatable("item.species.deflector_dummy.desc.powered").withStyle(ChatFormatting.GRAY));
        list.add(Component.literal(" ").append(Component.translatable("item.species.deflector_dummy.desc.damage").withStyle(style -> style.withColor(0xE21447))));

        super.appendHoverText(itemStack, level, list, tooltipFlag);
    }
}