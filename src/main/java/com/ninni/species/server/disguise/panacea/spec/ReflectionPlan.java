package com.ninni.species.server.disguise.panacea.spec;

import com.ninni.species.api.util.ReflectionHelper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/** Declarative reflection cache for a {@link ChainSpec}. Built once via {@link Builder},
 *  resolved lazily on first {@link #ensureInited(Level)} call against a probe entity. Null
 *  tolerant — missing fields/methods stay null and callers must null-check at use. */
public final class ReflectionPlan {

    /** Probe target = which entity probes a lookup runs against. */
    public enum Target { HEAD, PART }

    private final ResourceLocation headId;
    private final ResourceLocation partId;
    /** Optional second part type (e.g. Centipede tail). Same Java class as part in vanilla AM. */
    private final ResourceLocation tailPartId;

    private final Map<String, Lookup> lookups;       // ordered, name → spec
    private final Map<String, Class<?>> auxClasses;  // FQN → resolved class (e.g. enum)

    // Resolved at ensureInited:
    private volatile boolean inited;
    private EntityType<?> headType;
    private EntityType<?> partType;
    private EntityType<?> tailPartType;
    private final Map<String, Method> methods = new HashMap<>();
    private final Map<String, Field> fields = new HashMap<>();
    private final Map<String, EntityDataAccessor<?>> accessors = new HashMap<>();
    private final Map<String, Class<?>> resolvedAux = new HashMap<>();

    private ReflectionPlan(Builder b) {
        this.headId = b.headId;
        this.partId = b.partId;
        this.tailPartId = b.tailPartId;
        this.lookups = new LinkedHashMap<>(b.lookups);
        this.auxClasses = new LinkedHashMap<>(b.auxClasses);
    }

    public static Builder builder(ResourceLocation headId, ResourceLocation partId) {
        return new Builder(headId, partId);
    }

    public ResourceLocation headId() { return headId; }
    public ResourceLocation partId() { return partId; }
    public ResourceLocation tailPartId() { return tailPartId; }
    public EntityType<?> headType() { return headType; }
    public EntityType<?> partType() { return partType; }
    public EntityType<?> tailPartType() { return tailPartType; }

    public Method method(String key) { return methods.get(key); }
    public Field field(String key) { return fields.get(key); }
    public EntityDataAccessor<?> accessor(String key) { return accessors.get(key); }
    @SuppressWarnings("unchecked")
    public <T> EntityDataAccessor<T> accessorTyped(String key) { return (EntityDataAccessor<T>) accessors.get(key); }
    public Class<?> auxClass(String fqn) { return resolvedAux.get(fqn); }
    public boolean isInited() { return inited; }

    /**
     * Resolves all lookups against the entity classes. Idempotent + thread-safe (DCL).
     * Probe entities are created and discarded; level is required because EntityType.create needs it.
     */
    public void ensureInited(Level level) {
        if (inited) return;
        synchronized (this) {
            if (inited) return;
            // Resolve aux classes first (may be referenced by method signatures).
            for (Map.Entry<String, Class<?>> e : auxClasses.entrySet()) {
                Class<?> c = e.getValue();
                if (c == null) {
                    try { c = Class.forName(e.getKey()); }
                    catch (ClassNotFoundException ignored) {}
                }
                if (c != null) resolvedAux.put(e.getKey(), c);
            }

            headType = BuiltInRegistries.ENTITY_TYPE.getOptional(headId).orElse(null);
            partType = BuiltInRegistries.ENTITY_TYPE.getOptional(partId).orElse(null);
            if (tailPartId != null) {
                tailPartType = BuiltInRegistries.ENTITY_TYPE.getOptional(tailPartId).orElse(null);
            }

            Class<?> headClass = probeClass(headType, level);
            Class<?> partClass = probeClass(partType, level);

            for (Map.Entry<String, Lookup> e : lookups.entrySet()) {
                Lookup l = e.getValue();
                Class<?> target = (l.target == Target.HEAD) ? headClass : partClass;
                if (target == null) continue;
                switch (l.kind) {
                    case METHOD -> {
                        Class<?>[] params = resolveParams(l.paramFqns);
                        if (params == null) break; // some param class unresolved → skip silently
                        Method m = l.declared
                                ? ReflectionHelper.declaredMethod(target, l.name, params)
                                : ReflectionHelper.publicMethod(target, l.name, params);
                        if (m != null) methods.put(e.getKey(), m);
                    }
                    case FIELD -> {
                        Field f = ReflectionHelper.declaredField(target, l.name);
                        if (f != null) fields.put(e.getKey(), f);
                    }
                    case TYPED_FIELD -> {
                        Field f = ReflectionHelper.declaredFieldOfType(target, l.name, l.fieldType);
                        if (f != null) fields.put(e.getKey(), f);
                    }
                    case ACCESSOR -> {
                        EntityDataAccessor<?> a = ReflectionHelper.accessor(target, l.name);
                        if (a != null) accessors.put(e.getKey(), a);
                    }
                }
            }
            inited = true;
        }
    }

    private static Class<?> probeClass(EntityType<?> type, Level level) {
        if (type == null) return null;
        Entity probe = type.create(level);
        if (probe == null) return null;
        Class<?> c = probe.getClass();
        probe.discard();
        return c;
    }

    private Class<?>[] resolveParams(String[] fqns) {
        if (fqns == null || fqns.length == 0) return new Class<?>[0];
        Class<?>[] out = new Class<?>[fqns.length];
        for (int i = 0; i < fqns.length; i++) {
            String fqn = fqns[i];
            Class<?> primitive = primitiveByName(fqn);
            if (primitive != null) { out[i] = primitive; continue; }
            Class<?> aux = resolvedAux.get(fqn);
            if (aux != null) { out[i] = aux; continue; }
            try { out[i] = Class.forName(fqn); }
            catch (ClassNotFoundException e) { return null; }
        }
        return out;
    }

    private static Class<?> primitiveByName(String fqn) {
        return switch (fqn) {
            case "boolean" -> boolean.class;
            case "byte"    -> byte.class;
            case "short"   -> short.class;
            case "int"     -> int.class;
            case "long"    -> long.class;
            case "float"   -> float.class;
            case "double"  -> double.class;
            case "char"    -> char.class;
            default -> null;
        };
    }

    private enum Kind { METHOD, FIELD, TYPED_FIELD, ACCESSOR }

    private record Lookup(Target target, Kind kind, String name, boolean declared,
                          String[] paramFqns, Class<?> fieldType) {}

    public static final class Builder {
        private final ResourceLocation headId;
        private final ResourceLocation partId;
        private ResourceLocation tailPartId;
        private final Map<String, Lookup> lookups = new LinkedHashMap<>();
        private final Map<String, Class<?>> auxClasses = new LinkedHashMap<>();

        Builder(ResourceLocation headId, ResourceLocation partId) {
            this.headId = headId;
            this.partId = partId;
        }

        public Builder tailPart(ResourceLocation id) { this.tailPartId = id; return this; }

        /** Public method. Param types as FQN strings so aux classes (e.g. enums from soft-deps) can resolve later. */
        public Builder publicMethod(String key, Target target, String name, String... paramFqns) {
            lookups.put(key, new Lookup(target, Kind.METHOD, name, false, paramFqns, null));
            return this;
        }

        /** Declared (private/package) method, walks hierarchy. */
        public Builder declaredMethod(String key, Target target, String name, String... paramFqns) {
            lookups.put(key, new Lookup(target, Kind.METHOD, name, true, paramFqns, null));
            return this;
        }

        public Builder field(String key, Target target, String name) {
            lookups.put(key, new Lookup(target, Kind.FIELD, name, true, null, null));
            return this;
        }

        public Builder typedField(String key, Target target, String name, Class<?> type) {
            lookups.put(key, new Lookup(target, Kind.TYPED_FIELD, name, true, null, type));
            return this;
        }

        public Builder accessor(String key, Target target, String name) {
            lookups.put(key, new Lookup(target, Kind.ACCESSOR, name, true, null, null));
            return this;
        }

        /** Pre-register an aux class (e.g. {@code AnacondaPartIndex}) by FQN so methods referencing it resolve. */
        public Builder auxClass(String fqn) {
            auxClasses.put(fqn, null);
            return this;
        }

        public ReflectionPlan build() { return new ReflectionPlan(this); }
    }
}
