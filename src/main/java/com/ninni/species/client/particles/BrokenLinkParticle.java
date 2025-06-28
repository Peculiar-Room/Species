package com.ninni.species.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class BrokenLinkParticle extends BaseAshSmokeParticle {

    BrokenLinkParticle(ClientLevel world, double x, double y, double z, SpriteSet spriteProvider, double xd2, double yd2, double zd2) {
        super(world, x, y, z, 1, 1, 1, xd2, yd2, zd2, 1, spriteProvider, 0, 1, 1F, true);
        this.setSprite(spriteProvider.get(this.random.nextInt(5), 5));
        this.lifetime = 20 + this.random.nextInt(20);
        this.rCol = 1;
        this.gCol = 1;
        this.bCol = 1;
        this.scale(2f);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        if (this.age++ >= this.lifetime) {
            this.remove();
        } else {
            this.yd -= 0.04 * (double)this.gravity;
            this.move(this.xd, this.yd, this.zd);
            if (this.speedUpWhenYMotionIsBlocked && this.y == this.yo) {
                this.xd *= 1.1;
                this.zd *= 1.1;
            }

            this.xd *= this.friction;
            this.yd *= this.friction;
            this.zd *= this.friction;
            if (this.onGround) {
                this.xd *= 0.699999988079071;
                this.zd *= 0.699999988079071;
            }
        }
    }

    @Override
    public int getLightColor(float f) {
        BlockPos blockPos = BlockPos.containing(this.x, this.y, this.z);
        if (this.level.hasChunkAt(blockPos)) {
            return LevelRenderer.getLightColor(this.level, blockPos);
        }
        return super.getLightColor(f);
    }

    @OnlyIn(Dist.CLIENT)
    public record Factory(SpriteSet spriteProvider) implements ParticleProvider<SimpleParticleType> {
        @Nullable
        @Override
        public Particle createParticle(SimpleParticleType particleOptions, ClientLevel clientLevel, double d, double e, double f, double g, double h, double i) {
            return new BrokenLinkParticle(clientLevel, d, e, f, this.spriteProvider,g,h,i);
        }
    }
}