package com.ninni.species.client.model.mob.update_3;

import com.ninni.species.client.animation.QuakeAnimations;
import com.ninni.species.server.entity.mob.update_3.Quake;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

public class QuakeModel<T extends Quake> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart bodyAnim;
    private final ModelPart body;
    private final ModelPart coreAnim;
    private final ModelPart core;
    private final ModelPart head;
    private final ModelPart leftSpikes;
    private final ModelPart rightSpikes;
    private final ModelPart leftHand;
    private final ModelPart rightHand;
    private final ModelPart leftFoot;
    private final ModelPart rightFoot;

    public QuakeModel(ModelPart root) {
        this.root = root;
        this.bodyAnim = this.root.getChild("bodyAnim");
        this.body = this.bodyAnim.getChild("body");
        this.coreAnim = this.body.getChild("coreAnim");
        this.core = this.coreAnim.getChild("core");
        this.head = this.body.getChild("head");
        this.leftSpikes = this.body.getChild("leftSpikes");
        this.rightSpikes = this.body.getChild("rightSpikes");
        this.leftHand = this.body.getChild("leftHand");
        this.rightHand = this.body.getChild("rightHand");
        this.leftFoot = this.root.getChild("leftFoot");
        this.rightFoot = this.root.getChild("rightFoot");
    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animateWalk(QuakeAnimations.WALK, limbAngle, limbDistance, 4.5f, 8.0f);
        this.animate(entity.attackAnimationState, QuakeAnimations.ATTACK, animationProgress);
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition bodyAnim = partdefinition.addOrReplaceChild("bodyAnim", CubeListBuilder.create(), PartPose.offset(-0.5F, 16.0F, 0.0F));
        PartDefinition body = bodyAnim.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-13.0F, -30.0F, -8.0F, 27.0F, 30.0F, 16.0F)
                .texOffs(112, 86).addBox(-8.0F, -22.0F, -8.0F, 17.0F, 19.0F, 7.0F)
                .texOffs(10, 93).addBox(-8.0F, -22.0F, -2.5F, 17.0F, 19.0F, 0.0F)
                .texOffs(0, 46).addBox(-13.0F, -29.3F, -8.0F, 27.0F, 30.0F, 16.0F, new CubeDeformation(0.75F))
                .texOffs(70, -10).addBox(15.25F, -26.8F, -5.0F, 0.0F, 11.0F, 10.0F)
                .texOffs(70, -10).mirror().addBox(-14.25F, -26.8F, -5.0F, 0.0F, 11.0F, 10.0F).mirror(false), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition coreAnim = body.addOrReplaceChild("coreAnim", CubeListBuilder.create(), PartPose.offset(0.5F, -22.0F, -4.0F));
        PartDefinition core = coreAnim.addOrReplaceChild("core", CubeListBuilder.create().texOffs(44, 93).addBox(-8.5F, 0.0F, 0.5F, 17.0F, 19.0F, 0.0F)
                .texOffs(78, 93).addBox(-8.5F, 0.0F, -0.5F, 17.0F, 19.0F, 0.0F), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(86, 49).addBox(-6.5F, -7.0F, -5.0F, 13.0F, 7.0F, 10.0F), PartPose.offset(0.5F, -30.0F, 0.0F));
        PartDefinition leftSpikes = body.addOrReplaceChild("leftSpikes", CubeListBuilder.create().texOffs(86, 33).addBox(0.0F, 0.0F, -8.0F, 10.0F, 0.0F, 16.0F), PartPose.offsetAndRotation(14.75F, -30.05F, 0.0F, 0.0F, 0.0F, -0.7854F));
        PartDefinition rightSpikes = body.addOrReplaceChild("rightSpikes", CubeListBuilder.create().texOffs(86, 33).mirror().addBox(-10.0F, 0.0F, -8.0F, 10.0F, 0.0F, 16.0F).mirror(false), PartPose.offsetAndRotation(-13.75F, -30.05F, 0.0F, 0.0F, 0.0F, 0.7854F));
        PartDefinition leftHand = body.addOrReplaceChild("leftHand", CubeListBuilder.create().texOffs(86, 0).addBox(-13.0F, 0.0F, -10.5F, 16.0F, 21.0F, 12.0F), PartPose.offsetAndRotation(13.5F, -20.975F, -0.5F, 0.0F, -0.7854F, 0.0F));
        PartDefinition rightHand = body.addOrReplaceChild("rightHand", CubeListBuilder.create().texOffs(86, 0).mirror().addBox(-3.0F, 0.0F, -10.5F, 16.0F, 21.0F, 12.0F).mirror(false), PartPose.offsetAndRotation(-12.5F, -20.975F, -0.5F, 0.0F, 0.7854F, 0.0F));
        PartDefinition leftFoot = partdefinition.addOrReplaceChild("leftFoot", CubeListBuilder.create().texOffs(122, 33).addBox(-4.0F, 0.5F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(6.5F, 15.95F, 0.0F));
        PartDefinition rightFoot = partdefinition.addOrReplaceChild("rightFoot", CubeListBuilder.create().texOffs(124, 58).addBox(-4.0F, 0.5F, -4.0F, 8.0F, 7.0F, 8.0F, new CubeDeformation(0.5F)), PartPose.offset(-6.5F, 15.95F, 0.0F));

        return LayerDefinition.create(meshdefinition, 160, 112);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

}
