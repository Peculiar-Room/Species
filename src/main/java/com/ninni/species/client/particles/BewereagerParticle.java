package com.ninni.species.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class BewereagerParticle extends TextureSheetParticle {
    private final SpriteSet spriteProvider;

    public BewereagerParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites, boolean inverted) {
        super(world, x, y, z);

        this.gravity = (-0.3F) * (inverted ? -0.5F : 1);
        this.quadSize = 0.2F;
        this.lifetime = inverted ? 20 : 10;

        setSpriteFromAge(this.spriteProvider = sprites);
    }

    @Override
    public void tick() {
        super.tick();
        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }


    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double speed, double pYSpeed, double pZSpeed) {
            return new BewereagerParticle(pLevel, pX, pY, pZ, sprites, false);
        }
    }

    @Override
    protected int getLightColor(float tint) {
        return 240;
    }

    @OnlyIn(Dist.CLIENT)
    public static class ProviderInverted implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public ProviderInverted(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double speed, double pYSpeed, double pZSpeed) {
            return new BewereagerParticle(pLevel, pX, pY, pZ, sprites, true);
        }
    }
}