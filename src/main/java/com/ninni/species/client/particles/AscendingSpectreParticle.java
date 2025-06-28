package com.ninni.species.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class AscendingSpectreParticle extends TextureSheetParticle {
    private final SpriteSet spriteProvider;

    AscendingSpectreParticle(ClientLevel world, double v, double v1, double v2, double x, double y, double z, SpriteSet spriteProvider, float quadSize) {
        super(world, v, v1, v2, x, y, z);
        this.spriteProvider = spriteProvider;
        this.gravity = -0.1F;
        this.friction = 0.9F;
        this.xd = x + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
        this.yd = y + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
        this.zd = z + (Math.random() * 2.0D - 1.0D) * (double)0.05F;
        this.quadSize = 0.25F;
        this.lifetime = world.random.nextInt(60) + 20;
        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public void tick() {
        super.tick();

        if (age < lifetime) {
            if (alpha > 0.015F) alpha -= 0.015F;
            else this.remove();
        }

        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
    public record SmokeFactory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            return new AscendingSpectreParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider, 0.25F);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public record SoulFactory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            return new AscendingSpectreParticle(clientWorld, d, e, f, g, h, i, this.spriteProvider, 0.1F);
        }
    }
}