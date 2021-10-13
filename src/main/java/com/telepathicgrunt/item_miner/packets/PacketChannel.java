package com.telepathicgrunt.item_miner.packets;

import com.telepathicgrunt.item_miner.ItemMiner;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
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
    public static void init() {
        int channelID = -1;
        PacketChannel.DEFAULT_CHANNEL.registerMessage(++channelID, LevelProgressPacketHandler.class, LevelProgressPacketHandler::encode, LevelProgressPacketHandler::decode, LevelProgressPacketHandler::handle);
        PacketChannel.DEFAULT_CHANNEL.registerMessage(++channelID, MineableBlockPacket.class, MineableBlockPacket::encode, MineableBlockPacket::decode, MineableBlockPacket::handle);
    }

    public static void sendToAllPlayers(ItemMinerPacket packet) {
        PacketChannel.DEFAULT_CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
    }

    public static void sendToOnePlayer(ItemMinerPacket packet, ServerPlayerEntity playerTarget) {
        PacketChannel.DEFAULT_CHANNEL.send(PacketDistributor.PLAYER.with(() -> playerTarget), packet);
    }
}
