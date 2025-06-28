package com.ninni.species.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class SpectraliburParticle extends FloorParticle {
    private final SpriteSet sprites;
    private final Boolean inverted;

    public SpectraliburParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites, boolean inverted) {
        super(world, x, y, z);
        this.inverted = inverted;
        alpha = this.inverted ? 0 : 1;
        quadSize = this.inverted ? 2F : 0;
        lifetime = this.inverted ? 15 : 27;

        setSpriteFromAge(this.sprites = sprites);
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;

        quadSize = Mth.lerp(0.35F, quadSize, this.inverted ? 0 : 2F);

        if (age++ >= lifetime) {
            remove();
        } else {
            if (inverted) {
                if (age < (lifetime / 4)) alpha += 0.2F;
            } else {
                if (age > (lifetime / 2)) alpha -= 0.07F;
            }
        }

        setSpriteFromAge(sprites);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void render(VertexConsumer consumer, Camera camera, float delta) {
        this.renderRotatedParticle(consumer, camera, delta, true, 0.25F);
        this.renderRotatedParticle(consumer, camera, delta, false, 0.25F);

    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double speed, double pYSpeed, double pZSpeed) {
            return new SpectraliburParticle(pLevel, pX, pY, pZ, sprites, false);
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
            return new SpectraliburParticle(pLevel, pX, pY, pZ, sprites, true);
        }
    }
}