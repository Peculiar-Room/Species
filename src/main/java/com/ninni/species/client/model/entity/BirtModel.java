package com.ninni.species.client.model.entity;

import com.google.common.collect.ImmutableList;
import com.ninni.species.client.animation.BirtAnimations;
import com.ninni.species.client.animation.CruncherAnimations;
import com.ninni.species.entity.Birt;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;

import java.util.List;

@Environment(EnvType.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class BirtModel<T extends Birt> extends HierarchicalModel<T> {
    public static final String ANTENNA = "antenna";

    private final ModelPart root;

    private final ModelPart body;
    private final ModelPart antenna;
    private final ModelPart tail;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;
    private final ModelPart leftWing;
    private final ModelPart rightWing;

    public BirtModel(ModelPart root) {
        this.root = root;

        this.body = root.getChild(PartNames.BODY);

        this.antenna = this.body.getChild(ANTENNA);
        this.tail = this.body.getChild(PartNames.TAIL);
        this.leftLeg = this.body.getChild(PartNames.LEFT_LEG);
        this.rightLeg = this.body.getChild(PartNames.RIGHT_LEG);
        this.leftWing = this.body.getChild(PartNames.LEFT_WING);
        this.rightWing = this.body.getChild(PartNames.RIGHT_WING);
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition modelPartData = modelData.getRoot();

        PartDefinition body = modelPartData.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.5F, -5.0F, -3.5F, 7.0F, 7.0F, 7.0F)
                        .texOffs(18, 17)
                        .addBox(-2.5F, -1.0F, -5.5F, 5.0F, 2.0F, 2.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F)
        );

        PartDefinition antenna = body.addOrReplaceChild(
                ANTENNA,
                CubeListBuilder.create()
                        .texOffs(0, 1)
                        .addBox(-0.5F, -4.0F, -0.5F, 1.0F, 5.0F, 1.0F),
                PartPose.offset(0.0F, -5.0F, 0.0F)
        );

        PartDefinition tail = body.addOrReplaceChild(
                PartNames.TAIL,
                CubeListBuilder.create()
                        .texOffs(0, 24)
                        .addBox(-1.5F, 0.0F, 0.0F, 3.0F, 1.0F, 3.0F),
                PartPose.offset(0.0F, 0.0F, 3.5F)
        );

        PartDefinition leftLeg = body.addOrReplaceChild(
                PartNames.LEFT_LEG,
                CubeListBuilder.create()
                        .texOffs(8, 14)
                        .addBox(-1.5F, 0.0F, -2.0F, 3.0F, 2.0F, 2.0F),
                PartPose.offset(2.0F, 2.0F, 0.5F)
        );

        PartDefinition rightLeg = body.addOrReplaceChild(
                PartNames.RIGHT_LEG,
                CubeListBuilder.create()
                        .texOffs(8, 14)
                        .addBox(-1.5F, 0.0F, -2.0F, 3.0F, 2.0F, 2.0F),
                PartPose.offset(-2.0F, 2.0F, 0.5F)
        );

        PartDefinition leftWing = body.addOrReplaceChild(
                PartNames.LEFT_WING,
                CubeListBuilder.create()
                        .texOffs(0, 14)
                        .addBox(0.0F, -2.0F, -0.5F, 1.0F, 4.0F, 6.0F),
                PartPose.offset(3.5F, -1.0F, -1.0F)
        );

        PartDefinition rightWing = body.addOrReplaceChild(
                PartNames.RIGHT_WING,
                CubeListBuilder.create()
                        .texOffs(0, 14)
                        .mirror()
                        .addBox(-1.0F, -2.0F, -0.5F, 1.0F, 4.0F, 6.0F)
                        .mirror(false),
                PartPose.offset(-3.5F, -1.0F, -1.0F)
        );

        return LayerDefinition.create(modelData, 32, 32);
    }

    public List<ModelPart> getAllParts() {
        return ImmutableList.of(this.body, this.antenna, this.leftWing, this.rightWing, this.tail);
    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity.antennaTicks > 0) {
            this.antenna.xRot += Mth.cos(animationProgress) * 0.25F;
        }


        this.rightLeg.xRot = Mth.cos(limbAngle * 0.6662f) * 1.8f * limbDistance;
        this.leftLeg.xRot = Mth.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.8f * limbDistance;

        this.animate(entity.flyingAnimationState, BirtAnimations.FLY, animationProgress);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

}
