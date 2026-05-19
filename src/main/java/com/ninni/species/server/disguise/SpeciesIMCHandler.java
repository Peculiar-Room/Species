package com.ninni.species.server.disguise;

import com.ninni.species.Species;
import com.ninni.species.api.disguise.CompanionStackSpec;
import com.ninni.species.api.disguise.DisguiseBehavior;
import com.ninni.species.api.disguise.SpeciesAPI;
import com.ninni.species.api.disguise.SubEntityProvider;
import com.ninni.species.api.disguise.imc.*;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;

import java.util.Map;
import java.util.function.Consumer;

/**
 * Processes Forge IMC messages: each {@link SpeciesIMCKeys} method maps to a typed {@link Dispatcher}
 * that validates payload and forwards to {@link SpeciesAPI}; unknown methods and type mismatches are logged and skipped.
 */
public final class SpeciesIMCHandler {

    /** Per-method handler: validates payload type, then forwards to a typed action. The
     *  type-erased cast is guarded by {@link #apply}'s {@code isInstance} check. */
    private record Dispatcher<T>(Class<T> payloadType, Consumer<? super T> action) {
        @SuppressWarnings("unchecked")
        void apply(InterModComms.IMCMessage msg, Object payload) {
            if (payloadType.isInstance(payload)) {
                action.accept((T) payload);
            } else {
                Species.LOGGER.warn("Species IMC '{}' from '{}': expected payload of type {}, got {}",
                        msg.method(), msg.senderModId(), payloadType.getSimpleName(),
                        payload == null ? "null" : payload.getClass().getName());
            }
        }

        static <T> Dispatcher<T> of(Class<T> type, Consumer<? super T> action) {
            return new Dispatcher<>(type, action);
        }
    }

    /** Method-name → handler. Adding a new IMC axis is one line here. */
    private static final Map<String, Dispatcher<?>> HANDLERS = Map.ofEntries(
            Map.entry(SpeciesIMCKeys.REGISTER_BEHAVIOR,
                    Dispatcher.of(BehaviorRegistration.class, r -> SpeciesAPI.registerBehavior(r.type(), r.behavior()))),
            Map.entry(SpeciesIMCKeys.COMPOSE_BEHAVIOR,
                    Dispatcher.of(BehaviorRegistration.class, r -> SpeciesAPI.composeBehavior(r.type(), r.behavior()))),
            Map.entry(SpeciesIMCKeys.REGISTER_GLOBAL_BEHAVIOR,
                    Dispatcher.of(DisguiseBehavior.class, SpeciesAPI::registerGlobalBehavior)),
            Map.entry(SpeciesIMCKeys.REGISTER_COSMETICS,
                    Dispatcher.of(CosmeticsRegistration.class, r -> SpeciesAPI.registerCosmetics(r.type(), r.cosmetics()))),
            Map.entry(SpeciesIMCKeys.COMPOSE_COSMETICS,
                    Dispatcher.of(CosmeticsRegistration.class, r -> SpeciesAPI.composeCosmetics(r.type(), r.cosmetics()))),
            Map.entry(SpeciesIMCKeys.REGISTER_SUB_ENTITY_PROVIDER,
                    Dispatcher.of(SubEntityProviderRegistration.class, r -> SpeciesAPI.registerSubEntityProvider(r.type(), r.provider()))),
            Map.entry(SpeciesIMCKeys.REGISTER_GLOBAL_SUB_ENTITY_PROVIDER,
                    Dispatcher.of(SubEntityProvider.class, SpeciesAPI::registerGlobalSubEntityProvider)),
            Map.entry(SpeciesIMCKeys.SET_INVENTORY_SCALE,
                    Dispatcher.of(InventoryScaleEntry.class, e -> SpeciesAPI.setInventoryScale(e.type(), e.scale()))),
            Map.entry(SpeciesIMCKeys.SET_CAMERA_SIZE_MINIMUM,
                    Dispatcher.of(CameraSizeMinimumEntry.class, e -> SpeciesAPI.setCameraSizeMinimum(e.type(), e.minVisualSize()))),
            Map.entry(SpeciesIMCKeys.SET_INVENTORY_Y_OFFSET,
                    Dispatcher.of(InventoryYOffsetEntry.class, e -> SpeciesAPI.setInventoryYOffset(e.type(), e.yOffset()))),
            Map.entry(SpeciesIMCKeys.SET_WORLD_Y_OFFSET,
                    Dispatcher.of(WorldYOffsetEntry.class, e -> SpeciesAPI.setWorldYOffset(e.type(), e.yOffset()))),
            Map.entry(SpeciesIMCKeys.SET_SHADOW_OVERRIDE,
                    Dispatcher.of(ShadowOverrideEntry.class, e -> {
                        if (!Float.isNaN(e.radius())) SpeciesAPI.setShadowRadius(e.type(), e.radius());
                        if (!Float.isNaN(e.strength())) SpeciesAPI.setShadowStrength(e.type(), e.strength());
                    })),
            Map.entry(SpeciesIMCKeys.REGISTER_PHASE_CYCLE,
                    Dispatcher.of(PhaseCycleRegistration.class, r -> SpeciesAPI.registerPhaseCycle(r.type(), r.handler()))),
            Map.entry(SpeciesIMCKeys.REGISTER_INT_STATE_CYCLE,
                    Dispatcher.of(IntStateCycleRegistration.class, r -> SpeciesAPI.registerIntStateCycle(r.type(), r.spec()))),
            Map.entry(SpeciesIMCKeys.REGISTER_COMPANION_STACK,
                    Dispatcher.of(CompanionStackRegistration.class, r -> registerCompanionStackTypeErased(r))),
            Map.entry(SpeciesIMCKeys.REGISTER_RENDER_LAYER,
                    Dispatcher.of(RenderLayerRegistration.class, r -> SpeciesAPI.registerRenderLayer(r.type(), r.layer())))
    );

    /** Type-erasure helper — IMC payloads can't carry a parametric type, so route via raw. */
    @SuppressWarnings({"unchecked", "rawtypes"})
    private static void registerCompanionStackTypeErased(CompanionStackRegistration r) {
        SpeciesAPI.registerCompanionStack(r.type(), (CompanionStackSpec) r.spec());
    }

    private SpeciesIMCHandler() {}

    public static void onIMC(InterModProcessEvent event) {
        event.getIMCStream().forEach(SpeciesIMCHandler::dispatch);
    }

    private static void dispatch(InterModComms.IMCMessage msg) {
        Dispatcher<?> handler = HANDLERS.get(msg.method());
        if (handler == null) {
            Species.LOGGER.warn("Species IMC: unknown method '{}' from '{}'", msg.method(), msg.senderModId());
            return;
        }
        Object payload;
        try {
            payload = msg.messageSupplier().get();
        } catch (Throwable t) {
            Species.LOGGER.error("Species IMC '{}' from '{}' threw resolving payload: {}",
                    msg.method(), msg.senderModId(), t.toString());
            return;
        }
        try {
            handler.apply(msg, payload);
        } catch (Throwable t) {
            Species.LOGGER.error("Species IMC '{}' from '{}' failed during dispatch: {}",
                    msg.method(), msg.senderModId(), t.toString());
        }
    }
}
