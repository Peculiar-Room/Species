package com.ninni.species.client.model.entity;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.RenderType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GooberGooModel extends Model {
    private final ModelPart root;

    public GooberGooModel(ModelPart modelPart) {
        super(RenderType::entitySolid);
        this.root = modelPart;
    }

    public static LayerDefinition createLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild(
                "all",
                CubeListBuilder.create()
                        .texOffs(0, 0)
                        .addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F)
                        .texOffs(0, 16)
                        .addBox(-3.0F, -3.0F, -3.0F, 6.0F, 6.0F, 6.0F),
                PartPose.offsetAndRotation(0.0F, 20.0F, 0.0F, -1.5708F, 0.0F, 0.0F)
        );

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int i, int j, float f, float g, float h, float k) {
        this.root.render(poseStack, vertexConsumer, i, j, f, g, h, k);
    }
}
