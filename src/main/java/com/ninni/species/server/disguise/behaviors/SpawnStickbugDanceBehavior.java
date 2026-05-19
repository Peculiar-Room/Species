package com.ninni.species.server.disguise.behaviors;

import com.ninni.species.api.disguise.ActionContext;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.util.ReflectionHelper;
import com.ninni.species.api.util.PerSideStateMap;
import net.minecraft.world.entity.LivingEntity;

import java.lang.reflect.Method;

/** Spawn Stickbug disguise: keybind toggles dancing. We pin {@code DATA_DANCING=true} each
 *  preTick and let {@code Stickbug.tick} own the 19-tick anim restart loop (the
 *  {@code jukeboxPosition}-null reset branch is harmless since the entity just keeps dancing). */
public final class SpawnStickbugDanceBehavior implements DisguiseBehavior {

    public static final SpawnStickbugDanceBehavior INSTANCE = new SpawnStickbugDanceBehavior();

    private static volatile boolean reflectionInited;
    private static Method setDancingMethod;

    private static final class State {
        boolean dancing;
    }

    private final PerSideStateMap<State> states = new PerSideStateMap<>();

    private SpawnStickbugDanceBehavior() {}

    @Override
    public void onSpecialAction(LivingEntity wearer, LivingEntity disguise,
                                ActionContext context) {
        initReflection(disguise.getClass());
        State st = states.computeIfAbsent(wearer, State::new);
        st.dancing = !st.dancing;
        if (!st.dancing) setDancing(disguise, false);
    }

    @Override
    public void preTick(LivingEntity wearer, LivingEntity disguise) {
        State st = states.get(wearer);
        if (st == null || !st.dancing) return;
        // Pinned ahead of disguise.tick so the 19t anim restart loop sees DATA_DANCING=true.
        setDancing(disguise, true);
    }

    @Override
    public void onDestroyed(LivingEntity wearer, LivingEntity disguise) {
        states.remove(wearer);
    }

    private static void setDancing(LivingEntity disguise, boolean value) {
        if (setDancingMethod == null) return;
        try { setDancingMethod.invoke(disguise, value); }
        catch (ReflectiveOperationException ignored) {}
    }

    private static void initReflection(Class<?> entityClass) {
        if (reflectionInited) return;
        synchronized (SpawnStickbugDanceBehavior.class) {
            if (reflectionInited) return;
            setDancingMethod = ReflectionHelper.publicMethod(entityClass, "setDancing", boolean.class);
            reflectionInited = true;
        }
    }
}
