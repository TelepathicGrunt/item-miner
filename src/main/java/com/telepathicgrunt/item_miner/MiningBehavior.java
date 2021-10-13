package com.telepathicgrunt.item_miner;

import com.telepathicgrunt.item_miner.capabilities.IPlayerLevelAndProgress;
import com.telepathicgrunt.item_miner.capabilities.PlayerLevelAndProgress;
import com.telepathicgrunt.item_miner.packets.LevelProgressPacketHandler;
import com.telepathicgrunt.item_miner.packets.MineableBlockPacket;
import com.telepathicgrunt.item_miner.packets.PacketChannel;
import net.minecraft.block.Block;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MiningBehavior {

    @CapabilityInject(IPlayerLevelAndProgress.class)
    public static Capability<IPlayerLevelAndProgress> PLAYER_LEVEL_AND_PROGRESS = null;
    public static Set<Block> ITEM_MINERS_BLOCKS = new HashSet<>();

    public static void setupItemMinerSet() {
        ITEM_MINERS_BLOCKS = ItemMiner.ITEM_MINER_CONFIGS.itemMinerBlocks.get().stream()
                .map(stringRl -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stringRl)))
                .collect(Collectors.toSet());
    }

    // Make sure all clients that joins gets the set of mineable blocks for visual and proper behavior.
    // Also updates client to know what the current hunted's level and progress is
    public static void PlayerJoinEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if(!event.getPlayer().level.isClientSide()) {
            PacketChannel.sendToOnePlayer(new MineableBlockPacket(ITEM_MINERS_BLOCKS), (ServerPlayerEntity) event.getPlayer());
            ServerPlayerEntity serverPlayerEntity = event.getPlayer().level.getServer().getPlayerList().getPlayerByName(ItemMiner.ITEM_MINER_CONFIGS.huntedName.get());
            if(serverPlayerEntity != null) {
                PlayerLevelAndProgress cap = (PlayerLevelAndProgress) serverPlayerEntity.getCapability(PLAYER_LEVEL_AND_PROGRESS).orElseThrow(RuntimeException::new);
                PacketChannel.sendToOnePlayer(new LevelProgressPacketHandler(cap.getLevel(), cap.getProgress(), ItemMiner.ITEM_MINER_CONFIGS.itemsToLevelUp.get()), (ServerPlayerEntity) event.getPlayer());
            }
        }
    }


    // Prevent block from being broken by anything if player is not in creative.
    public static void BlockDestroyEvent(BlockEvent.BreakEvent event) {
        if(event.getPlayer() != null &&
            !event.getPlayer().isCreative() &&
            ITEM_MINERS_BLOCKS.contains(event.getState().getBlock()))
        {
            event.setCanceled(true);
        }
    }

    // Prevents wither, enderdragon, zombies, and other mobs from breaking block
    public static void BlockDestroyByMobEvent(LivingDestroyBlockEvent event) {
        if(ITEM_MINERS_BLOCKS.contains(event.getState().getBlock())) {
            event.setCanceled(true);
        }
    }

    // Prevents explosions from destroying the block
    public static void BlockDestroyByExplosionEvent(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().removeIf((blockPos) ->
                ITEM_MINERS_BLOCKS.contains(event.getWorld().getBlockState(blockPos).getBlock()));
    }

    // handles preventing the block from having a mining overlay and spawns item instead.
    public static void BlockBreakingEvent(PlayerEvent.BreakSpeed event) {
        if(event.getPlayer() != null &&
            !event.getPlayer().isCreative() &&
            ITEM_MINERS_BLOCKS.contains(event.getState().getBlock()))
        {
            // prevents mining overlay on block
            event.setNewSpeed(0);

            World world = event.getPlayer().level;
            if (!world.isClientSide) {
                PlayerLevelAndProgress cap = (PlayerLevelAndProgress) event.getPlayer().getCapability(PLAYER_LEVEL_AND_PROGRESS).orElseThrow(RuntimeException::new);

                // Delays the spawning of time when continuously mining.
                // -1 is to make sure we can always mine at start
                long lastMinedTime = cap.getLastMinedTime();
                long currentMinedTime = world.getGameTime();
                if(lastMinedTime != -1 && currentMinedTime - lastMinedTime < ItemMiner.ITEM_MINER_CONFIGS.miningSpeed.get().longValue()) {
                    return;
                }
                cap.setLastMinedTime(currentMinedTime);

                // Gets the level to use for item spawning.
                // If it is the hunted, uses the hunted's level for hunted's items.
                // Otherwise, using level 1 items for everyone else.
                int level = 1;
                boolean isCurrentlyHuntedPlayer = event.getPlayer().getName().getContents().equals(ItemMiner.ITEM_MINER_CONFIGS.huntedName.get());
                if (isCurrentlyHuntedPlayer) {
                    level = Math.min(cap.getLevel(), ItemCollections.MAX_LEVEL);
                }

                // grabs the collection of items and picks a random one
                List<Item> collection = ItemCollections.LEVEL_TO_ITEMS.get(level);
                ItemStack newItemStack = collection.get(world.random.nextInt(collection.size())).getDefaultInstance();

                // spawns the new item in world above block
                BlockPos pos = event.getPos();
                double xOffset = (double)(world.random.nextFloat() * 0.5F) + 0.25D;
                double yOffset = (double)(world.random.nextFloat() * 0.5F) + 0.75D;
                double zOffset = (double)(world.random.nextFloat() * 0.5F) + 0.25D;
                ItemEntity itementity = new ItemEntity(
                        world,
                        (double)pos.getX() + xOffset,
                        (double)pos.getY() + yOffset,
                        (double)pos.getZ() + zOffset,
                        newItemStack);
                itementity.setDefaultPickUpDelay();
                world.addFreshEntity(itementity);

                // Update progress on entity (applies to all players in case we want to switch to per-player progress)
                int progress = cap.getProgress();
                if(progress + 1 >= ItemMiner.ITEM_MINER_CONFIGS.itemsToLevelUp.get()) {
                    cap.setProgress(0);
                    cap.setLevel(level + 1);
                }
                else {
                    cap.setProgress(progress + 1);
                }

                // Send the new level and progress to client to display visually is it is the hunted that is mining.
                if(isCurrentlyHuntedPlayer) {
                    PacketChannel.sendToAllPlayers(new LevelProgressPacketHandler(cap.getLevel(), cap.getProgress(), ItemMiner.ITEM_MINER_CONFIGS.itemsToLevelUp.get()));
                }
            }
        }
    }
}
