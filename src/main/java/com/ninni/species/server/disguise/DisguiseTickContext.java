package com.ninni.species.server.disguise;

import org.jetbrains.annotations.ApiStatus;

/**
 * Per-thread depth counter gating particle suppression around {@code disguise.tick()} for local first-person wearers.
 * Depth (not boolean) handles re-entrant ticks; ThreadLocal isolates client tick from integrated-server thread.
 */
@ApiStatus.Internal
public final class DisguiseTickContext {

    private static final ThreadLocal<Integer> SUPPRESS_DEPTH = ThreadLocal.withInitial(() -> 0);
    // Always-on depth for the duration of disguise.tick() (any side), so leaked AI world
    // mutations (e.g. the Ignis disguise placing fire blocks) can be suppressed.
    private static final ThreadLocal<Integer> TICK_DEPTH = ThreadLocal.withInitial(() -> 0);

    private DisguiseTickContext() {}

    @ApiStatus.Internal
    public static void pushTick() {
        TICK_DEPTH.set(TICK_DEPTH.get() + 1);
    }

    @ApiStatus.Internal
    public static void popTick() {
        TICK_DEPTH.set(Math.max(0, TICK_DEPTH.get() - 1));
    }

    @ApiStatus.Internal
    public static boolean isDisguiseTicking() {
        return TICK_DEPTH.get() > 0;
    }

    @ApiStatus.Internal
    public static void pushSuppress() {
        SUPPRESS_DEPTH.set(SUPPRESS_DEPTH.get() + 1);
    }

    @ApiStatus.Internal
    public static void popSuppress() {
        SUPPRESS_DEPTH.set(Math.max(0, SUPPRESS_DEPTH.get() - 1));
    }

    @ApiStatus.Internal
    public static boolean isSuppressing() {
        return SUPPRESS_DEPTH.get() > 0;
    }
}
