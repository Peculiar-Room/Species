package com.ninni.species.client.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
public class AscendingDustParticle extends TextureSheetParticle {
    private final SpriteSet spriteProvider;

    AscendingDustParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.gravity = -0.08F - world.random.nextFloat()/4;
        this.quadSize = 0.5F;
        this.lifetime = world.random.nextInt(60) + 20;
        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();

        if (age < lifetime) {
            if (alpha > 0.1F) alpha -= 0.015F;
            else this.remove();
        }

        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Environment(value = EnvType.CLIENT)
    public record Factory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            return new AscendingDustParticle(clientWorld, d, e, f, this.spriteProvider);
        }
    }
}
