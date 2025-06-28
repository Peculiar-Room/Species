package com.ninni.species.client.model.mob.update_1;

import com.google.common.collect.ImmutableList;
import com.ninni.species.client.animation.BirtAnimations;
import com.ninni.species.server.entity.mob.update_1.Birt;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
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
public class BirtModel<T extends Birt> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart all;
    private final ModelPart body;
    private final ModelPart left_leg;
    private final ModelPart left_foot;
    private final ModelPart right_leg;
    private final ModelPart right_foot;
    private final ModelPart tail;
    private final ModelPart antenna;
    private final ModelPart right_wing;
    private final ModelPart left_wing;

    public BirtModel(ModelPart root) {
        this.root = root;
        this.all = root.getChild("all");
        this.body = this.all.getChild("body");
        this.left_leg = this.body.getChild("left_leg");
        this.left_foot = this.left_leg.getChild("left_foot");
        this.right_leg = this.body.getChild("right_leg");
        this.right_foot = this.right_leg.getChild("right_foot");
        this.tail = this.body.getChild("tail");
        this.antenna = this.body.getChild("antenna");
        this.right_wing = this.body.getChild("right_wing");
        this.left_wing = this.body.getChild("left_wing");
    }

    public List<ModelPart> getAllParts() {
        return ImmutableList.of(this.body, this.antenna, this.right_leg, this.left_leg, this.right_foot, this.left_foot, this.left_wing, this.right_wing, this.tail);
    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        if (entity.antennaTicks > 0) {
            this.antenna.xRot += Mth.cos(animationProgress) * 0.5F;
            this.antenna.zRot += Mth.sin(animationProgress) * 0.5F;
        }
        this.animate(entity.flyingAnimationState, BirtAnimations.FLY, animationProgress);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }


    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = all.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-3.5F, -7.0F, -3.5F, 7.0F, 7.0F, 7.0F)
                .texOffs(0, 14).addBox(-2.5F, -2.0F, -5.5F, 5.0F, 2.0F, 2.0F), PartPose.offset(0.0F, -2.0F, 0.0F));

        PartDefinition left_leg = body.addOrReplaceChild("left_leg", CubeListBuilder.create().texOffs(18, 14).addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F), PartPose.offset(1.0F, 0.0F, 0.5F));

        PartDefinition left_foot = left_leg.addOrReplaceChild("left_foot", CubeListBuilder.create().texOffs(15, 16).addBox(-1.5F, 0.0F, -2.0F, 3.0F, 0.0F, 3.0F), PartPose.offset(0.0F, 2.0F, 0.0F));

        PartDefinition right_leg = body.addOrReplaceChild("right_leg", CubeListBuilder.create().texOffs(18, 14).mirror().addBox(-0.5F, 0.0F, 0.0F, 1.0F, 2.0F, 0.0F).mirror(false), PartPose.offset(-1.0F, 0.0F, 0.5F));

        PartDefinition right_foot = right_leg.addOrReplaceChild("right_foot", CubeListBuilder.create().texOffs(15, 16).mirror().addBox(-1.5F, 0.0F, -2.0F, 3.0F, 0.0F, 3.0F).mirror(false), PartPose.offset(0.0F, 2.0F, 0.0F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(10, 19).addBox(0.5F, 0.0F, 0.0F, 2.0F, 0.0F, 4.0F)
                .texOffs(10, 19).mirror().addBox(-2.5F, 0.0F, 0.0F, 2.0F, 0.0F, 4.0F).mirror(false), PartPose.offset(0.0F, -1.0F, 3.5F));

        PartDefinition antenna = body.addOrReplaceChild("antenna", CubeListBuilder.create().texOffs(14, 14).mirror().addBox(-0.5F, -4.0F, -0.5F, 1.0F, 4.0F, 1.0F).mirror(false), PartPose.offset(0.0F, -7.0F, 0.0F));

        PartDefinition right_wing = body.addOrReplaceChild("right_wing", CubeListBuilder.create().texOffs(0, 18).addBox(-1.0F, 0.0F, -2.5F, 1.0F, 5.0F, 5.0F), PartPose.offset(-3.5F, -5.0F, 0.0F));

        PartDefinition left_wing = body.addOrReplaceChild("left_wing", CubeListBuilder.create().texOffs(0, 18).mirror().addBox(0.0F, 0.0F, -2.5F, 1.0F, 5.0F, 5.0F).mirror(false), PartPose.offset(3.5F, -5.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}
