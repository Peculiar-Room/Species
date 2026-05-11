package com.ninni.species.api.disguise.imc;

/**
 * Method-name constants for the Species Forge IMC contract; payload records live in this package.
 * Sent via {@link net.minecraftforge.fml.InterModComms#sendTo} during {@code InterModEnqueueEvent}.
 */
public final class SpeciesIMCKeys {

    /** Species' mod ID — IMC send target. */
    public static final String MOD_ID = "species";

    /** Replace the per-type behavior. Payload: {@link BehaviorRegistration}. */
    public static final String REGISTER_BEHAVIOR = "register_behavior";

    /** Compose with any existing per-type behavior. Payload: {@link BehaviorRegistration}. */
    public static final String COMPOSE_BEHAVIOR = "compose_behavior";

    /** Add a behavior applied to every disguise. Payload: {@link com.ninni.species.api.disguise.DisguiseBehavior}. */
    public static final String REGISTER_GLOBAL_BEHAVIOR = "register_global_behavior";

    /** Replace the per-type cosmetics. Payload: {@link CosmeticsRegistration}. */
    public static final String REGISTER_COSMETICS = "register_cosmetics";

    /** Compose with any existing per-type cosmetics. Payload: {@link CosmeticsRegistration}. */
    public static final String COMPOSE_COSMETICS = "compose_cosmetics";

    /** Register a sub-entity provider for a specific type. Payload: {@link SubEntityProviderRegistration}. */
    public static final String REGISTER_SUB_ENTITY_PROVIDER = "register_sub_entity_provider";

    /** Add a global sub-entity provider that self-gates per disguise. Payload: {@link com.ninni.species.api.disguise.SubEntityProvider}. */
    public static final String REGISTER_GLOBAL_SUB_ENTITY_PROVIDER = "register_global_sub_entity_provider";

    /** Set inventory-preview scale multiplier. Payload: {@link InventoryScaleEntry}. */
    public static final String SET_INVENTORY_SCALE = "set_inventory_scale";

    /** Set camera visual-size floor. Payload: {@link CameraSizeMinimumEntry}. */
    public static final String SET_CAMERA_SIZE_MINIMUM = "set_camera_size_minimum";

    /** Set inventory Y-offset (pre-scale world units). Payload: {@link InventoryYOffsetEntry}. */
    public static final String SET_INVENTORY_Y_OFFSET = "set_inventory_y_offset";

    /** Set in-world Y-offset (world units). Payload: {@link WorldYOffsetEntry}. */
    public static final String SET_WORLD_Y_OFFSET = "set_world_y_offset";

    /** Override shadow radius and/or strength on the disguise. Payload: {@link ShadowOverrideEntry}. */
    public static final String SET_SHADOW_OVERRIDE = "set_shadow_override";

    /** Register a sneak+keybind phase-cycle handler. Payload: {@link PhaseCycleRegistration}. */
    public static final String REGISTER_PHASE_CYCLE = "register_phase_cycle";

    /** Register a Wraptor-style integer visual cycle. Payload: {@link IntStateCycleRegistration}. */
    public static final String REGISTER_INT_STATE_CYCLE = "register_int_state_cycle";

    /** Register a Stackatick-style companion stack. Payload: {@link CompanionStackRegistration}. */
    public static final String REGISTER_COMPANION_STACK = "register_companion_stack";

    /** Register a render-layer for a specific type. Payload: {@link RenderLayerRegistration}. */
    public static final String REGISTER_RENDER_LAYER = "register_render_layer";

    private SpeciesIMCKeys() {}
}
