package com.ninni.species.client.model.mob.update_3;

import com.ninni.species.client.animation.WickedAnimations;
import com.ninni.species.server.entity.mob.update_3.Wicked;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class WickedModel<T extends Wicked> extends HierarchicalModel<T> {
    private final ModelPart root;
    private final ModelPart all;
    private final ModelPart body;
    private final ModelPart torso;
    private final ModelPart candle;
    private final ModelPart left_arm;
    private final ModelPart right_arm;

    public WickedModel(ModelPart root) {
        this.root = root;
        this.all = root.getChild("all");
        this.body = this.all.getChild("body");
        this.torso = this.body.getChild("torso");
        this.candle = this.torso.getChild("candle");
        this.left_arm = this.body.getChild("left_arm");
        this.right_arm = this.body.getChild("right_arm");
    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        float tilt = Math.min(limbDistance / 0.3f, 1.0f);
        float speed = 0.75f;
        float degree = 0.75f;
        this.torso.xRot = tilt * 0.25F;
        this.candle.xRot = tilt * -0.25F;
        this.left_arm.xRot = tilt;
        this.right_arm.xRot = tilt;
        this.left_arm.zRot = tilt * -0.25F;
        this.right_arm.zRot = tilt * 0.25F;

        this.all.y = Mth.sin(animationProgress * speed * 0.15F) * degree + 24.0F;

        this.body.y = Mth.sin(animationProgress * speed * 0.6F) * degree * 0.5F -11.0F;
        this.body.xRot = Mth.cos((animationProgress * speed * 0.3F)) * degree * 0.25F * 0.25F;

        this.left_arm.xRot += Mth.cos((animationProgress * speed * 0.2F)) * degree * 0.5F * 0.25F;
        this.left_arm.zRot += Mth.sin((animationProgress * speed * 0.2F)) * degree * 0.5F * 0.25F - 0.2F;
        this.left_arm.y = Mth.sin((animationProgress * speed * 0.2F) + 0.8F) * degree * 0.5F -8.0F;

        this.right_arm.xRot += Mth.cos((animationProgress * speed * 0.2F)) * degree * 0.5F * 0.25F;
        this.right_arm.zRot += Mth.sin((animationProgress * speed * 0.2F)) * degree * 0.5F * 0.25F + 0.2F;
        this.right_arm.y = Mth.sin((animationProgress * speed * 0.2F) + 0.8F) * degree * 0.5F -8.0F;

        this.candle.y = Mth.sin(animationProgress * speed * 0.6F) * degree * 0.25F;
        this.candle.xRot += Mth.cos((animationProgress * speed * 0.3F) - 0.5F) * degree * 0.25F * 0.25F;

        this.animate(entity.attackAnimationState, WickedAnimations.ATTACK, animationProgress);
        this.animate(entity.spotAnimationState, WickedAnimations.SPOT, animationProgress);
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition body = all.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.offset(0.0F, -15.0F, 1.0F));

        PartDefinition torso = body.addOrReplaceChild("torso", CubeListBuilder.create().texOffs(28, 10).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 23).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 13.0F, 4.0F, new CubeDeformation(0.5F))
                .texOffs(25, 23).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 13.0F, 4.0F, new CubeDeformation(0.49F)), PartPose.offset(0.0F, -8.0F, -1.0F));

        PartDefinition candle = torso.addOrReplaceChild("candle", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -6.0F, -4.0F, 8.0F, 6.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(0, 14).addBox(-4.0F, -3.0F, -4.0F, 8.0F, 1.0F, 8.0F, new CubeDeformation(0.25F))
                .texOffs(0, 0).addBox(-2.0F, -10.0F, 0.0F, 4.0F, 4.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition left_arm = body.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(0, 40).addBox(-0.5F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.1F))
                .texOffs(8, 40).addBox(-0.5F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.25F)), PartPose.offset(4.5F, -8.0F, -1.0F));

        PartDefinition right_arm = body.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 40).mirror().addBox(-1.5F, 0.0F, -1.0F, 2.0F, 15.0F, 2.0F, new CubeDeformation(0.1F)).mirror(false)
                .texOffs(8, 40).mirror().addBox(-1.5F, 0.0F, -1.0F, 2.0F, 13.0F, 2.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(-4.5F, -8.0F, -1.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
