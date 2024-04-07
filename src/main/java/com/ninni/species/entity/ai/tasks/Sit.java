package com.ninni.species.entity.ai.tasks;

import com.google.common.collect.ImmutableMap;
import com.ninni.species.entity.Cruncher;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.behavior.Behavior;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.memory.MemoryStatus;

import java.util.Map;

public class Sit extends Behavior<Cruncher> {

    public Sit() {
        super(ImmutableMap.of());
    }

    @Override
    protected boolean checkExtraStartConditions(ServerLevel world, Cruncher cruncher) {
        return cruncher.isOrderedToSit();
    }

    @Override
    protected boolean canStillUse(ServerLevel world, Cruncher cruncher, long p_22547_) {
        return this.checkExtraStartConditions(world, cruncher);
    }

    @Override
    protected void tick(ServerLevel world, Cruncher cruncher, long p_22553_) {
        cruncher.getNavigation().stop();
        cruncher.setInSittingPose(true);
    }

    @Override
    protected void stop(ServerLevel world, Cruncher cruncher, long p_22550_) {
        if (!this.canStillUse(world, cruncher, p_22550_)) {
            cruncher.setInSittingPose(false);
        }
    }

}
