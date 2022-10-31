package com.ninni.species.entity.entity.ai.goal;

import com.ninni.species.entity.BirtEntity;
import net.minecraft.entity.ai.TargetPredicate;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.EnumSet;
import java.util.List;

public class BirtCommunicatingGoal extends Goal {
    protected final BirtEntity sender;
    private final Class<? extends BirtEntity> entityClass;
    protected final World world;
    private int timer;
    @Nullable
    protected BirtEntity reciever;

    public BirtCommunicatingGoal(BirtEntity birt) {
        this(birt, birt.getClass());
    }

    public BirtCommunicatingGoal(BirtEntity birt, Class<? extends BirtEntity> entityClass) {
        this.sender = birt;
        this.world = birt.world;
        this.entityClass = entityClass;
        this.setControls(EnumSet.of(Goal.Control.MOVE, Goal.Control.LOOK));
    }

    @Override
    public boolean canStart() {
        if (!this.sender.canSendMessage()) return false;
        this.reciever = this.sender.findReciever();
        return this.reciever != null;
    }

    @Override
    public boolean shouldContinue() {
        assert this.reciever != null;
        return this.reciever.isAlive() && this.sender.canSendMessage() && this.timer < 60;
    }

    @Override
    public void stop() {
        this.reciever = null;
        this.timer = 0;
    }

    @Override
    public void tick() {
        this.sender.getLookControl().lookAt(this.reciever, 10.0f, this.sender.getMaxLookPitchChange());
        this.sender.getNavigation().stop();
        assert this.reciever != null;
        this.reciever.getLookControl().lookAt(this.sender, 10.0f, this.reciever.getMaxLookPitchChange());
        this.reciever.getNavigation().stop();
        ++this.timer;
        if (this.timer >= this.getTickCount(60)) {
            this.sendMessage();
        }
    }

    protected void sendMessage() {
        assert this.reciever != null;
        this.sender.sendMessage((ServerWorld)this.world, this.reciever);
    }
}
