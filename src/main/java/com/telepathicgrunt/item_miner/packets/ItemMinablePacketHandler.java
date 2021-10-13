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

import java.util.HashSet;
import java.util.Set;
import java.util.function.Supplier;

public class ItemMinablePacketHandler {

    /*
     * updates the set of blocks mineable for a client from server
     *
     * Packet to send to client and how the client will respond
     */
    public static class UpdateMinablePacket
    {
        private Set<Block> itemMinables;

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
        public static UpdateMinablePacket parse(final PacketBuffer buf) {
            CompoundNBT nbt = buf.readNbt();
            assert nbt != null;
            ListNBT listNBT = nbt.getList("mineables", 8);
            int size = Math.min(listNBT.size(), 1024);
            Set<Block> mineableBlocks = new HashSet<>();
            for(int index = 0; index < size; ++index) {
                mineableBlocks.add(ForgeRegistries.BLOCKS.getValue(new ResourceLocation(listNBT.getString(index))));
            }
            return new UpdateMinablePacket(mineableBlocks);
        }

        /*
         * creates the packet buffer and sets its values
         */
        public static void compose(final UpdateMinablePacket pkt, final PacketBuffer buf)
        {
            ListNBT listNBT = new ListNBT();
            for(Block block : pkt.itemMinables) {
                listNBT.add(StringNBT.valueOf(block.getRegistryName().toString()));
            }
            CompoundNBT nbt = new CompoundNBT();
            nbt.put("mineables", listNBT);

            buf.writeNbt(nbt);
        }


        /*
         * What the client will do with the packet
         */
        public static class Handler
        {
            //this is what gets run on the client
            public static void handle(final UpdateMinablePacket pkt, final Supplier<NetworkEvent.Context> ctx)
            {
                Minecraft.getInstance().execute(() -> {
                    MiningBehavior.ITEM_MINERS_BLOCKS = pkt.itemMinables;
                });
                ctx.get().setPacketHandled(true);
            }
        }
    }

}
