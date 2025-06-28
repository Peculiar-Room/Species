package com.ninni.species.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Vector3f;

public class HangerCritParticle extends FloorParticle {
    private final SpriteSet sprites;

    public HangerCritParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z);

        alpha = 0.5F;
        quadSize = 0;
        lifetime = 48;
        this.sprites = sprites;
        setSpriteFromAge(sprites);
    }


    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;

        quadSize = Mth.lerp(0.3F, quadSize, 1.5F);

        if (age++ >= lifetime) {
            remove();
        } else {
            if (age > (lifetime / 2)) alpha -= 0.02F;
        }

        setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float delta) {
        this.renderRotatedParticle(consumer, camera, delta, true, 0.025F);
        this.renderRotatedParticle(consumer, camera, delta, false, 0.025F);
    }

    @Override
    protected int getLightColor(float tint) {
        return 240;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double speed, double pYSpeed, double pZSpeed) {
            return new HangerCritParticle(pLevel, pX, pY, pZ, sprites);
        }
    }
}