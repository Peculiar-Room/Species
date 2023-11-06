package com.ninni.species.client.model.entity;

import com.ninni.species.entity.Springling;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

@Environment(EnvType.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class SpringlingModel<T extends Springling> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart neck;
    private final ModelPart rightArm;
    private final ModelPart leftArm;
    private final ModelPart rightLeg;
    private final ModelPart leftLeg;
    private final ModelPart tail;

    public SpringlingModel(ModelPart root) {
        this.root = root;

        body = root.getChild("body");
        head = root.getChild("head");
        neck = root.getChild("neck");
        tail = body.getChild("tail");
        rightLeg = body.getChild("rightLeg");
        leftLeg = body.getChild("leftLeg");
        rightArm = body.getChild("rightArm");
        leftArm = body.getChild("leftArm");

    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        limbDistance = Mth.clamp(limbDistance, -0.25F, 0.25F);
        this.rightLeg.xRot = Mth.cos(limbAngle * 0.6662f) * 3.4f * limbDistance;
        this.leftLeg.xRot = Mth.cos(limbAngle * 0.6662f + (float)Math.PI) * 3.4f * limbDistance;
        this.neck.yScale = entity.getExtendedAmount() * 1.3f;
        this.neck.yRot = entity.getExtendedAmount();
        this.head.y = 4.5F - entity.getExtendedAmount() * 20.75f;
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition head = partdefinition.addOrReplaceChild(
                "head",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-8.0F, -1.5F, -8.0F, 16.0F, 4.0F, 16.0F)
                        .texOffs(32, 20)
                        .mirror()
                        .addBox(-12.0F, -1.5F, -2.0F, 4.0F, 3.0F, 4.0F)
                        .mirror(false)
                        .texOffs(32, 20)
                        .addBox(8.0F, -1.5F, -2.0F, 4.0F, 3.0F, 4.0F),
                PartPose.offset(0.0F, -9.5F, 0.0F)
        );

        PartDefinition neck = partdefinition.addOrReplaceChild(
                "neck",
                CubeListBuilder.create()
                        .texOffs(0, 39)
                        .addBox(-2.0F, -16.0F, -2.0F, 4.0F, 16.0F, 4.0F),
                PartPose.offset(0.0F, 7.0F, 0.0F)
        );

        PartDefinition body = partdefinition.addOrReplaceChild(
                "body",
                CubeListBuilder.create()
                        .texOffs(0, 20)
                        .addBox(-6.0F, -17.0F, -4.0F, 12.0F, 11.0F, 8.0F),
                PartPose.offset(0.0F, 24.0F, 0.0F)
        );

        PartDefinition leftLeg = body.addOrReplaceChild(
                "leftLeg",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-2.0F, -1.0F, -2.0F, 4.0F, 7.0F, 4.0F),
                PartPose.offset(3.0F, -6.0F, -1.0F)
        );

        PartDefinition rightLeg = body.addOrReplaceChild(
                "rightLeg",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .mirror()
                        .addBox(-2.0F, -1.0F, -2.0F, 4.0F, 7.0F, 4.0F)
                        .mirror(false),
                PartPose.offset(-3.0F, -6.0F, -1.0F)
        );

        PartDefinition leftArm = body.addOrReplaceChild(
                "leftArm",
                CubeListBuilder.create()
                        .texOffs(16, 39)
                        .addBox(-1.5F, -2.5F, -6.0F, 3.0F, 5.0F, 6.0F),
                PartPose.offset(5.5F, -11.5F, -1.0F)
        );

        PartDefinition rightArm = body.addOrReplaceChild(
                "rightArm",
                CubeListBuilder.create()
                        .texOffs(16, 39)
                        .mirror()
                        .addBox(-1.5F, -2.5F, -6.0F, 3.0F, 5.0F, 6.0F)
                        .mirror(false),
                PartPose.offset(-5.5F, -11.5F, -1.0F)
        );

        PartDefinition tail = body.addOrReplaceChild(
                "tail",
                CubeListBuilder.create()
                        .texOffs(30, 29)
                        .addBox(-2.0F, -2.0F, 0.0F, 4.0F, 4.0F, 10.0F),
                PartPose.offset(0.0F, -8.0F, 4.0F)
        );

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

}
