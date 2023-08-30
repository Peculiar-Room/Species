package com.ninni.species.client.model.entity;

import com.ninni.species.client.animation.TreeperAnimations;
import com.ninni.species.entity.Treeper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.phys.Vec3;

@Environment(EnvType.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class TreeperModel<T extends Treeper> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart leftArm;
    private final ModelPart root1;
    private final ModelPart root2;
    private final ModelPart root3;
    private final ModelPart rightArm;
    private final ModelPart root4;
    private final ModelPart root5;
    private final ModelPart root6;
    private final ModelPart rightLeg;
    private final ModelPart root7;
    private final ModelPart root8;
    private final ModelPart root9;
    private final ModelPart leftLeg;
    private final ModelPart root10;
    private final ModelPart root11;
    private final ModelPart root12;
    private final ModelPart trunk;
    private final ModelPart rightEye;
    private final ModelPart leftEye;
    private final ModelPart canopy;

    public TreeperModel(ModelPart root) {
        this.root = root;

        this.leftArm = root.getChild("leftArm");
        this.root1 = leftArm.getChild("root1");
        this.root2 = leftArm.getChild("root2");
        this.root3 = leftArm.getChild("root3");

        this.rightArm = root.getChild("rightArm");
        this.root4 = rightArm.getChild("root4");
        this.root5 = rightArm.getChild("root5");
        this.root6 = rightArm.getChild("root6");

        this.rightLeg = root.getChild("rightLeg");
        this.root7 = rightLeg.getChild("root7");
        this.root8 = rightLeg.getChild("root8");
        this.root9 = rightLeg.getChild("root9");

        this.leftLeg = root.getChild("leftLeg");
        this.root10 = leftLeg.getChild("root10");
        this.root11 = leftLeg.getChild("root11");
        this.root12 = leftLeg.getChild("root12");


        this.trunk = root.getChild("trunk");

        this.rightEye = trunk.getChild("rightEye");
        this.leftEye = trunk.getChild("leftEye");
        this.canopy = trunk.getChild("canopy");
    }

    @Override
    public void setupAnim(T treeper, float f, float g, float h, float i, float j) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (treeper.getPose() != Pose.DIGGING) {
            Entity entity = Minecraft.getInstance().getCameraEntity();
            Vec3 vec3 = (entity).getEyePosition(0.0F);
            Vec3 vec32 = treeper.getEyePosition(0.0F);
            float d = (float) (vec3.y - vec32.y);
            if (d <= 4.5 && d >= -4.5) {
                this.leftEye.y = -45.75F - d;
                this.rightEye.y = -47.75F - d;
            } else {
                this.leftEye.y = -45.75F;
                this.rightEye.y = -47.75F;
            }
            Vec3 vec33 = treeper.getViewVector(0.0F);
            vec33 = new Vec3(vec33.x, 0.0, vec33.z);
            Vec3 vec34 = (new Vec3(vec32.x - vec3.x, 0.0, vec32.z - vec3.z)).normalize().yRot(1.5707964F);
            double e = vec33.dot(vec34);
            this.leftEye.x = Mth.sqrt((float) Math.abs(e)) * 4.0F * (float) Math.signum(e) + 8.5F;
            this.rightEye.x = Mth.sqrt((float) Math.abs(e)) * 4.0F * (float) Math.signum(e) - 8.5F;
        }
        if (treeper.getPose() != Pose.STANDING) this.animate(treeper.plantingAnimationState, TreeperAnimations.PLANTS, h);
        this.animate(treeper.shakingFailAnimationState, TreeperAnimations.SHAKE_FAIL, h);
        this.animate(treeper.shakingSuccessAnimationState, TreeperAnimations.SHAKE_SUCCESS, h);

    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition leftArm = partdefinition.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, 0.0F, -7.0F, 16.0F, 32.0F, 14.0F), PartPose.offsetAndRotation(16.0F, -8.0F, -16.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition root = leftArm.addOrReplaceChild("root1", CubeListBuilder.create().texOffs(0, 46).addBox(0.0F, -6.0F, 0.0F, 20.0F, 12.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 26.0F, -7.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition root2 = leftArm.addOrReplaceChild("root2", CubeListBuilder.create().texOffs(0, 58).addBox(0.0F, -6.0F, 0.0F, 20.0F, 12.0F, 0.0F), PartPose.offsetAndRotation(8.0F, 26.0F, -7.0F, 0.0F, 1.1781F, 0.0F));

        PartDefinition root3 = leftArm.addOrReplaceChild("root3", CubeListBuilder.create().texOffs(0, 58).addBox(0.0F, -6.0F, 0.0F, 20.0F, 12.0F, 0.0F), PartPose.offsetAndRotation(-8.0F, 26.0F, -7.0F, 0.0F, 1.9635F, 0.0F));

        PartDefinition rightArm = partdefinition.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-8.0F, 0.0F, -7.0F, 16.0F, 32.0F, 14.0F).mirror(false), PartPose.offsetAndRotation(-16.0F, -8.0F, -16.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition root4 = rightArm.addOrReplaceChild("root4", CubeListBuilder.create().texOffs(0, 46).mirror().addBox(-20.0F, -6.0F, 0.0F, 20.0F, 12.0F, 0.0F).mirror(false), PartPose.offsetAndRotation(0.0F, 26.0F, -7.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition root5 = rightArm.addOrReplaceChild("root5", CubeListBuilder.create().texOffs(0, 58).mirror().addBox(-20.0F, -6.0F, 0.0F, 20.0F, 12.0F, 0.0F).mirror(false), PartPose.offsetAndRotation(-8.0F, 26.0F, -7.0F, 0.0F, -1.1781F, 0.0F));

        PartDefinition root6 = rightArm.addOrReplaceChild("root6", CubeListBuilder.create().texOffs(0, 58).mirror().addBox(-20.0F, -6.0F, 0.0F, 20.0F, 12.0F, 0.0F).mirror(false), PartPose.offsetAndRotation(8.0F, 26.0F, -7.0F, 0.0F, -1.9635F, 0.0F));

        PartDefinition rightLeg = partdefinition.addOrReplaceChild("rightLeg", CubeListBuilder.create().texOffs(0, 0).mirror().addBox(-8.0F, 0.0F, -7.0F, 16.0F, 32.0F, 14.0F).mirror(false), PartPose.offsetAndRotation(-16.0F, -8.0F, 16.0F, 0.0F, -0.7854F, 0.0F));

        PartDefinition root7 = rightLeg.addOrReplaceChild("root7", CubeListBuilder.create().texOffs(0, 46).mirror().addBox(-20.0F, -6.0F, 0.0F, 20.0F, 12.0F, 0.0F).mirror(false), PartPose.offsetAndRotation(0.0F, 26.0F, 7.0F, 0.0F, 1.5708F, 0.0F));

        PartDefinition root8 = rightLeg.addOrReplaceChild("root8", CubeListBuilder.create().texOffs(0, 58).mirror().addBox(-20.0F, -6.0F, 0.0F, 20.0F, 12.0F, 0.0F).mirror(false), PartPose.offsetAndRotation(-8.0F, 26.0F, 7.0F, 0.0F, 1.1781F, 0.0F));

        PartDefinition root9 = rightLeg.addOrReplaceChild("root9", CubeListBuilder.create().texOffs(0, 58).mirror().addBox(-20.0F, -6.0F, 0.0F, 20.0F, 12.0F, 0.0F).mirror(false), PartPose.offsetAndRotation(8.0F, 26.0F, 7.0F, 0.0F, 1.9635F, 0.0F));

        PartDefinition leftLeg = partdefinition.addOrReplaceChild("leftLeg", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, 0.0F, -7.0F, 16.0F, 32.0F, 14.0F), PartPose.offsetAndRotation(16.0F, -8.0F, 16.0F, 0.0F, 0.7854F, 0.0F));

        PartDefinition root10 = leftLeg.addOrReplaceChild("root10", CubeListBuilder.create().texOffs(0, 46).addBox(0.0F, -6.0F, 0.0F, 20.0F, 12.0F, 0.0F), PartPose.offsetAndRotation(0.0F, 26.0F, 7.0F, 0.0F, -1.5708F, 0.0F));

        PartDefinition root11 = leftLeg.addOrReplaceChild("root11", CubeListBuilder.create().texOffs(0, 58).addBox(0.0F, -6.0F, 0.0F, 20.0F, 12.0F, 0.0F), PartPose.offsetAndRotation(8.0F, 26.0F, 7.0F, 0.0F, -1.1781F, 0.0F));

        PartDefinition root12 = leftLeg.addOrReplaceChild("root12", CubeListBuilder.create().texOffs(0, 58).addBox(0.0F, -6.0F, 0.0F, 20.0F, 12.0F, 0.0F), PartPose.offsetAndRotation(-8.0F, 26.0F, 7.0F, 0.0F, -1.9635F, 0.0F));

        PartDefinition trunk = partdefinition.addOrReplaceChild("trunk", CubeListBuilder.create().texOffs(0, 80).addBox(-16.0F, -133.0F, -16.0F, 32.0F, 144.0F, 32.0F)
                .texOffs(128, 80).addBox(-16.0F, -133.0F, -16.0F, 32.0F, 144.0F, 32.0F, new CubeDeformation(1.0F)), PartPose.offset(0.0F, -3.0F, 0.0F));

        PartDefinition leftEye = trunk.addOrReplaceChild("leftEye", CubeListBuilder.create().texOffs(0, 73).addBox(-4.0F, -4.0F, -0.125F, 8.0F, 8.0F, 0.0F)
                .texOffs(0, 70).addBox(-1.0F, -1.0F, -0.375F, 2.0F, 2.0F, 1.0F), PartPose.offset(8.5F, -45.75F, -16.125F));

        PartDefinition rightEye = trunk.addOrReplaceChild("rightEye", CubeListBuilder.create().texOffs(0, 73).mirror().addBox(-4.0F, -4.0F, -0.125F, 8.0F, 8.0F, 0.0F).mirror(false)
                .texOffs(0, 70).mirror().addBox(-1.0F, -1.0F, -0.375F, 2.0F, 2.0F, 1.0F).mirror(false), PartPose.offset(-8.5F, -47.75F, -16.125F));

        PartDefinition canopy = trunk.addOrReplaceChild("canopy", CubeListBuilder.create().texOffs(64, 0).addBox(-16.0F, -8.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -8.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -8.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -8.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-64.0F, -8.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(48.0F, -8.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -8.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -8.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -8.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -8.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -8.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -8.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-64.0F, -8.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(48.0F, -8.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -8.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -8.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -8.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -8.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -8.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -8.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-64.0F, -8.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(48.0F, -8.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -8.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -8.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -8.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -8.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -8.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -8.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-64.0F, -8.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(48.0F, -8.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -8.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -8.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -8.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -8.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -8.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -8.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-64.0F, -8.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(48.0F, -8.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -8.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -8.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -8.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -8.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -8.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -8.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-64.0F, -8.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(48.0F, -8.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -8.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -8.0F, 48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -8.0F, 48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -8.0F, 48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -8.0F, 48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -8.0F, 48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -8.0F, 48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -8.0F, -64.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -8.0F, -64.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -8.0F, -64.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -8.0F, -64.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -8.0F, -64.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -8.0F, -64.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -8.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -88.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -40.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -40.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -40.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -40.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -40.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-64.0F, -40.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(48.0F, -40.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -40.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -40.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -40.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -40.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -40.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -40.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -40.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -40.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -40.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -40.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -40.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -40.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-64.0F, -40.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(48.0F, -40.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -40.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -40.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -40.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -40.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -40.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -40.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-64.0F, -40.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(48.0F, -40.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -40.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -40.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -40.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -40.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -40.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -40.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-64.0F, -40.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(48.0F, -40.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -40.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -40.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -40.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -40.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -40.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -40.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -40.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -40.0F, 48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -40.0F, 48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -40.0F, 48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -40.0F, 48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -40.0F, -64.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -40.0F, -64.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -40.0F, -64.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -40.0F, -64.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -56.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -24.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -24.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -24.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -24.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -24.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -24.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -24.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -24.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -24.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -24.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -24.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -24.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -24.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -24.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -24.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -24.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -24.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -24.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -24.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -24.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -24.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -24.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -24.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -24.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -24.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -24.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -24.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -24.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -24.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -24.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -24.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -24.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -56.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -56.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -56.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -56.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -56.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -56.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -56.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -56.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -56.0F, -48.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -56.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -56.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -56.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -56.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -56.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -56.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -56.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -56.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -56.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -56.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -56.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -56.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -56.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -56.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -56.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(32.0F, -56.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-48.0F, -56.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -56.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -56.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -56.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -56.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -56.0F, 32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -88.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -88.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -88.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -72.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -72.0F, -32.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -72.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -72.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -72.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -72.0F, -16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -72.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -72.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(16.0F, -72.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-32.0F, -72.0F, 0.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(0.0F, -72.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F))
                .texOffs(64, 0).addBox(-16.0F, -72.0F, 16.0F, 16.0F, 16.0F, 16.0F, new CubeDeformation(0.01F)), PartPose.offset(0.0F, -102.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
