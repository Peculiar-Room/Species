package com.ninni.species.mixin.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.ninni.species.registry.SpeciesVillagerTypes;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.VillagerHeadModel;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.entity.layers.VillagerProfessionLayer;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerDataHolder;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.awt.*;

@Mixin(VillagerProfessionLayer.class)
public abstract class VillagerProfessionLayerMixin<T extends LivingEntity & VillagerDataHolder, M extends EntityModel<T> & VillagerHeadModel> extends RenderLayer<T, M> {
    @Shadow protected abstract ResourceLocation getResourceLocation(String p_117669_, ResourceLocation p_117670_);
    @Shadow @Final private static Int2ObjectMap<ResourceLocation> LEVEL_LOCATIONS;

    public VillagerProfessionLayerMixin(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void S$render(PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, T villager, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo ci) {
        VillagerData villagerData = villager.getVillagerData();
        VillagerType villagerType = villagerData.getType();
        VillagerProfession villagerProfession = villagerData.getProfession();

        if (!villager.isInvisible() && villagerType == SpeciesVillagerTypes.CURED_BEWEREAGER.get()) {
            ci.cancel();
            M m = this.getParentModel();
            (m).hatVisible(true);
            ResourceLocation typeLocation = this.getResourceLocation("type", BuiltInRegistries.VILLAGER_TYPE.getKey(villagerType));
            renderColoredCutoutModel(m, typeLocation, poseStack, bufferSource, packedLight, villager, -1);
            (m).hatVisible(true);
            if (villagerProfession != VillagerProfession.NONE && !villager.isBaby()) {
                ResourceLocation professionLocation = this.getResourceLocation("profession", BuiltInRegistries.VILLAGER_PROFESSION.getKey(villagerProfession));
                renderColoredCutoutModel(m, professionLocation, poseStack, bufferSource, packedLight, villager, -1);
                if (villagerProfession != VillagerProfession.NITWIT) {
                    ResourceLocation professionLevelLocation = this.getResourceLocation("profession_level", LEVEL_LOCATIONS.get(Mth.clamp(villagerData.getLevel(), 1, LEVEL_LOCATIONS.size())));
                    renderColoredCutoutModel(m, professionLevelLocation, poseStack, bufferSource, packedLight, villager, -1);
                }
            }

        }
    }
}
