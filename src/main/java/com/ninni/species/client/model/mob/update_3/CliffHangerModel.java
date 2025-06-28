package com.ninni.species.client.model.mob.update_3;

import com.ninni.species.server.entity.mob.update_3.CliffHanger;
import net.minecraft.client.model.geom.ModelPart;

public class CliffHangerModel<T extends CliffHanger> extends HangerModel<T> {

    public CliffHangerModel(ModelPart root) {
        super(root);
    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        super.setupAnim(entity, limbAngle, limbDistance, animationProgress, headYaw, headPitch);
        if (entity.isAttached()) {
            this.all.zRot += (float) Math.PI;
            this.all.y = 0;
        }
    }
}
