package com.ninni.species.client.model.entity;

import com.google.common.collect.ImmutableList;
import com.ninni.species.entity.DeepfishEntity;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.List;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class DeepfishEntityModel<T extends DeepfishEntity> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart body;
    private final ModelPart leftEar;
    private final ModelPart rightEar;
    private final ModelPart tail;

    public DeepfishEntityModel(ModelPart root) {
        this.root = root;

        this.body = root.getChild(PartNames.BODY);

        this.leftEar = this.body.getChild(PartNames.LEFT_EAR);
        this.rightEar  = this.body.getChild(PartNames.RIGHT_EAR);
        this.tail = this.body.getChild(PartNames.TAIL);
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition modelData = new MeshDefinition();
        PartDefinition root = modelData.getRoot();

        PartDefinition body = root.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
                        .texOffs(0, 16)
                        .addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 20.0F, 0.0F)
        );

        PartDefinition leftEar = body.addOrReplaceChild(
                PartNames.LEFT_EAR,
                CubeListBuilder.create()
                        .texOffs(0, 28)
                        .addBox(0.0F, -2.5F, -2.5F, 2.0F, 5.0F, 5.0F, new CubeDeformation(0.0F)),
                PartPose.offset(4.0F, 0.5F, -0.5F)
        );

        PartDefinition rightEar = body.addOrReplaceChild(
                PartNames.RIGHT_EAR,
                CubeListBuilder.create()
                        .texOffs(0, 28)
                        .mirror()
                        .addBox(-2.0F, -2.5F, -2.5F, 2.0F, 5.0F, 5.0F, new CubeDeformation(0.0F))
                        .mirror(false),
                PartPose.offset(-4.0F, 0.5F, -0.5F)
        );

        PartDefinition tail = body.addOrReplaceChild(
                PartNames.TAIL,
                CubeListBuilder.create()
                        .texOffs(18, 8)
                        .addBox(0.0F, -2.0F, 0.0F, 0.0F, 4.0F, 8.0F, new CubeDeformation(0.0F)),
                PartPose.offset(0.0F, 0.0F, 4.0F)
        );

        return LayerDefinition.create(modelData, 48, 48);
    }


    @Override
    public ModelPart root() {
        return this.root;
    }

    public List<ModelPart> getAllParts() {
        return ImmutableList.of(this.body, this.tail, this.leftEar, this.rightEar);
    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        limbDistance = Mth.clamp(limbDistance, -0.25F, 0.25F);
        float speed = 1.5f;
        float degree = 1.0f;
        this.body.xRot = headPitch * ((float)Math.PI / 180);
        this.body.yRot = headYaw * ((float)Math.PI / 180);

        this.body.yRot += Mth.cos(animationProgress * speed * 0.15F) * degree * 0.25F;
        this.body.y = Mth.cos(animationProgress * speed * 0.15F + (float)Math.PI / 2) * degree * 1 + 20.0F;
        this.tail.yRot = Mth.cos(limbAngle * speed * 2.1F + 0.5F) * degree * 3F * limbDistance;
        this.tail.xRot = Mth.cos(animationProgress * speed * 0.15F + (float)Math.PI / 2 + 0.5F) * degree * 1.5F * 0.25F;
    }
}
