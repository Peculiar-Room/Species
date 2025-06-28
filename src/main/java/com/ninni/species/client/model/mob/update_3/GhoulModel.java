package com.ninni.species.client.model.mob.update_3;

import com.ninni.species.client.animation.GhoulAnimations;
import com.ninni.species.server.entity.mob.update_3.Ghoul;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class GhoulModel<E extends Ghoul> extends HierarchicalModel<E> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart torso2;
    private final ModelPart torso;
    private final ModelPart head2;
    private final ModelPart head;
    private final ModelPart leftEar;
    private final ModelPart rightEar;

    public GhoulModel(ModelPart root) {
        this.root = root;

        this.body = this.root.getChild("body");
        
        this.leftArm = this.body.getChild("leftArm");
        this.rightArm = this.body.getChild("rightArm");
        this.leftLeg = this.body.getChild("leftLeg");
        this.rightLeg = this.body.getChild("rightLeg");
        this.torso2 = this.body.getChild("torso2");
        
        this.torso = this.torso2.getChild("torso");
        this.head2 = this.torso.getChild("head2");
        this.head = this.head2.getChild("head");
        
        this.leftEar = this.head.getChild("leftEar");
        this.rightEar = this.head.getChild("rightEar");
    }

    @Override
    public void setupAnim(E entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float speed = Math.min(limbDistance / 0.3f, 4.0f);

        if (entity.isCrawling()) {
            this.applyStatic(GhoulAnimations.SIT);
            this.animateWalk(GhoulAnimations.CRAWL, limbAngle, limbDistance, 9f, 8.0f);
        } else {
            if (speed > 2) {
                this.animateWalk(GhoulAnimations.RUN, limbAngle, limbDistance, 1.5f, 8.0f);
            } else this.animateWalk(GhoulAnimations.WALK, limbAngle, limbDistance, 9f, 8.0f);
        }

        this.animate(entity.idleAnimationState, GhoulAnimations.IDLE, animationProgress);
        this.animate(entity.searchAnimationState, GhoulAnimations.SEARCH, animationProgress);
        this.animate(entity.attackAnimationState, GhoulAnimations.ATTACK, animationProgress);
        this.animate(entity.confusedAnimationState, GhoulAnimations.CONFUSED, animationProgress);
    }
    
    @Override
    public ModelPart root() {
        return this.root;
    }


    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, -5.0F));

        body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(0, 0).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 15.0F, 4.0F, new CubeDeformation(-0.01F)), PartPose.offset(4.5F, -15.0F, -2.0F));
        body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-1.5F, 0.0F, -2.0F, 3.0F, 15.0F, 4.0F, new CubeDeformation(-0.01F)).mirror(false), PartPose.offset(-4.5F, -15.0F, -2.0F));
        body.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(40, 26).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 15.0F, 4.0F), PartPose.offset(4.5F, -15.0F, 13.0F));
        body.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(40, 26).mirror().addBox(-1.5F, 0.0F, -2.0F, 3.0F, 15.0F, 4.0F).mirror(false), PartPose.offset(-4.5F, -15.0F, 13.0F));
        PartDefinition torso2 = body.addOrReplaceChild("torso2", CubeListBuilder.create(), PartPose.offset(0.0F, -16.0F, 5.0F));
        PartDefinition torso = torso2.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(0, 1).addBox(-5.0F, -5.0F, -16.0F, 10.0F, 7.0F, 18.0F), PartPose.offset(0.0F, 0.0F, 6.0F));
        PartDefinition head2 = torso.addOrReplaceChild("head2", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, -15.0F));
        PartDefinition head = head2.addOrReplaceChild("head", CubeListBuilder.create().texOffs(0, 26).addBox(-6.0F, -5.0F, -7.0F, 12.0F, 7.0F, 8.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        head.addOrReplaceChild("leftEar", CubeListBuilder.create().texOffs(38, 0).addBox(-2.0F, -6.0F, 0.0F, 7.0F, 8.0F, 0.0F), PartPose.offset(6.0F, -3.0F, -1.0F));
        head.addOrReplaceChild("rightEar", CubeListBuilder.create().texOffs(38, 0).mirror().addBox(-5.0F, -6.0F, 0.0F, 7.0F, 8.0F, 0.0F).mirror(false), PartPose.offset(-6.0F, -3.0F, -1.0F));

        return LayerDefinition.create(meshdefinition, 64, 48);
    }
}
