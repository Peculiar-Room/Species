package com.ninni.species.client.model.entity;

import com.google.common.collect.ImmutableList;
import com.ninni.species.client.animation.GooberAnimations;
import com.ninni.species.client.model.HierarchicalAgeableListModel;
import com.ninni.species.entity.Goober;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

@Environment(EnvType.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class GooberModel<E extends Goober> extends HierarchicalAgeableListModel<E> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart neck;
    private final ModelPart head;
    private final ModelPart jaw;
    private final ModelPart tail;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;

    public GooberModel(ModelPart root) {
        this.root = root;

        this.body = this.root.getChild(PartNames.BODY);
        this.leftArm = this.root.getChild(PartNames.LEFT_ARM);
        this.rightArm = this.root.getChild(PartNames.RIGHT_ARM);
        this.rightLeg = this.root.getChild(PartNames.RIGHT_LEG);
        this.leftLeg = this.root.getChild(PartNames.LEFT_LEG);

        this.neck = this.body.getChild(PartNames.NECK);
        this.tail = this.body.getChild(PartNames.TAIL);

        this.head = this.neck.getChild(PartNames.HEAD);
        
        this.jaw = this.head.getChild(PartNames.JAW);
    }

    @Override
    public void setupAnim(E entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animateWalk(GooberAnimations.WALK, limbAngle, limbDistance, 4.5f, 8.0f);
        this.animate(entity.idleAnimationState, GooberAnimations.IDLE, animationProgress);
        this.animate(entity.layDownIdleAnimationState, GooberAnimations.LAY_DOWN_IDLE, animationProgress);
        this.animate(entity.layDownAnimationState, GooberAnimations.LAY_DOWN, animationProgress);
        this.animate(entity.standUpAnimationState, GooberAnimations.STAND_UP, animationProgress);
        this.animate(entity.yawnAnimationState, GooberAnimations.YAWN, animationProgress);
        this.animate(entity.layDownYawnAnimationState, GooberAnimations.LAY_DOWN_YAWN, animationProgress);
        this.animate(entity.rearUpAnimationState, GooberAnimations.REAR_UP, animationProgress);
        this.head.xRot += entity.isGooberLayingDown() ? 0f : headPitch * ((float) Math.PI / 180f) - (headPitch * ((float) Math.PI / 180f)) / 2;
        this.head.yRot += headYaw * ((float) Math.PI / 180f) - (headYaw * ((float) Math.PI / 180f)) / 2;
        this.neck.xRot += entity.isGooberLayingDown() ? 0f : (headPitch * ((float) Math.PI / 180f)) / 2;
        this.neck.yRot += (headYaw * ((float) Math.PI / 180f)) / 2;
    }
    
    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-11.0F, -18.0F, -16.0F, 22.0F, 22.0F, 26.0F),
                PartPose.offset(0.0F, 9.0F, 3.0F)
        );

        PartDefinition tail = body.addOrReplaceChild(
                PartNames.TAIL,
                CubeListBuilder.create()
                        .texOffs(12, 57)
                        .addBox(-5.0F, -1.5F, 0.0F, 10.0F, 3.0F, 32.0F),
                PartPose.offsetAndRotation(0.0F, 2.5F, 10.0F, -0.2618F, 0.0F, 0.0F)
        );

        PartDefinition neck = body.addOrReplaceChild(
                PartNames.NECK,
                CubeListBuilder.create()
                        .texOffs(64, 56)
                        .addBox(-7.0F, -8.0F, -23.0F, 14.0F, 10.0F, 23.0F),
                PartPose.offsetAndRotation(0.0F, 2.0F, -16.0F, -0.7854F, 0.0F, 0.0F)
        );

        PartDefinition head = neck.addOrReplaceChild(
                PartNames.HEAD,
                CubeListBuilder.create()
                        .texOffs(0, 48)
                        .addBox(-6.0F, -2.0F, -10.0F, 12.0F, 7.0F, 10.0F, new CubeDeformation(0.01F)),
                PartPose.offsetAndRotation(0.0F, -5.0F, -23.0F, 0.7854F, 0.0F, 0.0F)
        );

        PartDefinition jaw = head.addOrReplaceChild(
                PartNames.JAW, 
                CubeListBuilder.create()
                        .texOffs(0, 65)
                        .addBox(-6.0F, -4.0F, -10.0F, 12.0F, 9.0F, 10.0F)
                        .texOffs(93, 45)
                        .addBox(-6.0F, -3.0F, -5.0F, 12.0F, 8.0F, 3.0F),
                PartPose.offset(0.0F, 2.0F, 0.0F)
        );

        PartDefinition leftArm = partdefinition.addOrReplaceChild(
                PartNames.LEFT_ARM, 
                CubeListBuilder.create()
                        .texOffs(70, 1)
                        .addBox(-4.5F, -1.0F, -4.5F, 9.0F, 16.0F, 9.0F),
                PartPose.offset(8.5F, 9.0F, -6.5F)
        );

        PartDefinition rightArm = partdefinition.addOrReplaceChild(
                PartNames.RIGHT_ARM, 
                CubeListBuilder.create()
                        .texOffs(70, 1)
                        .addBox(-4.5F, -1.0F, -4.5F, 9.0F, 16.0F, 9.0F),
                PartPose.offset(-8.5F, 9.0F, -6.5F)
        );

        PartDefinition rightLeg = partdefinition.addOrReplaceChild(
                PartNames.RIGHT_LEG,
                CubeListBuilder.create()
                        .texOffs(70, 1)
                        .addBox(-4.5F, -1.0F, -4.5F, 9.0F, 16.0F, 9.0F),
                PartPose.offset(-9.5F, 9.0F, 7.5F)
        );

        PartDefinition leftLeg = partdefinition.addOrReplaceChild(
                PartNames.LEFT_LEG,
                CubeListBuilder.create()
                        .texOffs(70, 1)
                        .addBox(-4.5F, -1.0F, -4.5F, 9.0F, 16.0F, 9.0F),
                PartPose.offset(9.5F, 9.0F, 7.5F)
        );

        return LayerDefinition.create(meshdefinition, 144, 96);
    }

    @Override public ModelPart root() {
        return this.root;
    }

    @Override
    protected Iterable<ModelPart> headParts() {
        return ImmutableList.of(this.neck);
    }
    @Override
    protected Iterable<ModelPart> bodyParts() {
        return ImmutableList.of(this.body, this.leftArm, this.leftLeg, this.rightArm, this.rightLeg);
    }
}
