package com.ninni.species.client.model.entity;

import com.google.common.collect.ImmutableList;
import com.ninni.species.entity.Cruncher;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.model.HierarchicalModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;

import java.util.List;

@Environment(EnvType.CLIENT)
@SuppressWarnings("FieldCanBeLocal, unused")
public class CruncherModel<T extends Cruncher> extends HierarchicalModel<T> {

    private final ModelPart root;

    private final ModelPart all;
    private final ModelPart body;
    private final ModelPart f_Rleg;
    private final ModelPart b_Rleg;
    private final ModelPart b_Lleg;
    private final ModelPart f_Lleg;
    private final ModelPart larm;
    private final ModelPart rarm;
    private final ModelPart tail;
    private final ModelPart head;
    private final ModelPart jaw;

    public CruncherModel(ModelPart root) {
        this.root = root;

        this.all = root.getChild("all");

        this.body = all.getChild("body");
        this.f_Rleg = all.getChild("f_Rleg");
        this.b_Rleg = all.getChild("b_Rleg");
        this.b_Lleg = all.getChild("b_Lleg");
        this.f_Lleg = all.getChild("f_Lleg");

        this.tail = body.getChild("tail");
        this.head = body.getChild("head");
        this.larm = body.getChild("larm");
        this.rarm = body.getChild("rarm");

        this.jaw = head.getChild("jaw");
    }

    @Override
    public void setupAnim(T entity, float limbAngle, float limbDistance, float animationProgress, float headYaw, float headPitch) {
        this.root().getAllParts().forEach(ModelPart::resetPose);
    }

    public static LayerDefinition getLayerDefinition() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition root = partdefinition.addOrReplaceChild("all", CubeListBuilder.create(), PartPose.offset(0.0F, 24.5F, 0.5F));

        PartDefinition body = root.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-13.5F, -53.5F, -16.5F, 27.0F, 31.0F, 32.0F), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition head = body.addOrReplaceChild("head", CubeListBuilder.create().texOffs(88, 63).addBox(-7.75F, -24.0F, -10.25F, 16.0F, 30.0F, 16.0F)
                .texOffs(156, 163).addBox(-5.75F, -24.0F, -22.25F, 12.0F, 10.0F, 12.0F)
                .texOffs(120, 175).addBox(-5.75F, -14.0F, -22.25F, 12.0F, 5.0F, 12.0F)
                .texOffs(0, 0).addBox(-2.75F, -33.0F, -22.25F, 6.0F, 9.0F, 6.0F)
                .texOffs(152, 74).addBox(-16.25F, -41.0F, -0.25F, 33.0F, 21.0F, 0.0F)
                .texOffs(114, 157).addBox(-12.25F, -36.0F, -0.25F, 25.0F, 16.0F, 2.0F), PartPose.offset(-0.25F, -41.5F, -12.25F));

        PartDefinition jaw = head.addOrReplaceChild("jaw", CubeListBuilder.create().texOffs(136, 50).addBox(-9.25F, -8.5F, -18.5F, 19.0F, 10.0F, 14.0F)
                .texOffs(138, 95).addBox(-9.25F, 1.5F, -18.5F, 19.0F, 11.0F, 14.0F)
                .texOffs(58, 148).addBox(-9.25F, -1.5F, -4.5F, 19.0F, 14.0F, 9.0F), PartPose.offset(0.0F, -15.5F, -6.5F));

        PartDefinition larm = body.addOrReplaceChild("larm", CubeListBuilder.create().texOffs(160, 120).addBox(0.0F, 0.0F, -10.0F, 12.0F, 4.0F, 14.0F), PartPose.offset(13.5F, -42.5F, -13.25F));

        PartDefinition rarm = body.addOrReplaceChild("rarm", CubeListBuilder.create().texOffs(160, 120).mirror().addBox(-12.0F, 0.0F, -10.0F, 12.0F, 4.0F, 14.0F).mirror(false), PartPose.offset(-13.5F, -41.75F, -13.25F));

        PartDefinition tail = body.addOrReplaceChild("tail", CubeListBuilder.create().texOffs(0, 63).addBox(-6.5F, -7.5F, -4.0F, 13.0F, 15.0F, 30.0F), PartPose.offset(0.0F, -37.5F, 16.5F));

        PartDefinition f_Rleg = root.addOrReplaceChild("f_Rleg", CubeListBuilder.create().texOffs(3, 143).mirror().addBox(-6.5F, -1.0F, -7.0F, 12.0F, 19.0F, 12.0F).mirror(false)
                .texOffs(58, 71).mirror().addBox(-6.5F, 15.0F, -10.0F, 12.0F, 3.0F, 3.0F).mirror(false)
                .texOffs(119, 56).mirror().addBox(-6.4375F, 14.9375F, -10.25F, 12.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(-11.0F, -18.5F, -13.5F));

        PartDefinition b_Rleg = root.addOrReplaceChild("b_Rleg", CubeListBuilder.create().texOffs(3, 143).mirror().addBox(-6.5F, -1.0F, -7.0F, 12.0F, 19.0F, 12.0F).mirror(false)
                .texOffs(58, 71).mirror().addBox(-6.5F, 15.0F, -10.0F, 12.0F, 3.0F, 3.0F).mirror(false)
                .texOffs(119, 56).mirror().addBox(-6.4375F, 14.9375F, -10.25F, 12.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)).mirror(false), PartPose.offset(-11.0F, -18.5F, 15.5F));

        PartDefinition b_Lleg = root.addOrReplaceChild("b_Lleg", CubeListBuilder.create().texOffs(3, 143).addBox(-5.5F, -1.0F, -7.0F, 12.0F, 19.0F, 12.0F)
                .texOffs(58, 71).addBox(-5.5F, 15.0F, -10.0F, 12.0F, 3.0F, 3.0F)
                .texOffs(119, 56).addBox(-5.5625F, 14.9375F, -10.25F, 12.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offset(11.0F, -18.5F, 15.5F));

        PartDefinition f_Lleg = root.addOrReplaceChild("f_Lleg", CubeListBuilder.create().texOffs(3, 143).addBox(-5.5F, -1.0F, -7.0F, 12.0F, 19.0F, 12.0F)
                .texOffs(58, 71).addBox(-5.5F, 15.0F, -10.0F, 12.0F, 3.0F, 3.0F)
                .texOffs(119, 56).addBox(-5.5625F, 14.9375F, -10.25F, 12.0F, 3.0F, 3.0F, new CubeDeformation(0.25F)), PartPose.offset(11.0F, -18.5F, -13.5F));

        return LayerDefinition.create(meshdefinition, 256, 256);
    }

    public List<ModelPart> getAllParts() {
        return ImmutableList.of(this.all);
    }

    @Override
    public ModelPart root() {
        return this.root;
    }

}
