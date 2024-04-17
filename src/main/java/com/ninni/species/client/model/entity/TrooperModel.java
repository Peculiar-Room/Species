package com.ninni.species.client.model.entity;

import com.google.common.collect.ImmutableList;
import com.ninni.species.entity.Trooper;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

import java.util.Collections;

@Environment(EnvType.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class TrooperModel<E extends Trooper> extends AgeableListModel<E> {

    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart rightArm;
    private final ModelPart leftArm;

    public TrooperModel(ModelPart root) {
        this.root = root;

        this.body = this.root.getChild(PartNames.BODY);
        this.rightLeg = this.root.getChild(PartNames.RIGHT_LEG);
        this.leftLeg = this.root.getChild(PartNames.LEFT_LEG);
        this.rightArm = this.root.getChild(PartNames.RIGHT_ARM);
        this.leftArm = this.root.getChild(PartNames.LEFT_ARM);
        
        this.head = this.body.getChild(PartNames.HEAD);
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition body = partdefinition.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create()
                        .texOffs(0, 18)
                        .addBox(-2.0F, -7.0F, -2.0F, 4.0F, 8.0F, 4.0F),
                PartPose.offset(0.0F, 20.0F, 0.0F)
        );

        PartDefinition leaves = body.addOrReplaceChild(
                "leaves",
                CubeListBuilder.create()
                        .texOffs(0, 12)
                        .addBox(-5.0F, -4.0F, 0.0F, 10.0F, 6.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, 0.7854F, 0.0F)
        );

        PartDefinition leaves2 = body.addOrReplaceChild(
                "leaves2",
                CubeListBuilder.create()
                        .texOffs(0, 12)
                        .mirror()
                        .addBox(-5.0F, -4.0F, 0.0F, 10.0F, 6.0F, 0.0F)
                        .mirror(false),
                PartPose.offsetAndRotation(0.0F, -3.0F, 0.0F, 0.0F, -0.7854F, 0.0F)
        );

        PartDefinition head = body.addOrReplaceChild(
                PartNames.HEAD,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-3.0F, -6.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.offset(0.0F, -7.0F, 0.0F)
        );

        PartDefinition leftArm = partdefinition.addOrReplaceChild(
                PartNames.LEFT_ARM,
                CubeListBuilder.create()
                        .texOffs(16, 18)
                        .addBox(-2.0F, 0.0F, -2.0F, 3.0F, 5.0F, 3.0F),
                PartPose.offset(-2.0F, 19.0F, -2.0F)
        );

        PartDefinition rightArm = partdefinition.addOrReplaceChild(
                PartNames.RIGHT_ARM,
                CubeListBuilder.create()
                        .texOffs(16, 18)
                        .mirror()
                        .addBox(-1.0F, 0.0F, -2.0F, 3.0F, 5.0F, 3.0F)
                        .mirror(false),
                PartPose.offset(2.0F, 19.0F, -2.0F)
        );

        PartDefinition leftLeg = partdefinition.addOrReplaceChild(
                PartNames.LEFT_LEG,
                CubeListBuilder.create()
                        .texOffs(16, 18)
                        .addBox(-2.0F, 0.0F, -1.0F, 3.0F, 5.0F, 3.0F),
                PartPose.offset(-2.0F, 19.0F, 2.0F)
        );

        PartDefinition rightLeg = partdefinition.addOrReplaceChild(
                PartNames.RIGHT_LEG,
                CubeListBuilder.create()
                        .texOffs(16, 18)
                        .mirror()
                        .addBox(-1.0F, 0.0F, -1.0F, 3.0F, 5.0F, 3.0F)
                        .mirror(false),
                PartPose.offset(2.0F, 19.0F, 2.0F)
        );

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override protected Iterable<ModelPart> headParts() { return Collections.emptyList(); }
    @Override protected Iterable<ModelPart> bodyParts() { return ImmutableList.of(this.root); }

    @Override
    public void setupAnim(E entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.head.yRot = headYaw * ((float)Math.PI / 180);
        this.head.xRot = headPitch * ((float)Math.PI / 180);
        this.rightLeg.xRot = Mth.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
        this.leftLeg.xRot = Mth.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.rightArm.xRot = Mth.cos(limbAngle * 0.6662f + (float)Math.PI) * 1.4f * limbDistance;
        this.leftArm.xRot = Mth.cos(limbAngle * 0.6662f) * 1.4f * limbDistance;
    }
}
