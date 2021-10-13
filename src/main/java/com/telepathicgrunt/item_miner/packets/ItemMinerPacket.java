package com.telepathicgrunt.item_miner.packets;

import com.telepathicgrunt.item_miner.MiningBehavior;
import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public interface ItemMinerPacket {
    void encode(PacketBuffer buffer);
    void handle(Supplier<NetworkEvent.Context> context);
}
