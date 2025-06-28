package com.ninni.species.server.entity.ai.goal;

import com.ninni.species.server.entity.mob.update_1.Birt;
import net.minecraft.world.entity.ai.goal.Goal;

public class SendMessageTicksGoal extends Goal {
    private final Birt birt;

    public SendMessageTicksGoal(Birt birt) {
        this.birt = birt;
    }

    @Override
    public boolean canUse() {
        return this.birt.findReciever() != null && this.birt.getRandom().nextInt(200) == 0 && !this.birt.canSendMessage();
    }

    @Override
    public void start() {
        this.birt.setMessageTicks(600);
    }

}
