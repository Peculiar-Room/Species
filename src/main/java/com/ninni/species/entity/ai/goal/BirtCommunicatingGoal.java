package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.BirtEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;

public class BirtCommunicatingGoal extends Goal {
    protected final BirtEntity sender;
    private final Class<? extends BirtEntity> entityClass;
    protected final Level world;
    private int timer;
    @Nullable
    protected BirtEntity reciever;

    public BirtCommunicatingGoal(BirtEntity birt) {
        this(birt, birt.getClass());
    }

    public BirtCommunicatingGoal(BirtEntity birt, Class<? extends BirtEntity> entityClass) {
        this.sender = birt;
        this.world = birt.level();
        this.entityClass = entityClass;
        this.setFlags(EnumSet.of(Goal.Flag.MOVE, Goal.Flag.LOOK));
    }

    @Override
    public boolean canUse() {
        if (!this.sender.canSendMessage()) return false;
        this.reciever = this.sender.findReciever();
        return this.reciever != null;
    }

    @Override
    public boolean canContinueToUse() {
        assert this.reciever != null;
        return this.reciever.isAlive() && this.sender.canSendMessage() && this.timer < 60;
    }

    @Override
    public void start() {
        this.world.broadcastEntityEvent(this.sender, (byte) 10);
        this.world.broadcastEntityEvent(this.reciever, (byte) 10);
        super.start();
    }

    @Override
    public void stop() {
        this.reciever = null;
        this.timer = 0;
    }

    @Override
    public void tick() {
        this.sender.getLookControl().setLookAt(this.reciever, 10.0f, this.sender.getMaxHeadXRot());
        this.sender.getNavigation().stop();
        assert this.reciever != null;
        this.reciever.getLookControl().setLookAt(this.sender, 10.0f, this.reciever.getMaxHeadXRot());
        this.reciever.getNavigation().stop();
        ++this.timer;
        if (this.timer >= this.adjustedTickDelay(60)) {
            this.sendMessage();
        }
    }

    protected void sendMessage() {
        assert this.reciever != null;
        this.sender.sendMessage((ServerLevel) this.world, this.reciever);
    }
}
