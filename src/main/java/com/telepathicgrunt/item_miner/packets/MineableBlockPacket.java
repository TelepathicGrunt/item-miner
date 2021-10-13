package com.telepathicgrunt.item_miner.packets;

import com.telepathicgrunt.item_miner.MiningBehavior;
import net.minecraft.block.Block;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class MineableBlockPacket implements ItemMinerPacket {

    /*
     * updates the set of blocks mineable for a client from server
     *
     * Packet to send to client and how the client will respond
     */
    private final Set<Block> itemMinables;

    /*
     * Sets the mineables for packet
     */
    public MineableBlockPacket(Set<Block> itemMinables) {
        this.itemMinables = itemMinables;
    }

    /*
     * How the client will read the packet.
     */
    public static MineableBlockPacket decode(final PacketBuffer buf) {
        int size = buf.readVarInt();
        Set<Block> mineableBlocks = new HashSet<>();

        for (int i = 0; i < size; i++) {
            mineableBlocks.add(ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation()));
        }

        return new MineableBlockPacket(mineableBlocks);
    }

    /*
     * creates the packet buffer and sets its values
     */
    public void encode(final PacketBuffer buf) {
        buf.writeVarInt(this.itemMinables.size());
        for (Block block : this.itemMinables) {
            buf.writeResourceLocation(block.getRegistryName());
        }
    }


    /*
     * What the client will do with the packet
     */
    public void handle(final Supplier<NetworkEvent.Context> ctx) {
        MiningBehavior.ITEM_MINERS_BLOCKS = itemMinables;
        ctx.get().setPacketHandled(true);
    }
}
