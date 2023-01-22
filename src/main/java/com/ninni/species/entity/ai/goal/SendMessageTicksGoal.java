package com.ninni.species.entity.ai.goal;

import com.ninni.species.entity.BirtEntity;
import net.minecraft.world.entity.ai.goal.Goal;

public class SendMessageTicksGoal extends Goal {
    private final BirtEntity birt;

    public SendMessageTicksGoal(BirtEntity birt) {
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
