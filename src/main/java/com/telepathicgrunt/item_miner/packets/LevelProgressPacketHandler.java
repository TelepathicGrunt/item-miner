package com.telepathicgrunt.item_miner.packets;

import com.telepathicgrunt.item_miner.ItemMiner;
import com.telepathicgrunt.item_miner.client.ItemMinerClient;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.PacketDistributor;

import java.util.function.Supplier;

public class LevelProgressPacketHandler implements ItemMinerPacket{
    /*
     * updates the level/progress for all clients from server
     *
     * Packet to send to client and how the client will respond
     */

    private final int level;
    private final int progess;
    private final int maxProgress;
    
    /*
     * Sets the level and progress for packet
     */
    public LevelProgressPacketHandler(int level, int progress, int maxProgress) {
        this.level = level;
        this.progess = progress;
        this.maxProgress = maxProgress;
    }

    /*
     * How the client will read the packet.
     */
    public static LevelProgressPacketHandler decode(final PacketBuffer buf) {
        return new LevelProgressPacketHandler(buf.readInt(), buf.readInt(), buf.readInt());
    }

    /*
     * creates the packet buffer and sets its values
     */
    public void encode(final PacketBuffer buf) {
        buf.writeInt(this.level);
        buf.writeInt(this.progess);
        buf.writeInt(this.maxProgress);
    }

    /*
     * What the client will do with the packet
     */
    public void handle(final Supplier<NetworkEvent.Context> ctx) {
        ItemMinerClient.currentLevelToDisplay = level;
        ItemMinerClient.currentProgressToDisplay = progess;
        ItemMinerClient.currentMaxProgressToDisplay = maxProgress;
        ItemMiner.LOGGER.info("Packet recieved: {}, {}, {}", level, progess, maxProgress);
        ctx.get().setPacketHandled(true);
    }
}
