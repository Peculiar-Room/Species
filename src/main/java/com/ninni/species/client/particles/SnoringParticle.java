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
public class SnoringParticle extends TextureSheetParticle {
    private final SpriteSet spriteProvider;


    SnoringParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider) {
        super(world, x, y, z);
        this.spriteProvider = spriteProvider;
        this.gravity = -0.025F;
        this.lifetime = 60;
        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        super.tick();
        if (age < lifetime) {
            if (alpha > 0.1F) {
                alpha -= 0.015F;
            } else {
                this.remove();
            }
        }
        xd = Mth.sin(age * 0.125F) * 0.05F;
        this.setSpriteFromAge(spriteProvider);
    }

    @Override
    protected int getLightColor(float f) {
        BlockPos blockPos = new BlockPos(this.x, this.y, this.z);
        if (this.level.hasChunkAt(blockPos)) {
            return LevelRenderer.getLightColor(this.level, blockPos);
        }
        return 0;
    }

    @OnlyIn(Dist.CLIENT)
    public record Factory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {

        @Override
        public Particle createParticle(SimpleParticleType defaultParticleType, ClientLevel clientWorld, double d, double e, double f, double g, double h, double i) {
            return new SnoringParticle(clientWorld, d, e, f, this.spriteProvider);
        }
    }
}
