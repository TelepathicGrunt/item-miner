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
    private final Set<Block> itemMinablesHunter;
    private final Set<Block> itemMinablesHunted;
    private final boolean isHunted;

    /*
     * Sets the mineables for packet
     */
    public MineableBlockPacket(Set<Block> itemMinablesHunter, Set<Block> itemMinablesHunted, boolean isHunted) {
        this.itemMinablesHunter = itemMinablesHunter;
        this.itemMinablesHunted = itemMinablesHunted;
        this.isHunted = isHunted;
    }

    /*
     * How the client will read the packet.
     */
    public static MineableBlockPacket decode(final PacketBuffer buf) {
        int size1 = buf.readVarInt();
        Set<Block> mineableBlocks1 = new HashSet<>();
        for (int i = 0; i < size1; i++) {
            mineableBlocks1.add(ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation()));
        }

        int size2 = buf.readVarInt();
        Set<Block> mineableBlocks2 = new HashSet<>();
        for (int i = 0; i < size2; i++) {
            mineableBlocks2.add(ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation()));
        }

        boolean isHunted = buf.readBoolean();

        return new MineableBlockPacket(mineableBlocks1, mineableBlocks2, isHunted);
    }

    /*
     * creates the packet buffer and sets its values
     */
    public void encode(final PacketBuffer buf) {
        buf.writeVarInt(this.itemMinablesHunter.size());
        for (Block block : this.itemMinablesHunter) {
            buf.writeResourceLocation(block.getRegistryName());
        }

        buf.writeVarInt(this.itemMinablesHunted.size());
        for (Block block : this.itemMinablesHunted) {
            buf.writeResourceLocation(block.getRegistryName());
        }

        buf.writeBoolean(this.isHunted);
    }


    /*
     * What the client will do with the packet
     */
    public void handle(final Supplier<NetworkEvent.Context> ctx) {
        MiningBehavior.ITEM_MINERS_BLOCKS_HUNTER = itemMinablesHunter;
        MiningBehavior.ITEM_MINERS_BLOCKS_HUNTED = itemMinablesHunted;
        MiningBehavior.IS_HUNTED = isHunted;
        ctx.get().setPacketHandled(true);
    }
}
