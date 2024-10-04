package com.ninni.species.client.model.entity;

import com.ninni.species.client.animation.MammutilationAnimations;
import com.ninni.species.entity.Mammutilation;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartNames;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class MammutilationModel<T extends Mammutilation> extends HierarchicalModel<T> {
    public static final String ALL = "all";
    public static final String LEFT_TUSK = "left_tusk";
    public static final String RIGHT_TUSK = "right_tusk";

    private final ModelPart root;
    private final ModelPart all;
    private final ModelPart head;
    private final ModelPart leftTusk;
    private final ModelPart rightTusk;
    private final ModelPart body;
    private final ModelPart tail;
    private final ModelPart leftArm;
    private final ModelPart rightArm;
    private final ModelPart leftLeg;
    private final ModelPart rightLeg;

    public MammutilationModel(ModelPart root) {
        this.root = root;

        this.all = this.root.getChild(ALL);

        this.head = this.all.getChild(PartNames.HEAD);
        this.body = this.all.getChild(PartNames.BODY);
        this.leftArm = this.all.getChild(PartNames.LEFT_ARM);
        this.rightArm = this.all.getChild(PartNames.RIGHT_ARM);
        this.leftLeg = this.all.getChild(PartNames.LEFT_LEG);
        this.rightLeg = this.all.getChild(PartNames.RIGHT_LEG);

        this.tail = this.body.getChild(PartNames.TAIL);

        this.leftTusk = this.head.getChild(LEFT_TUSK);
        this.rightTusk = this.head.getChild(RIGHT_TUSK);
    }

    @Override
    public ModelPart root() {
        return root;
    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);

        this.animateWalk(MammutilationAnimations.WALK, limbAngle, limbDistance, 4.5f, 8.0f);
        this.animate(entity.idleAnimationState, MammutilationAnimations.IDLE, animationProgress);
        this.animate(entity.coughAnimationState, MammutilationAnimations.COUGH, animationProgress);
        this.animate(entity.howlAnimationState, MammutilationAnimations.HOWL, animationProgress);
        this.head.xRot += headPitch * ((float) Math.PI / 180f);
        this.head.yRot += headYaw * ((float) Math.PI / 180f);
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition all = partdefinition.addOrReplaceChild(
                ALL,
                CubeListBuilder.create(),
                PartPose.offset(0.0F, -5.0F, 0.0F)
        );

        PartDefinition head = all.addOrReplaceChild(
                PartNames.HEAD,
                CubeListBuilder.create()
                        .texOffs(108, 0)
                        .addBox(10.0F, -6.8333F, -30.875F, 6.0F, 4.0F, 27.0F)
                        .texOffs(144, 37)
                        .addBox(2.0F, -6.8333F, -30.875F, 4.0F, 4.0F, 15.0F)
                        .texOffs(144, 37)
                        .addBox(-6.0F, -6.8333F, -30.875F, 4.0F, 4.0F, 15.0F)
                        .texOffs(144, 56)
                        .addBox(-6.0F, -2.8333F, -30.875F, 12.0F, 8.0F, 15.0F)
                        .texOffs(198, 56)
                        .addBox(-14.0F, 1.1667F, -30.875F, 8.0F, 8.0F, 15.0F)
                        .texOffs(198, 56)
                        .mirror()
                        .addBox(6.0F, 1.1667F, -30.875F, 8.0F, 8.0F, 15.0F)
                        .mirror(false)
                        .texOffs(108, 0)
                        .mirror()
                        .addBox(-16.0F, -6.8333F, -30.875F, 6.0F, 4.0F, 27.0F)
                        .mirror(false)
                        .texOffs(0, 0)
                        .addBox(-18.0F, -34.8333F, -30.875F, 36.0F, 48.0F, 36.0F)
                        .texOffs(0, 0)
                        .addBox(18.0F, -27.8333F, -21.875F, 4.0F, 12.0F, 12.0F)
                        .texOffs(0, 0)
                        .mirror()
                        .addBox(-22.0F, -27.8333F, -21.875F, 4.0F, 12.0F, 12.0F)
                        .mirror(false),
                PartPose.offset(0.0F, 1.8333F, -6.125F)
        );

        head.addOrReplaceChild(
                LEFT_TUSK,
                CubeListBuilder.create()
                        .texOffs(0, 144)
                        .addBox(-8.0F, -8.0F, -48.0F, 16.0F, 16.0F, 48.0F)
                        .texOffs(0, 134)
                        .addBox(8.0F, 8.0F, -10.0F, 0.0F, 6.0F, 10.0F),
                PartPose.offset(26.0F, -0.8333F, 0.125F)
        );

        head.addOrReplaceChild(
                RIGHT_TUSK,
                CubeListBuilder.create()
                        .texOffs(0, 144)
                        .mirror()
                        .addBox(-8.0F, -8.0F, -48.0F, 16.0F, 16.0F, 48.0F)
                        .mirror(false)
                        .texOffs(0, 134)
                        .mirror()
                        .addBox(-8.0F, 8.0F, -10.0F, 0.0F, 6.0F, 10.0F)
                        .mirror(false),
                PartPose.offset(-26.0F, -0.8333F, 0.125F)
        );

        PartDefinition body = all.addOrReplaceChild(
                PartNames.BODY,
                CubeListBuilder.create()
                        .texOffs(0, 84)
                        .addBox(-18.0F, -18.75F, -2.5F, 36.0F, 36.0F, 24.0F)
                        .texOffs(0, 208)
                        .mirror()
                        .addBox(-2.0F, -28.75F, 11.5F, 4.0F, 10.0F, 4.0F)
                        .mirror(false)
                        .texOffs(16, 208)
                        .addBox(-2.0F, -28.75F, 5.5F, 4.0F, 10.0F, 4.0F)
                        .texOffs(32, 208)
                        .addBox(-2.0F, -28.75F, -0.5F, 4.0F, 10.0F, 4.0F),
                PartPose.offset(0.0F, 1.75F, 1.5F)
        );

        body.addOrReplaceChild(
                PartNames.TAIL,
                CubeListBuilder.create()
                        .texOffs(0, 150)
                        .addBox(-5.0F, 0.0F, 0.0F, 10.0F, 26.0F, 0.0F),
                PartPose.offsetAndRotation(0.0F, -18.75F, 21.5F, 0.3927F, 0.0F, 0.0F)
        );

        all.addOrReplaceChild(
                PartNames.LEFT_ARM,
                CubeListBuilder.create()
                        .texOffs(120, 84)
                        .addBox(-10.0F, -4.5F, -10.0F, 20.0F, 35.0F, 20.0F),
                PartPose.offset(19.0F, -1.5F, -11.0F)
        );

        all.addOrReplaceChild(
                PartNames.RIGHT_ARM,
                CubeListBuilder.create()
                        .texOffs(120, 84)
                        .mirror().addBox(-10.0F, -4.5F, -10.0F, 20.0F, 35.0F, 20.0F)
                        .mirror(false),
                PartPose.offset(-19.0F, -1.5F, -11.0F)
        );

        all.addOrReplaceChild(
                PartNames.LEFT_LEG,
                CubeListBuilder.create()
                        .texOffs(120, 139)
                        .addBox(-10.0F, -5.0F, -10.0F, 20.0F, 28.0F, 20.0F),
                PartPose.offset(15.0F, 6.0F, 17.0F)
        );

        all.addOrReplaceChild(
                PartNames.RIGHT_LEG,
                CubeListBuilder.create()
                        .texOffs(120, 139)
                        .mirror()
                        .addBox(-10.0F, -5.0F, -10.0F, 20.0F, 28.0F, 20.0F)
                        .mirror(false),
                PartPose.offset(-15.0F, 6.0F, 17.0F)
        );

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

}