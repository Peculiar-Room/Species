package com.ninni.species.server.block.entity;

import com.ninni.species.registry.SpeciesBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import javax.annotation.Nullable;

public class BirtdayCakeBlockEntity extends BlockEntity {
    private String playerName = "";
    private int age = 0;

    public BirtdayCakeBlockEntity(BlockPos pos, BlockState state) {
        super(SpeciesBlockEntities.BIRTDAY_CAKE.get(), pos, state);
    }

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String name) {
        this.playerName = name;
        setChanged();
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
        setChanged();
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        if (tag.contains("PlayerName")) this.playerName = tag.getString("PlayerName");
        if (tag.contains("Age")) this.age = tag.getInt("Age");
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        if (!playerName.equals("")) tag.putString("PlayerName", playerName);
        if (age != 0) tag.putInt("Age", age);
        super.saveAdditional(tag);
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = super.getUpdateTag();
        if (!playerName.equals("")) tag.putString("PlayerName", playerName);
        if (age != 0) tag.putInt("Age", age);
        return tag;
    }

    @Override
    public void handleUpdateTag(CompoundTag tag) {
        super.handleUpdateTag(tag);
        this.load(tag);
    }

    @Nullable
    @Override
    public ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public void onDataPacket(Connection net, ClientboundBlockEntityDataPacket pkt) {
        handleUpdateTag(pkt.getTag());
    }
}