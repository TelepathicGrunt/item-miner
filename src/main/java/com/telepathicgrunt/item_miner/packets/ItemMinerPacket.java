package com.telepathicgrunt.item_miner.packets;

import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;

import java.util.function.Supplier;

public interface ItemMinerPacket {
    void encode(PacketBuffer buffer);
    void handle(Supplier<NetworkEvent.Context> context);
}
