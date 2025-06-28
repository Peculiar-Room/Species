package com.ninni.species.client.model.mob.update_3;

import com.ninni.species.client.animation.DeflectorDummyAnimations;
import com.ninni.species.server.entity.mob.update_3.DeflectorDummy;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class DeflectorDummyModel<T extends DeflectorDummy> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart all;
    private final ModelPart top;
    private final ModelPart spring;
    private final ModelPart body;

    public DeflectorDummyModel(ModelPart root) {
        this.root = root;
        this.all = root.getChild("all");
        this.top = this.all.getChild("top");
        this.spring = this.top.getChild("spring");
        this.body = this.top.getChild("body");
    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animate(entity.redirectAnimationState, DeflectorDummyAnimations.REDIRECT, animationProgress);
        this.animate(entity.absorbAnimationState, DeflectorDummyAnimations.ABSORB, animationProgress);
        this.animate(entity.releaseAnimationState, DeflectorDummyAnimations.RELEASE, animationProgress);
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -2.0F, -7.0F, 14.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition top = all.addOrReplaceChild("top", CubeListBuilder.create(), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition spring = top.addOrReplaceChild("spring", CubeListBuilder.create().texOffs(44, 16).addBox(-3.0F, -12.0F, -3.0F, 6.0F, 12.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition body = top.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 16).addBox(-6.0F, -17.0F, -5.0F, 12.0F, 17.0F, 10.0F, new CubeDeformation(0.0F))
                .texOffs(0, 43).addBox(-6.0F, -17.475F, -5.0F, 12.0F, 17.0F, 10.0F, new CubeDeformation(0.5F)), PartPose.offset(0.0F, -12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 80, 80);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
