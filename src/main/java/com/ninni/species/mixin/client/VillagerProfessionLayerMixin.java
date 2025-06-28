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

@Mixin(VillagerProfessionLayer.class)
public abstract class VillagerProfessionLayerMixin<T extends LivingEntity & VillagerDataHolder, M extends EntityModel<T> & VillagerHeadModel> extends RenderLayer<T, M> {
    @Shadow protected abstract ResourceLocation getResourceLocation(String p_117669_, ResourceLocation p_117670_);
    @Shadow @Final private static Int2ObjectMap<ResourceLocation> LEVEL_LOCATIONS;

    public VillagerProfessionLayerMixin(RenderLayerParent<T, M> p_117346_) {
        super(p_117346_);
    }

    @Inject(method = "render", at = @At("HEAD"), cancellable = true)
    private void S$render(PoseStack poseStack, MultiBufferSource bufferSource, int p_117648_, T villager, float p_117650_, float p_117651_, float p_117652_, float p_117653_, float p_117654_, float p_117655_, CallbackInfo ci) {
        VillagerData $$10 = villager.getVillagerData();
        VillagerType $$11 = $$10.getType();
        VillagerProfession $$12 = $$10.getProfession();

        if (!villager.isInvisible() && $$11 == SpeciesVillagerTypes.CURED_BEWEREAGER.get()) {
            ci.cancel();
            M $$15 = this.getParentModel();
            ($$15).hatVisible(true);
            ResourceLocation $$16 = this.getResourceLocation("type", BuiltInRegistries.VILLAGER_TYPE.getKey($$11));
            renderColoredCutoutModel($$15, $$16, poseStack, bufferSource, p_117648_, villager, 1.0F, 1.0F, 1.0F);
            ($$15).hatVisible(true);
            if ($$12 != VillagerProfession.NONE && !villager.isBaby()) {
                ResourceLocation $$17 = this.getResourceLocation("profession", BuiltInRegistries.VILLAGER_PROFESSION.getKey($$12));
                renderColoredCutoutModel($$15, $$17, poseStack, bufferSource, p_117648_, villager, 1.0F, 1.0F, 1.0F);
                if ($$12 != VillagerProfession.NITWIT) {
                    ResourceLocation $$18 = this.getResourceLocation("profession_level", LEVEL_LOCATIONS.get(Mth.clamp($$10.getLevel(), 1, LEVEL_LOCATIONS.size())));
                    renderColoredCutoutModel($$15, $$18, poseStack, bufferSource, p_117648_, villager, 1.0F, 1.0F, 1.0F);
                }
            }

        }
    }
}
