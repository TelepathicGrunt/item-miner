package com.telepathicgrunt.item_miner.packets;

import com.telepathicgrunt.item_miner.ItemMiner;
import com.telepathicgrunt.item_miner.client.ItemMinerClient;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class LevelProgressPacketHandler {


    /*
     * updates the level/progress for all clients from server
     *
     * Packet to send to client and how the client will respond
     */
    public static class UpdateLevelProgressPacket
    {
        private final int level;
        private final int progess;
        private final int maxProgress;

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
        public static UpdateLevelProgressPacket decode(final PacketBuffer buf) {
            return new UpdateLevelProgressPacket(buf.readInt(), buf.readInt(), buf.readInt());
        }

        /*
         * creates the packet buffer and sets its values
         */
        public static void encode(final UpdateLevelProgressPacket pkt, final PacketBuffer buf)
        {
            buf.writeInt(pkt.level);
            buf.writeInt(pkt.progess);
            buf.writeInt(pkt.maxProgress);
        }

        /*
         * What the client will do with the packet
         */
        public void handle(final Supplier<NetworkEvent.Context> ctx)
        {
            ItemMinerClient.currentLevelToDisplay = level;
            ItemMinerClient.currentProgressToDisplay = progess;
            ItemMinerClient.currentMaxProgressToDisplay = maxProgress;
            ItemMiner.LOGGER.info("Packet recieved: {}, {}, {}", level, progess, maxProgress);
            ctx.get().setPacketHandled(true);
        }
    }
}
