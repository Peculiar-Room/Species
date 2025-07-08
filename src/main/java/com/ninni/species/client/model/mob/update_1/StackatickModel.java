package com.ninni.species.client.model.mob.update_1;

import com.ninni.species.client.animation.StackatickAnimations;
import com.ninni.species.server.entity.mob.update_1.Stackatick;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Pose;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class StackatickModel<E extends Stackatick> extends HierarchicalModel<E> {

    private final ModelPart all;
    private final ModelPart shell;
    private final ModelPart leftAntenna;
    private final ModelPart rightAntenna;
    private final ModelPart leftMidLeg;
    private final ModelPart rightMidLeg;
    private final ModelPart leftFrontLeg;
    private final ModelPart rightFrontLeg;
    private final ModelPart leftBackleg;
    private final ModelPart rightBackleg;

    private final ModelPart root;

    public StackatickModel(ModelPart root) {
        this.root = root;

        this.all = root.getChild("all");
        this.shell = this.all.getChild("shell");
        this.leftAntenna = this.shell.getChild("leftAntenna");
        this.rightAntenna = this.shell.getChild("rightAntenna");
        this.leftMidLeg = this.all.getChild("leftMidLeg");
        this.rightMidLeg = this.all.getChild("rightMidLeg");
        this.leftFrontLeg = this.all.getChild("leftFrontLeg");
        this.rightFrontLeg = this.all.getChild("rightFrontLeg");
        this.leftBackleg = this.all.getChild("leftBackleg");
        this.rightBackleg = this.all.getChild("rightBackleg");
    }


    @Override
    public void setupAnim(E entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float pi = (float)Math.PI;

        float tamedAngle = entity.isTame() ? 0 : 0.6F;

        leftAntenna.zRot = Mth.sin(animationProgress * 0.2F) * 0.1F;
        leftAntenna.xRot += Mth.cos(animationProgress * 0.1F) * 0.2F + tamedAngle;
        rightAntenna.zRot = Mth.sin(animationProgress * 0.2F + pi) * 0.1F;
        rightAntenna.xRot += Mth.cos(animationProgress * 0.1F + pi) * 0.2F + tamedAngle;

        this.animateWalk(StackatickAnimations.WALK, limbAngle, limbDistance, 2.7f, 100);
        this.animate(entity.sitAnimationState, StackatickAnimations.SIT, animationProgress);
        this.animate(entity.standUpAnimationState, StackatickAnimations.STAND_UP, animationProgress);

        if (entity.getPose() == Pose.CROUCHING && !entity.sitAnimationState.isStarted()) {
            this.applyStatic(StackatickAnimations.SITTING);
        }
        if (this.young) this.applyStatic(StackatickAnimations.BABY_PROPORTIONS);
    }


    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 21.0F, 0.0F));

        PartDefinition shell = all.addOrReplaceChild("shell", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -6.0F, -8.0F, 16.0F, 6.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(0, 22).addBox(3.0F, 0.0F, -8.0F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 30).addBox(-8.0F, 0.0F, -8.0F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(20, 30).addBox(-8.0F, 0.0F, 3.0F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(20, 22).addBox(3.0F, 0.0F, 3.0F, 5.0F, 3.0F, 5.0F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(-3.0F, -9.0F, -8.0F, 6.0F, 3.0F, 16.0F, new CubeDeformation(0.0F))
                .texOffs(35, 33).addBox(-8.0F, -9.0F, -3.0F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(35, 24).addBox(3.0F, -9.0F, -3.0F, 5.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        PartDefinition leftAntenna = shell.addOrReplaceChild("leftAntenna", CubeListBuilder.create().texOffs(-10, 0).addBox(-0.5F, 0.0F, -10.0F, 4.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.5F, 0.0F, -8.0F, -1.0472F, 0.0F, 0.0F));

        PartDefinition rightAntenna = shell.addOrReplaceChild("rightAntenna", CubeListBuilder.create().texOffs(-10, 0).mirror().addBox(-3.5F, 0.0F, -10.0F, 4.0F, 0.0F, 10.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-1.5F, 0.0F, -8.0F, -1.0472F, 0.0F, 0.0F));

        PartDefinition leftMidLeg = all.addOrReplaceChild("leftMidLeg", CubeListBuilder.create().texOffs(8, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -5.0F, 0.0F, 0.0F, 0.0F, -0.6545F));

        PartDefinition rightMidLeg = all.addOrReplaceChild("rightMidLeg", CubeListBuilder.create().texOffs(8, 0).mirror().addBox(-2.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.0F, -5.0F, 0.0F, 0.0F, 0.0F, 0.6545F));

        PartDefinition leftFrontLeg = all.addOrReplaceChild("leftFrontLeg", CubeListBuilder.create().texOffs(8, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -4.75F, -2.0F, -0.3927F, 0.0F, -0.6545F));

        PartDefinition rightFrontLeg = all.addOrReplaceChild("rightFrontLeg", CubeListBuilder.create().texOffs(8, 0).mirror().addBox(-2.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.0F, -4.75F, -2.0F, -0.3927F, 0.0F, 0.6545F));

        PartDefinition leftBackleg = all.addOrReplaceChild("leftBackleg", CubeListBuilder.create().texOffs(8, 0).addBox(0.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(5.0F, -4.75F, 2.0F, 0.3927F, 0.0F, -0.6545F));

        PartDefinition rightBackleg = all.addOrReplaceChild("rightBackleg", CubeListBuilder.create().texOffs(8, 0).mirror().addBox(-2.0F, 0.0F, -1.0F, 2.0F, 10.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offsetAndRotation(-5.0F, -4.75F, 2.0F, 0.3927F, 0.0F, 0.6545F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
