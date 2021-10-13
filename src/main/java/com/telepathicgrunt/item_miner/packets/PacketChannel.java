package com.telepathicgrunt.item_miner.packets;

import com.telepathicgrunt.item_miner.ItemMiner;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.simple.SimpleChannel;

public class PacketChannel {
    //setup channel to send packages through
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel DEFAULT_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(ItemMiner.MODID, "networking"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals
    );

    /*
     * Register the channel so it exists
     */
    public static void init()
    {
        int channelID = -1;
        PacketChannel.DEFAULT_CHANNEL.registerMessage(++channelID, LevelProgressPacketHandler.UpdateLevelProgressPacket.class, LevelProgressPacketHandler.UpdateLevelProgressPacket::encode, LevelProgressPacketHandler.UpdateLevelProgressPacket::decode, LevelProgressPacketHandler.UpdateLevelProgressPacket::handle);
        PacketChannel.DEFAULT_CHANNEL.registerMessage(++channelID, ItemMinablePacketHandler.UpdateMinablePacket.class, ItemMinablePacketHandler.UpdateMinablePacket::encode, ItemMinablePacketHandler.UpdateMinablePacket::decode, ItemMinablePacketHandler.UpdateMinablePacket::handle);
    }
}
