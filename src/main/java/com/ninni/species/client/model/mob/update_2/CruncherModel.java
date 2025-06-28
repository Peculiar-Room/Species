package com.ninni.species.client.model.mob.update_2;

import com.google.common.collect.ImmutableList;
import com.ninni.species.client.animation.CruncherAnimations;
import com.ninni.species.server.entity.mob.update_2.Cruncher;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class CruncherModel<T extends Cruncher> extends HierarchicalModel<T> {

    private final ModelPart root;

    private final ModelPart all;
    private final ModelPart body;
    private final ModelPart rightFrontLeg;
    private final ModelPart rightBackLeg;
    private final ModelPart leftBackLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart tail;
    private final ModelPart head;
    private final ModelPart jaw;

    public CruncherModel(ModelPart root) {
        this.root = root;

        this.all = root.getChild("all");

        this.body = all.getChild("body");
        this.rightFrontLeg = all.getChild("rightFrontLeg");
        this.rightBackLeg = all.getChild("rightBackLeg");
        this.leftBackLeg = all.getChild("leftBackLeg");
        this.leftFrontLeg = all.getChild("leftFrontLeg");

        this.tail = body.getChild("tail");
        this.head = body.getChild("head");
        this.leftArm = body.getChild("leftArm");
        this.rightArm = body.getChild("rightArm");

        this.jaw = head.getChild("jaw");
    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        this.animateWalk(CruncherAnimations.WALK, limbAngle, limbDistance, 2f, 2f);
        this.animate(entity.stunAnimationState, CruncherAnimations.STUN, animationProgress);
        this.animate(entity.idleAnimationState, CruncherAnimations.IDLE, animationProgress);
        this.animate(entity.spitAnimationState, CruncherAnimations.SPIT, animationProgress);
        this.animate(entity.roarAnimationState, CruncherAnimations.ROAR, animationProgress);
        this.animate(entity.attackAnimationState, CruncherAnimations.STOMP, animationProgress);
        this.head.xRot += headPitch * ((float) Math.PI / 180f);
        this.head.yRot += headYaw * ((float) Math.PI / 180f);
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 24.5F, 0.5F));

        PartDefinition body = all.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-13.5F, -15.5F, -16.0F, 27.0F, 31.0F, 32.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -30.0F, -0.5F));

        PartDefinition rightFrontLeg = all.addOrReplaceChild("rightFrontLeg", CubeListBuilder.create().texOffs(3, 143).mirror().addBox(-6.5F, -1.0F, -7.0F, 12.0F, 19.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(58, 71).mirror().addBox(-6.5F, 15.0F, -10.0F, 12.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(119, 56).mirror().addBox(-6.4375F, 14.9375F, -10.25F, 12.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(-11.0F, -18.5F, -13.5F));

        PartDefinition rightBackLeg = all.addOrReplaceChild("rightBackLeg", CubeListBuilder.create().texOffs(3, 143).mirror().addBox(-6.5F, -1.0F, -7.0F, 12.0F, 19.0F, 12.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(58, 71).mirror().addBox(-6.5F, 15.0F, -10.0F, 12.0F, 3.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(119, 56).mirror().addBox(-6.4375F, 14.9375F, -10.25F, 12.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(-11.0F, -18.5F, 15.5F));

        PartDefinition leftBackLeg = all.addOrReplaceChild("leftBackLeg", CubeListBuilder.create().texOffs(3, 143).addBox(-5.5F, -1.0F, -7.0F, 12.0F, 19.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(58, 71).addBox(-5.5F, 15.0F, -10.0F, 12.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(119, 56).addBox(-5.5625F, 14.9375F, -10.25F, 12.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offset(11.0F, -18.5F, 15.5F));

        PartDefinition leftFrontLeg = all.addOrReplaceChild("leftFrontLeg", CubeListBuilder.create().texOffs(3, 143).addBox(-5.5F, -1.0F, -7.0F, 12.0F, 19.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(58, 71).addBox(-5.5F, 15.0F, -10.0F, 12.0F, 3.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(119, 56).addBox(-5.5625F, 14.9375F, -10.25F, 12.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offset(11.0F, -18.5F, -13.5F));


        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(88, 63).addBox(-7.75F, -24.0F, -10.25F, 16.0F, 30.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(156, 163).addBox(-5.75F, -24.0F, -22.25F, 12.0F, 10.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(120, 175).addBox(-5.75F, -14.0F, -22.25F, 12.0F, 5.0F, 12.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-2.75F, -33.0F, -22.25F, 6.0F, 9.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(152, 74).addBox(-16.25F, -41.0F, -0.25F, 33.0F, 21.0F, 0.0F, new CubeDeformation(0.0F))
                .texOffs(114, 157).addBox(-12.25F, -36.0F, -0.25F, 25.0F, 16.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(-0.25F, -3.5F, -11.75F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(136, 50).addBox(-9.25F, -8.5F, -18.5F, 19.0F, 10.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(138, 95).addBox(-9.25F, 1.5F, -18.5F, 19.0F, 11.0F, 14.0F, new CubeDeformation(0.0F))
                .texOffs(58, 148).addBox(-9.25F, -1.5F, -4.5F, 19.0F, 14.0F, 9.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -15.5F, -6.5F));

        PartDefinition leftArm = body.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(160, 120).addBox(0.0F, 0.0F, -10.0F, 12.0F, 4.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(13.5F, -4.5F, -13.0F));

        PartDefinition rightArm = body.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(160, 120).mirror().addBox(-12.0F, 0.0F, -10.0F, 12.0F, 4.0F, 14.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(-13.5F, -3.75F, -13.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 63).addBox(-6.5F, -7.5F, -4.0F, 13.0F, 15.0F, 30.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.5F, 17.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    public List<ModelPart> getAllParts() {
        return ImmutableList.of(this.all);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

}