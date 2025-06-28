package com.ninni.species.server.data;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.ninni.species.Species;
import com.ninni.species.registry.SpeciesNetwork;
import com.ninni.species.server.packet.CruncherPelletSyncPacket;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.network.PacketDistributor;

import javax.annotation.Nullable;
import java.util.Map;

public class CruncherPelletManager extends SimpleJsonResourceReloadListener {
    public static final Gson GSON_INSTANCE = (new GsonBuilder()).create();
    public static final Map<ItemStack, CruncherPelletData> DATA = Maps.newHashMap();

    public CruncherPelletManager() {
        super(GSON_INSTANCE, "gameplay/cruncher_pellets");
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> object, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        DATA.clear();

        object.forEach((resourceLocation, jsonElement) -> {
            CruncherPelletManager.CruncherPelletData data = CruncherPelletManager.CruncherPelletData.CODEC.parse(JsonOps.INSTANCE, jsonElement).result()
                    .orElseGet(() -> {
                        Species.LOGGER.error("Failed to read Cruncher pellet recipe for resource {}", resourceLocation);
                        return null;
                    });

            if (data != null && data.item.getItem() != Items.AIR) DATA.put(data.item, data);
        });

        Species.LOGGER.info("Loaded {} Cruncher pellet recipes", DATA.size());
    }

    public void onDatapackSync(@Nullable ServerPlayer player) {
        if (DATA.isEmpty()) return;

        BiMap<ResourceLocation, CruncherPelletData> registryMap = HashBiMap.create();
        for (CruncherPelletData data : DATA.values()) {
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(data.item().getItem());
            if (id != null) {
                registryMap.put(id, data);
            }
        }

        if (player == null) {
            SpeciesNetwork.INSTANCE.send(PacketDistributor.ALL.noArg(), new CruncherPelletSyncPacket(registryMap));
        } else {
            SpeciesNetwork.INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), new CruncherPelletSyncPacket(registryMap));
        }
    }


    public void synchronizeRegistryForClient(Map<ResourceLocation, CruncherPelletData> newData) {
        DATA.clear();
        for (CruncherPelletData data : newData.values()) {
            if (data.item().getItem() != Items.AIR) {
                DATA.put(data.item(), data);
            }
        }
    }

    public record CruncherPelletData(EntityType<?> entityType, ItemStack item, int minTries, int maxTries) {
        public static final Codec<CruncherPelletData> CODEC = RecordCodecBuilder.create(instance ->
                instance.group(
                        BuiltInRegistries.ENTITY_TYPE.byNameCodec().fieldOf("type").forGetter(CruncherPelletData::entityType),
                        ItemStack.CODEC.fieldOf("item").forGetter(CruncherPelletData::item),
                        Codec.INT.fieldOf("minTries").forGetter(CruncherPelletData::minTries),
                        Codec.INT.fieldOf("maxTries").forGetter(CruncherPelletData::maxTries)
                ).apply(instance, CruncherPelletData::new));

        public static CruncherPelletData fromNetwork(FriendlyByteBuf buf) {
            EntityType<?> type = buf.readById(BuiltInRegistries.ENTITY_TYPE);
            ItemStack item = buf.readItem();
            int minTries = buf.readVarInt();
            int maxTries = buf.readVarInt();
            return new CruncherPelletData(type, item, minTries, maxTries);
        }

        public void toNetwork(FriendlyByteBuf buf) {
            buf.writeId(BuiltInRegistries.ENTITY_TYPE, this.entityType);
            buf.writeItem(this.item);
            buf.writeVarInt(this.minTries);
            buf.writeVarInt(this.maxTries);
        }
    }
}
