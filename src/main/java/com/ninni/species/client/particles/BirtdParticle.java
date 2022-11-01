package com.ninni.species.client.particles;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.*;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;

@Environment(EnvType.CLIENT)
public class BirtdParticle extends SpriteBillboardParticle {
    private final SpriteProvider spriteProvider;


    BirtdParticle(ClientWorld world, double x, double y, double z, SpriteProvider spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.maxAge = 50;
        scale = 0.2f;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    public ParticleTextureSheet getType() {
        return ParticleTextureSheet.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();

        if (age < maxAge) {
            if (alpha > 0.1F) alpha -= 0.015F;
            else this.markDead();
        }

        velocityX = MathHelper.cos(age * 0.115F) * 0.05F;
        velocityZ = MathHelper.sin(age * 0.115F) * 0.05F;
        this.setSpriteForAge(spriteProvider);
    }

    @Override
    @SuppressWarnings("deprecation")
    public int getBrightness(float tint) {
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        if (this.world.isChunkLoaded(blockPos)) {
            return WorldRenderer.getLightmapCoordinates(this.world, blockPos);
        }
        return 0;
    }

    @Environment(value = EnvType.CLIENT)
    public record Factory(SpriteProvider spriteProvider) implements ParticleFactory<DefaultParticleType> {

        @Override
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            return new BirtdParticle(clientWorld, d, e, f, this.spriteProvider);
        }
    }
}
