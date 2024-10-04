package com.ninni.species.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class RotatingParticle extends TextureSheetParticle {
    private final SpriteSet spriteProvider;
    private boolean birtd;

    RotatingParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider, boolean birtd) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.lifetime = 55;
        quadSize = this.birtd ? 0.2f : 0.25f;
        this.birtd = birtd;
        this.alpha = this.birtd ? this.alpha : 0.0f;
        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();

        if (birtd) {
            if (age < lifetime) {
                if (alpha > 0.1F) alpha -= 0.015F;
                else this.remove();
            }
            xd = Mth.cos(age * 0.115F) * 0.05F;
            zd = Mth.sin(age * 0.115F) * 0.05F;
        } else {
            this.x += 0.118F;
            this.z -= 1;
            if (age < 2) alpha = 0f;
            else alpha = 0.75f;
            xd = Mth.cos((age) * 0.118F) * 0.1F;
            zd = Mth.sin((age) * 0.118F) * 0.1F;
            yd = Mth.sin(age * 0.8F) * 0.05F;
        }

        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    protected int getLightColor(float f) {
        BlockPos blockPos = new BlockPos((int)this.x, (int)this.y, (int)this.z);
        if (this.level.hasChunkAt(blockPos)) {
            return LevelRenderer.getLightColor(this.level, blockPos);
        }
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public record BirtdFactory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            return new RotatingParticle(clientWorld, d, e, f, this.spriteProvider, true);
        }
    }

    @OnlyIn(Dist.CLIENT)
    public record FoodFactory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            return new RotatingParticle(clientWorld, d, e, f, this.spriteProvider, false);
        }
    }
}