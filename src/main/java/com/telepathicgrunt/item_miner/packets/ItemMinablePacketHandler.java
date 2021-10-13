package com.telepathicgrunt.item_miner.packets;

import com.telepathicgrunt.item_miner.ItemMiner;
import com.telepathicgrunt.item_miner.MiningBehavior;
import com.telepathicgrunt.item_miner.client.ItemMinerClient;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.nbt.NBTTypes;
import net.minecraft.nbt.StringNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ItemMinablePacketHandler {

    /*
     * updates the set of blocks mineable for a client from server
     *
     * Packet to send to client and how the client will respond
     */
    public static class UpdateMinablePacket {

        private final Set<Block> itemMinables;

        public static void sendToClient(Set<Block> itemMinables) {
            PacketChannel.DEFAULT_CHANNEL.send(PacketDistributor.ALL.noArg(), new UpdateMinablePacket(itemMinables));
        }

        /*
         * Sets the mineables for packet
         */
        public UpdateMinablePacket(Set<Block> itemMinables) {
            this.itemMinables = itemMinables;
        }

        /*
         * How the client will read the packet.
         */
        public static UpdateMinablePacket decode(final PacketBuffer buf) {
            int size = buf.readVarInt();
            Set<Block> mineableBlocks = new HashSet<>();

            for (int i = 0; i < size; i++) {
                mineableBlocks.add(ForgeRegistries.BLOCKS.getValue(buf.readResourceLocation()));
            }

            return new UpdateMinablePacket(mineableBlocks);
        }

        /*
         * creates the packet buffer and sets its values
         */
        public static void encode(final UpdateMinablePacket pkt, final PacketBuffer buf) {
            buf.writeVarInt(pkt.itemMinables.size());
            for (Block block : pkt.itemMinables) {
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

}
