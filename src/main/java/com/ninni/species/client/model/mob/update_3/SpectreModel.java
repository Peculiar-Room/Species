package com.ninni.species.client.model.mob.update_3;

import com.ninni.species.client.animation.SpectreAnimations;
import com.ninni.species.server.entity.mob.update_3.Spectre;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.util.Mth;

public class SpectreModel<E extends Spectre> extends HierarchicalModel<E> {
    private final ModelPart root;
    private final ModelPart all;
    private final ModelPart rightArm;
    private final ModelPart rightChain;
    private final ModelPart leftArm;
    private final ModelPart leftChain;
    private final ModelPart body;
    private final ModelPart head;
    private final ModelPart face;
    private final ModelPart collar;
    private final ModelPart helmet;
    private final ModelPart featherBase;
    private final ModelPart featherMiddle;
    private final ModelPart featherEnd;

    public SpectreModel(ModelPart root) {
        this.root = root;
        this.all = root.getChild("all");
        this.rightArm = this.all.getChild("rightArm");
        this.rightChain = this.rightArm.getChild("rightChain");
        this.leftArm = this.all.getChild("leftArm");
        this.leftChain = this.leftArm.getChild("leftChain");
        this.body = this.all.getChild("body");
        this.collar = this.body.getChild("collar");
        this.head = this.all.getChild("head");
        this.face = this.head.getChild("face");
        this.helmet = this.head.getChild("helmet");
        this.featherBase = this.helmet.getChild("featherBase");
        this.featherMiddle = this.featherBase.getChild("featherMiddle");
        this.featherEnd = this.featherMiddle.getChild("featherEnd");
    }

    @Override
    public void setupAnim(E entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
        float speed = 0.5f;
        float degree = 1.0f;
        this.head.xRot += headPitch * ((float) Math.PI / 180);
        this.head.yRot += headYaw * ((float) Math.PI / 180);

        if (entity.getVariant() == Spectre.Type.HULKING_SPECTRE) this.animate(entity.attackAnimationState, SpectreAnimations.SABLE_ATTACK, animationProgress);
        else this.animate(entity.attackAnimationState, SpectreAnimations.ATTACK, animationProgress);
        this.animate(entity.dashAnimationState, SpectreAnimations.JOUSTING_ATTACK, animationProgress);
        this.animate(entity.spawnAnimationState, SpectreAnimations.SPAWN, animationProgress);
        this.animate(entity.poofAnimationState, SpectreAnimations.POOF, animationProgress);

        if (entity.getVariant() == Spectre.Type.SPECTRE) {
            float tilt = Math.min(limbDistance / 0.3f, 1.0f);
            this.body.xRot = tilt;
            this.body.z = tilt * 5F;
            this.leftArm.xRot = -tilt;
            this.rightArm.xRot = -tilt;

            this.leftArm.y = Mth.cos((animationProgress * speed * 0.3F) - 0.8F) * degree * 0.5F;
            this.rightArm.y = Mth.sin((animationProgress * speed * 0.3F) - 0.5F) * degree * 0.5F + 0.5F;
        }
        if (entity.getVariant() == Spectre.Type.JOUSTING_SPECTRE) {
            float tilt = Math.min(limbDistance / 0.3f, 1.0f);
            this.leftArm.xRot = -tilt;
            this.rightArm.xRot = -tilt;

            this.leftArm.y = Mth.cos((animationProgress * speed * 0.3F) - 0.8F) * degree * 0.5F;
            this.rightArm.y = Mth.sin((animationProgress * speed * 0.3F) - 0.5F) * degree * 0.5F + 0.5F;
        }
        if (entity.getVariant() == Spectre.Type.HULKING_SPECTRE) {
            float tilt = Math.min(limbDistance / 0.3f, 1.0f);
            this.body.xRot = tilt * 0.5F;
            this.body.z = tilt * 2;

            this.leftArm.y = Mth.cos((animationProgress * speed * 0.3F) - 0.8F) * degree * 1F;
            this.rightArm.y = Mth.sin((animationProgress * speed * 0.3F) - 0.5F) * degree * 1F + 1F;
        }

        this.body.y = Mth.cos(animationProgress * speed * 0.3F) * degree * 0.5F + 1.5F;
        this.body.xRot += Mth.cos((animationProgress * speed * 0.3F) - 0.5F) * degree * 0.25F * 0.25F;

        this.leftArm.xRot += Mth.cos((animationProgress * speed * 0.3F)) * degree * 0.25F * 0.25F;

        this.leftChain.xRot = Mth.cos((animationProgress * speed * 0.3F) - 0.2F) * degree * 0.35F * 0.25F;
        this.leftChain.yRot = Mth.cos((animationProgress * speed * 0.3F) - 0.2F) * degree * 0.35F * 0.25F;
        this.leftChain.zRot = Mth.sin((animationProgress * speed * 0.3F) - 0.2F) * degree * 0.35F * 0.25F;
        this.leftChain.y = Mth.cos((animationProgress * speed * 0.3F) - 0.2F) * degree * 0.5F + 7F;

        this.rightArm.xRot += Mth.sin((animationProgress * speed * 0.3F)) * degree * 0.25F * 0.25F;

        this.rightChain.xRot = Mth.sin((animationProgress * speed * 0.3F)) * degree * 0.35F * 0.25F;
        this.rightChain.yRot = Mth.sin((animationProgress * speed * 0.3F)) * degree * 0.35F * 0.25F;
        this.rightChain.zRot = Mth.cos((animationProgress * speed * 0.3F)) * degree * 0.35F * 0.25F;
        this.rightChain.y = Mth.sin((animationProgress * speed * 0.3F)) * degree * 0.5F + 7F;

        this.head.y += Mth.cos(animationProgress * speed * 0.3F) * degree * 0.5F + 2F - 4F;
        this.head.xRot += Mth.sin((animationProgress * speed * 0.3F) - 0.5F) * degree * 0.25F * 0.25F;

        this.helmet.xRot += Mth.sin((animationProgress * speed * 0.3F) - 0.8F) * degree * 0.25F * 0.25F;

        this.featherBase.xRot = Mth.cos((animationProgress * speed * 0.3F) - 0.2F) * degree * 0.75F * 0.25F - 0.8F;
        this.featherMiddle.xRot = Mth.cos((animationProgress * speed * 0.3F) - 0.8F) * degree * 0.75F * 0.25F - 0.4F;
        this.featherEnd.xRot = Mth.cos((animationProgress * speed * 0.3F) - 1.2F) * degree * 0.75F * 0.25F - 0.2F;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 10.0F, 0.0F));
        PartDefinition rightArm = all.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(32, 15).addBox(-3.0F, -1.5F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 0.5F, 0.0F));
        rightArm.addOrReplaceChild("rightChain", CubeListBuilder.create().texOffs(0, 31).addBox(-3.0F, -1.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(-1.0F, 6.5F, 0.0F));
        PartDefinition leftArm = all.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(32, 15).mirror().addBox(-1.0F, -1.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(6.0F, 0.0F, 0.0F));
        leftArm.addOrReplaceChild("leftChain", CubeListBuilder.create().texOffs(0, 31).mirror().addBox(-3.0F, -1.0F, -3.0F, 6.0F, 2.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(1.0F, 7.0F, 0.0F));
        PartDefinition body = all.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 39).addBox(-4.0F, -2.5F, -2.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 51).addBox(-4.0F, 5.5F, -2.0F, 8.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, 0.0F));
        body.addOrReplaceChild("collar", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition head = all.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -1.75F, 1.0F));
        head.addOrReplaceChild("face", CubeListBuilder.create().texOffs(0, 15).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.25F, -1.0F));
        PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(0, 0).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(0.02F)), PartPose.offset(0.0F, -3.25F, -1.0F));
        PartDefinition featherBase = helmet.addOrReplaceChild("featherBase", CubeListBuilder.create().texOffs(38, 42).addBox(-1.5F, -6.0F, 0.0F, 3.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));
        PartDefinition featherMiddle = featherBase.addOrReplaceChild("featherMiddle", CubeListBuilder.create().texOffs(35, 37).addBox(-1.5F, -5.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));
        featherMiddle.addOrReplaceChild("featherEnd", CubeListBuilder.create().texOffs(32, 32).addBox(-1.5F, -5.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    public static LayerDefinition createSableBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 10.0F, 0.0F));
        PartDefinition rightArm = all.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(37, 32).addBox(-3.0F, 0.0F, -3.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 4).addBox(-1.0F, 13.0F, 4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).mirror().addBox(-6.0F, 13.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(24, 0).addBox(4.0F, 13.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-1.0F, 13.0F, -6.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(-4.0F, 10.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(-11.0F, -1.0F, 0.0F));
        rightArm.addOrReplaceChild("rightChain", CubeListBuilder.create().texOffs(0, 62).addBox(-4.0F, -1.5F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.5F, 0.0F));
        PartDefinition leftArm = all.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(37, 32).mirror().addBox(-3.0F, 0.0F, -3.0F, 6.0F, 10.0F, 6.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 4).mirror().addBox(-1.0F, 13.0F, 4.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(24, 0).addBox(4.0F, 13.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(24, 0).mirror().addBox(-6.0F, 13.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 0).mirror().addBox(-1.0F, 13.0F, -6.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)).mirror(false)
                .texOffs(0, 0).mirror().addBox(-4.0F, 10.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(11.0F, -1.0F, 0.0F));
        leftArm.addOrReplaceChild("leftChain", CubeListBuilder.create().texOffs(0, 62).addBox(-4.0F, -1.5F, -4.0F, 8.0F, 3.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 7.5F, 0.0F));
        PartDefinition body = all.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 32).addBox(-6.0F, -2.5F, -3.0F, 12.0F, 10.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(0, 50).addBox(-4.0F, 7.5F, -2.0F, 8.0F, 8.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, 0.0F));
        body.addOrReplaceChild("collar", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition head = all.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -1.75F, 1.0F));
        head.addOrReplaceChild("face", CubeListBuilder.create().texOffs(32, 0).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.25F, -1.0F));
        PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(0, 16).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(0.02F)), PartPose.offset(0.0F, -3.25F, -1.0F));
        PartDefinition featherBase = helmet.addOrReplaceChild("featherBase", CubeListBuilder.create().texOffs(54, 26).addBox(-1.5F, -6.0F, 0.0F, 3.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));
        PartDefinition featherMiddle = featherBase.addOrReplaceChild("featherMiddle", CubeListBuilder.create().texOffs(51, 21).addBox(-1.5F, -5.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));
        featherMiddle.addOrReplaceChild("featherEnd", CubeListBuilder.create().texOffs(48, 16).addBox(-1.5F, -5.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 80);
    }

    public static LayerDefinition createJoustingBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 10.0F, 0.0F));
        PartDefinition rightArm = all.addOrReplaceChild("rightArm", CubeListBuilder.create().texOffs(24, 52).addBox(-2.0F, 0.5F, -2.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)), PartPose.offset(-6.0F, 0.5F, 0.0F));
        rightArm.addOrReplaceChild("rightChain", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition leftArm = all.addOrReplaceChild("leftArm", CubeListBuilder.create().texOffs(24, 52).mirror().addBox(-1.0F, 1.0F, -2.0F, 3.0F, 12.0F, 3.0F, new CubeDeformation(0.0F)).mirror(false), PartPose.offset(6.0F, 0.0F, 0.0F));
        leftArm.addOrReplaceChild("leftChain", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition body = all.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 52).addBox(-4.0F, -0.5F, -2.0F, 8.0F, 5.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(4, 67).addBox(-2.0F, 4.5F, -2.0F, 4.0F, 9.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 1.5F, 0.0F));
        body.addOrReplaceChild("collar", CubeListBuilder.create().texOffs(0, 0).addBox(-8.0F, -1.5F, -8.0F, 16.0F, 3.0F, 16.0F, new CubeDeformation(0.1F)), PartPose.offset(0.0F, -1.5F, 0.0F));
        PartDefinition head = all.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, -1.75F, 1.0F));
        head.addOrReplaceChild("face", CubeListBuilder.create().texOffs(0, 36).addBox(-4.0F, -8.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -0.25F, -1.0F));
        PartDefinition helmet = head.addOrReplaceChild("helmet", CubeListBuilder.create().texOffs(0, 19).addBox(-5.0F, -5.0F, -5.0F, 10.0F, 5.0F, 10.0F, new CubeDeformation(0.02F))
                .texOffs(40, 20).addBox(-1.0F, -13.0F, -5.0F, 2.0F, 8.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -3.25F, -1.0F));
        PartDefinition featherBase = helmet.addOrReplaceChild("featherBase", CubeListBuilder.create().texOffs(58, 46).addBox(-1.5F, -6.0F, 0.0F, 3.0F, 6.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));
        PartDefinition featherMiddle = featherBase.addOrReplaceChild("featherMiddle", CubeListBuilder.create().texOffs(55, 41).addBox(-1.5F, -5.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -6.0F, 0.0F));
        featherMiddle.addOrReplaceChild("featherEnd", CubeListBuilder.create().texOffs(52, 36).addBox(-1.5F, -5.0F, 0.0F, 3.0F, 5.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -5.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 80);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }
}
