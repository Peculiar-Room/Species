package com.ninni.species.registry;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiFunction;

public class SpeciesRenderTypes extends RenderType {
    private static final BiFunction<ResourceLocation, Boolean, RenderType> ENTITY_TRANSLUCENT_EMISSIVE = Util.memoize((p_286163_, p_286164_) -> {
        CompositeState rendertype$compositestate = RenderType.CompositeState.builder().setShaderState(RENDERTYPE_ENTITY_TRANSLUCENT_EMISSIVE_SHADER).setTextureState(new RenderStateShard.TextureStateShard(p_286163_, false, false)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setWriteMaskState(COLOR_WRITE).setOverlayState(OVERLAY).setLayeringState(VIEW_OFFSET_Z_LAYERING).createCompositeState(p_286164_);
        return create("spectre_light", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, true, true, rendertype$compositestate);
    });

    public SpeciesRenderTypes(String p_173178_, VertexFormat p_173179_, VertexFormat.Mode p_173180_, int p_173181_, boolean p_173182_, boolean p_173183_, Runnable p_173184_, Runnable p_173185_) {
        super(p_173178_, p_173179_, p_173180_, p_173181_, p_173182_, p_173183_, p_173184_, p_173185_);
    }

    public static RenderType ScrollingTex(ResourceLocation p_110437_, float p_110438_, float p_110439_) {
        return create("scrolling_texture", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(p_110437_, false, false)).setTexturingState(new RenderStateShard.OffsetTexturingStateShard(p_110438_, p_110439_)).setTransparencyState(TRANSLUCENT_TRANSPARENCY).setCullState(NO_CULL).setLightmapState(NO_LIGHTMAP).setOverlayState(OVERLAY).createCompositeState(false));
    }
    public static RenderType spectreLight(ResourceLocation p_110437_) {
        return ENTITY_TRANSLUCENT_EMISSIVE.apply(p_110437_, true);
    }

    public static RenderType spectreBody(ResourceLocation texture) {
        RenderType.CompositeState state = RenderType.CompositeState.builder().setShaderState(RenderStateShard.RENDERTYPE_ENERGY_SWIRL_SHADER).setTextureState(new RenderStateShard.TextureStateShard(texture, false, false)).setTransparencyState(RenderStateShard.TRANSLUCENT_TRANSPARENCY).setCullState(RenderStateShard.NO_CULL).setLightmapState(RenderStateShard.NO_LIGHTMAP).setOverlayState(RenderStateShard.OVERLAY).createCompositeState(false);
        return RenderType.create("spectre_body", DefaultVertexFormat.NEW_ENTITY, VertexFormat.Mode.QUADS, 256, false, true, state);
}
}
