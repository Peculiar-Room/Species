package com.ninni.species.server.disguise;

import com.ninni.species.api.disguise.CompanionStackSpec;
import com.ninni.species.api.disguise.SubEntityProvider;
import com.ninni.species.api.util.PerSideStateMap;
import com.ninni.species.server.disguise.panacea.DisguiseTopologyRegistry;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/** Generic per-wearer companion-stack manager backing every {@link CompanionStackSpec}
 *  registration. Self-registers a global {@link SubEntityProvider} so render dispatch
 *  picks the companions up automatically. */
public final class GenericCompanionStackManager {

    private static final Map<EntityType<?>, CompanionStackSpec<?>> SPECS = new HashMap<>();
    private static final PerSideStateMap<StackState> STATES = new PerSideStateMap<>();
    private static volatile boolean providerRegistered;

    private GenericCompanionStackManager() {}

    /** Registers a spec for {@code type}; cycling logic should call {@link #cycle} when the
     *  shift+keybind fires, and {@link #tickStack} per-tick to position companions. */
    public static <C extends LivingEntity> void register(EntityType<?> type, CompanionStackSpec<C> spec) {
        if (type == null || spec == null) return;
        SPECS.put(type, spec);
        ensureProviderRegistered();
    }

    private static synchronized void ensureProviderRegistered() {
        if (providerRegistered) return;
        DisguiseTopologyRegistry.registerGlobalSubEntityProvider((wearer, disguise) -> {
            if (disguise == null || !SPECS.containsKey(disguise.getType())) return Collections.emptyList();
            StackState s = STATES.get(wearer);
            if (s == null || s.companions.isEmpty()) return Collections.emptyList();
            return s.companions;
        });
        providerRegistered = true;
    }

    private static final class StackState {
        int level;
        final List<LivingEntity> companions = new ArrayList<>();
    }

    /** Cycles the wearer's stack level 0 → 1 → ... → max → 0, spawning/discarding to match. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void cycle(LivingEntity wearer, LivingEntity disguise) {
        if (wearer == null || disguise == null) return;
        CompanionStackSpec spec = SPECS.get(disguise.getType());
        if (spec == null) return;
        StackState s = STATES.computeIfAbsent(wearer, StackState::new);
        int next = (s.level + 1) % (spec.maxLevel() + 1);
        setLevel(wearer, disguise, spec, s, next);
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void setLevel(LivingEntity wearer, LivingEntity disguise, CompanionStackSpec spec,
                                 StackState s, int target) {
        while (s.companions.size() > target) {
            LivingEntity removed = s.companions.remove(s.companions.size() - 1);
            removed.discard();
        }
        while (s.companions.size() < target) {
            LivingEntity added = (LivingEntity) spec.companionType().create(wearer.level());
            if (added == null) break;
            added.setNoGravity(true);
            added.noPhysics = true;
            added.setInvulnerable(true);
            added.setSilent(true);
            int idx = s.companions.size();
            spec.initializer().configure(added, wearer, disguise, idx);
            s.companions.add(added);
        }
        s.level = target;
    }

    /** Positions companions and re-applies tick-time state (color, yaw) per frame. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void tickStack(LivingEntity wearer, LivingEntity disguise) {
        if (wearer == null || disguise == null) return;
        CompanionStackSpec spec = SPECS.get(disguise.getType());
        if (spec == null) return;
        StackState s = STATES.get(wearer);
        if (s == null || s.companions.isEmpty()) return;
        double baseX = wearer.getX();
        double baseY = wearer.getY();
        double baseZ = wearer.getZ();
        // HeightResolver overrides for models where visual top != bbox top. Default uses
        // pose-aware getDimensions to bypass the disguise's pose lag at tick time.
        double topOfDisguise = spec.heightResolver() != null
                ? spec.heightResolver().resolve(wearer, disguise)
                : disguise.getDimensions(wearer.getPose()).height;
        for (int i = 0; i < s.companions.size(); i++) {
            LivingEntity c = s.companions.get(i);
            c.xo = c.getX(); c.yo = c.getY(); c.zo = c.getZ();
            c.xOld = c.xo; c.yOld = c.yo; c.zOld = c.zo;
            c.yRotO = c.getYRot();
            c.yBodyRotO = c.yBodyRot;
            c.yHeadRotO = c.yHeadRot;
            double cy = baseY + topOfDisguise + i * spec.verticalSpacing();
            c.setPos(baseX, cy, baseZ);
            // Mirror wearer walk state so each companion's setupAnim drives the same walk-cycle
            // (limb angle + body bob) the disguise body sees. Without this, sub-entities sit
            // perfectly still while the disguise visibly walks.
            c.walkAnimation.speed = wearer.walkAnimation.speed;
            c.walkAnimation.speedOld = wearer.walkAnimation.speedOld;
            c.walkAnimation.position = wearer.walkAnimation.position;
            spec.tickConfigurer().tick(c, wearer, disguise, i);
            c.tickCount++;
        }
    }

    /** Clears the wearer's companions on both sides. Idempotent. */
    public static void clear(LivingEntity wearer) {
        if (wearer == null) return;
        for (boolean side : new boolean[]{true, false}) {
            StackState s = STATES.side(side).remove(wearer.getUUID());
            if (s == null) continue;
            for (LivingEntity c : s.companions) c.discard();
        }
    }
}
