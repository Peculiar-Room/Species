package com.ninni.species.world;

import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerBossEvent;

public class CruncherBossEvent extends ServerBossEvent {
    public CruncherBossEvent(Component component, BossBarColor bossBarColor, BossBarOverlay bossBarOverlay) {
        super(component, bossBarColor, bossBarOverlay);
    }
}
