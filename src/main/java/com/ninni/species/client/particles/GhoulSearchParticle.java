package com.ninni.species.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

import java.util.function.Consumer;

public class GhoulSearchParticle extends FloorParticle {
    private final SpriteSet sprites;

    public GhoulSearchParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z);

        alpha = 1;
        quadSize = 0;
        lifetime = 48;

        setSpriteFromAge(this.sprites = sprites);
    }


    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;

        quadSize = Mth.lerp(0.06F, quadSize, 8F);

        if (age++ >= lifetime) {
            remove();
        } else {
            if (age > (lifetime / 2)) {
                alpha -= 0.04F;
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
        this.renderRotatedParticle(consumer, camera, delta, true, 0);
        this.renderRotatedParticle(consumer, camera, delta, false, 0);
    }

    @Override
    protected int getLightColor(float tint) {
        BlockPos blockpos = BlockPos.containing(this.x, this.y, this.z);
        return this.level.hasChunkAt(blockpos) ? LevelRenderer.getLightColor(this.level, blockpos) : 0;
    }

    @OnlyIn(Dist.CLIENT)
    public static class Provider implements ParticleProvider<SimpleParticleType> {
        private final SpriteSet sprites;

        public Provider(SpriteSet sprites) {
            this.sprites = sprites;
        }

        public Particle createParticle(SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double speed, double pYSpeed, double pZSpeed) {
            return new GhoulSearchParticle(pLevel, pX, pY, pZ, sprites);
        }
    }
}