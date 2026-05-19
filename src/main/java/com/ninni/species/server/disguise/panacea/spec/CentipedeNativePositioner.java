package com.ninni.species.server.disguise.panacea.spec;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/** Wraps AM's {@code tickMultipartPosition}; resets {@code prevHeight} on flat ground because
 *  the native probe sticks at 0.2-0.4 there, leaving the chain nosing down. */
public final class CentipedeNativePositioner implements SegmentPositioner {

    /** Index 0 sits closer to the head than the standard back offset. */
    private static final float INDEX_0_BACK_OFFSET = 0.45F;
    private static final float FALLBACK_BACK_OFFSET = 0.5F;

    public static final CentipedeNativePositioner INSTANCE = new CentipedeNativePositioner();
    private CentipedeNativePositioner() {}

    @Override
    public void position(SegmentContext ctx) {
        Entity seg = ctx.seg();
        Entity prev = ctx.prev();
        LivingEntity disguise = ctx.disguise();
        ReflectionPlan refl = ctx.reflection();

        // Armor stand wearer: bypass tickMultipartPosition and snap each segment behind the
        // parent at spec spacing; -O fields aligned so partialTick=0 reads the snapped pose.
        if (ctx.wearer() instanceof net.minecraft.world.entity.decoration.ArmorStand) {
            float prevYaw = prev.getYRot();
            double spacing = ctx.spec().initialSpacing();
            Vec3 offset = new Vec3(0, 0, -spacing).yRot((float) -Math.toRadians(prevYaw));
            Vec3 ideal = prev.position().add(offset);
            seg.setPos(ideal);
            seg.xo = ideal.x; seg.xOld = ideal.x;
            seg.yo = ideal.y; seg.yOld = ideal.y;
            seg.zo = ideal.z; seg.zOld = ideal.z;
            seg.setYRot(prevYaw);
            seg.yRotO = prevYaw;
            seg.setXRot(0F);
            seg.xRotO = 0F;
            if (seg instanceof LivingEntity living) {
                living.yBodyRot = prevYaw;
                living.yBodyRotO = prevYaw;
                living.yHeadRot = prevYaw;
                living.yHeadRotO = prevYaw;
            }
            return;
        }

        Method tickMultipart = refl.method("tickMultipartPosition");
        Method getYawForPart = refl.method("getYawForPart");
        if (tickMultipart == null || getYawForPart == null) return;

        Method getBackOffset = refl.method("getBackOffset");
        Field prevHeightField = refl.field("prevHeight");

        try {
            float backOffset;
            if (ctx.index() == 0) backOffset = INDEX_0_BACK_OFFSET;
            else if (getBackOffset != null) backOffset = (float) getBackOffset.invoke(prev);
            else backOffset = FALLBACK_BACK_OFFSET;

            float parentXRot = prev.getXRot();
            float ourYRot = (float) getYawForPart.invoke(disguise, ctx.index());
            int headId = disguise.getId();

            if (prevHeightField != null && isOnFlatGround(seg)) {
                try { prevHeightField.setDouble(seg, 0.0); } catch (IllegalAccessException ignored) {}
            }

            tickMultipart.invoke(seg, headId, backOffset, prev.position(), parentXRot, ourYRot, true);
        } catch (ReflectiveOperationException ignored) {}
    }

    /** Solid block 0.5 below + non-solid at +0.4 — the AABB-straddle case where prevHeight sticks. */
    private static boolean isOnFlatGround(Entity seg) {
        Level level = seg.level();
        BlockPos under = BlockPos.containing(seg.getX(), seg.getY() - 0.5, seg.getZ());
        BlockPos above = BlockPos.containing(seg.getX(), seg.getY() + 0.4, seg.getZ());
        return level.getBlockState(under).isSuffocating(level, under)
                && !level.getBlockState(above).isSuffocating(level, above);
    }
}
