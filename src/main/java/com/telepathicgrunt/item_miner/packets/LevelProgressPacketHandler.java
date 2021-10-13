package com.telepathicgrunt.item_miner.packets;

import com.telepathicgrunt.item_miner.ItemMiner;
import com.telepathicgrunt.item_miner.client.ItemMinerClient;
import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class LevelProgressPacketHandler {


    /*
     * updates the level/progress for all clients from server
     *
     * Packet to send to client and how the client will respond
     */
    public static class UpdateLevelProgressPacket
    {
        private int level;
        private int progess;
        private int maxProgress;

        public static void sendToClient(int level, int progress, int maxProgress) {
            PacketChannel.DEFAULT_CHANNEL.send(PacketDistributor.ALL.noArg(), new UpdateLevelProgressPacket(level, progress, maxProgress));
        }

        /*
         * Sets the level and progress for packet
         */
        public UpdateLevelProgressPacket(int level, int progress, int maxProgress) {
            this.level = level;
            this.progess = progress;
            this.maxProgress = maxProgress;
        }

        /*
         * How the client will read the packet.
         */
        public static UpdateLevelProgressPacket parse(final PacketBuffer buf) {
            return new UpdateLevelProgressPacket(buf.readInt(), buf.readInt(), buf.readInt());
        }

        /*
         * creates the packet buffer and sets its values
         */
        public static void compose(final UpdateLevelProgressPacket pkt, final PacketBuffer buf)
        {
            buf.writeInt(pkt.level);
            buf.writeInt(pkt.progess);
            buf.writeInt(pkt.maxProgress);
        }


        /*
         * What the client will do with the packet
         */
        public static class Handler
        {
            //this is what gets run on the client
            public static void handle(final UpdateLevelProgressPacket pkt, final Supplier<NetworkEvent.Context> ctx)
            {
                Minecraft.getInstance().execute(() -> {
                    ItemMinerClient.currentLevelToDisplay = pkt.level;
                    ItemMinerClient.currentProgressToDisplay = pkt.progess;
                    ItemMinerClient.currentMaxProgressToDisplay = pkt.maxProgress;
                    ItemMiner.LOGGER.info("Packet recieved: {}, {}, {}", pkt.level, pkt.progess, pkt.maxProgress);
                });
                ctx.get().setPacketHandled(true);
            }
        }
    }
}
