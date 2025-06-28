package com.ninni.species.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.joml.Quaternionf;
import org.joml.Vector3f;

public class SpectraliburReleasedParticle extends FloorParticle {
    private final SpriteSet sprites;

    public SpectraliburReleasedParticle(ClientLevel world, double x, double y, double z, SpriteSet sprites) {
        super(world, x, y, z);
        alpha = 1;
        quadSize = 0;
        lifetime = 80;

        setSpriteFromAge(this.sprites = sprites);
    }

    @Override
    public void tick() {
        xo = x;
        yo = y;
        zo = z;

        quadSize = Mth.lerp(0.06F, quadSize, 4F);

        if (age++ >= lifetime) {
            remove();
        } else {
            if (age > (lifetime / 6)) {
                alpha = Math.max(0, alpha - 0.02F);
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
        this.renderRotatedParticle(consumer, camera, delta, true, 0.025F);
        this.renderRotatedParticle(consumer, camera, delta, false, 0.025F);
    }

    void renderRotatedParticle(VertexConsumer vertexConsumer, Camera camera, float delta, boolean bl, float rotatingAmount) {
        Vec3 vec3 = camera.getPosition();
        float f = (float)(Mth.lerp(delta, this.xo, this.x) - vec3.x());
        float f1 = (float)(Mth.lerp(delta, this.yo, this.y) - vec3.y());
        float f2 = (float)(Mth.lerp(delta, this.zo, this.z) - vec3.z());
        Vector3f vector = (new Vector3f(0.5F, 0.5F, 0.5F)).normalize();
        Quaternionf quaternionf1 = (new Quaternionf()).setAngleAxis(0.0F, vector.x(), vector.y(), vector.z());
        quaternionf1.rotationX(Mth.PI / 2);
        Vector3f[] avector3f = new Vector3f[]{new Vector3f(-1.0F, -1.0F, 0.0F), new Vector3f(-1.0F, 1.0F, 0.0F), new Vector3f(1.0F, 1.0F, 0.0F), new Vector3f(1.0F, -1.0F, 0.0F)};
        float f3 = this.getQuadSize(delta);

        for(int i = 0; i < 4; ++i) {
            Vector3f vector3f = avector3f[i];
            vector3f.rotate(quaternionf1);
            if (bl) vector3f.rotate(new Quaternionf().rotationX(Mth.PI));
            if (rotatingAmount > 0) vector3f.rotate(new Quaternionf().rotationY(this.age * rotatingAmount));
            vector3f.rotate(new Quaternionf().rotationX((float) Math.sin(this.age * 1.25F) * 0.01F));
            vector3f.rotate(new Quaternionf().rotationZ((float) Math.cos(this.age * 1.25F) * 0.01F));
            vector3f.mul(f3);
            vector3f.add(f, f1, f2);
        }

        int j = this.getLightColor(delta);
        this.makeCornerVertex(vertexConsumer, avector3f[0], this.getU1(), this.getV1(), j);
        this.makeCornerVertex(vertexConsumer, avector3f[1], this.getU1(), this.getV0(), j);
        this.makeCornerVertex(vertexConsumer, avector3f[2], this.getU0(), this.getV0(), j);
        this.makeCornerVertex(vertexConsumer, avector3f[3], this.getU0(), this.getV1(), j);
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
            return new SpectraliburReleasedParticle(pLevel, pX, pY, pZ, sprites);
        }
    }
}