package com.telepathicgrunt.item_miner;

import com.telepathicgrunt.item_miner.capabilities.IPlayerLevelAndProgress;
import com.telepathicgrunt.item_miner.capabilities.PlayerLevelAndProgress;
import com.telepathicgrunt.item_miner.mixin.SpawnEggItemAccessor;
import com.telepathicgrunt.item_miner.packets.LevelProgressPacketHandler;
import com.telepathicgrunt.item_miner.packets.MineableBlockPacket;
import com.telepathicgrunt.item_miner.packets.PacketChannel;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SpawnEggItem;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.event.entity.living.LivingDestroyBlockEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.world.BlockEvent;
import net.minecraftforge.event.world.ExplosionEvent;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class MiningBehavior {

    @CapabilityInject(IPlayerLevelAndProgress.class)
    public static Capability<IPlayerLevelAndProgress> PLAYER_LEVEL_AND_PROGRESS = null;
    public static Set<Block> ITEM_MINERS_BLOCKS_HUNTED = new HashSet<>();
    public static Set<Block> ITEM_MINERS_BLOCKS_HUNTER = new HashSet<>();
    public static Boolean IS_HUNTED = false; // Client side use in case it is needed. Unused right now

    public static void setupItemMinerSet() {
        ITEM_MINERS_BLOCKS_HUNTED = ItemMiner.ITEM_MINER_CONFIGS.itemMinerBlocksHunted.get().stream()
                .map(stringRl -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stringRl)))
                .collect(Collectors.toSet());

        ITEM_MINERS_BLOCKS_HUNTER = ItemMiner.ITEM_MINER_CONFIGS.itemMinerBlocksHunter.get().stream()
                .map(stringRl -> ForgeRegistries.BLOCKS.getValue(new ResourceLocation(stringRl)))
                .collect(Collectors.toSet());
    }

    // Make sure all clients that joins gets the set of mineable blocks for visual and proper behavior.
    // Also updates client to know what the current hunted's level and progress is
    public static void PlayerJoinEvent(PlayerEvent.PlayerLoggedInEvent event) {
        if(!event.getPlayer().level.isClientSide()) {
            boolean isHunted = event.getPlayer().getName().getString().equals(ItemMiner.ITEM_MINER_CONFIGS.huntedName.get());
            PacketChannel.sendToOnePlayer(new MineableBlockPacket(ITEM_MINERS_BLOCKS_HUNTER, ITEM_MINERS_BLOCKS_HUNTED, isHunted), (ServerPlayerEntity) event.getPlayer());
            ServerPlayerEntity serverPlayerEntity = event.getPlayer().level.getServer().getPlayerList().getPlayerByName(ItemMiner.ITEM_MINER_CONFIGS.huntedName.get());
            if(serverPlayerEntity != null) {
                PlayerLevelAndProgress cap = (PlayerLevelAndProgress) serverPlayerEntity.getCapability(PLAYER_LEVEL_AND_PROGRESS).orElseThrow(RuntimeException::new);
                PacketChannel.sendToOnePlayer(new LevelProgressPacketHandler(cap.getLevel(), cap.getProgress(), getItemsForMaxProgress(cap.getLevel())), (ServerPlayerEntity) event.getPlayer());
            }
        }
    }


    // Prevent block from being broken by anything if player is not in creative.
    public static void BlockDestroyEvent(BlockEvent.BreakEvent event) {
        if(event.getPlayer() != null &&
            !event.getPlayer().isCreative() &&
            (ITEM_MINERS_BLOCKS_HUNTER.contains(event.getState().getBlock()) || ITEM_MINERS_BLOCKS_HUNTED.contains(event.getState().getBlock())))
        {
            event.setCanceled(true);

            if(ItemMiner.ITEM_MINER_CONFIGS.dropOnlyOnBlockBreak.get()) {
                attemptItemSpawning(event.getPlayer().level, event.getPlayer(), event.getPos(), event.getState().getBlock());
            }
        }
    }

    // Prevents wither, enderdragon, zombies, and other mobs from breaking block
    public static void BlockDestroyByMobEvent(LivingDestroyBlockEvent event) {
        if((ITEM_MINERS_BLOCKS_HUNTER.contains(event.getState().getBlock()) || ITEM_MINERS_BLOCKS_HUNTED.contains(event.getState().getBlock()))) {
            event.setCanceled(true);
        }
    }

    // Prevents explosions from destroying the block
    public static void BlockDestroyByExplosionEvent(ExplosionEvent.Detonate event) {
        event.getAffectedBlocks().removeIf((blockPos) -> {
            Block block = event.getWorld().getBlockState(blockPos).getBlock();
            return ITEM_MINERS_BLOCKS_HUNTER.contains(block) || ITEM_MINERS_BLOCKS_HUNTED.contains(block);
        });
    }

    // handles preventing the block from having a mining overlay and spawns item instead.
    public static void BlockBreakingEvent(PlayerEvent.BreakSpeed event) {
        if(event.getPlayer() != null &&
            !event.getPlayer().isCreative() &&
            (ITEM_MINERS_BLOCKS_HUNTER.contains(event.getState().getBlock()) || ITEM_MINERS_BLOCKS_HUNTED.contains(event.getState().getBlock())))
        {
            if(ItemMiner.ITEM_MINER_CONFIGS.miningSpeed.get() >= 0) {
                event.setNewSpeed(ItemMiner.ITEM_MINER_CONFIGS.miningSpeed.get());
            }

            if(!ItemMiner.ITEM_MINER_CONFIGS.dropOnlyOnBlockBreak.get()) {
                attemptItemSpawning(event.getPlayer().level, event.getPlayer(), event.getPos(), event.getState().getBlock());
            }
        }
    }

    private static void attemptItemSpawning(World world, PlayerEntity player, BlockPos pos, Block blockBeingMined) {
        if (!world.isClientSide) {
            PlayerLevelAndProgress cap = (PlayerLevelAndProgress) player.getCapability(PLAYER_LEVEL_AND_PROGRESS).orElseThrow(RuntimeException::new);

            // Delays the spawning of time when continuously mining.
            // -1 is to make sure we can always mine at start
            long lastMinedTime = cap.getLastMinedTime();
            long currentMinedTime = world.getGameTime();
            if(!ItemMiner.ITEM_MINER_CONFIGS.dropOnlyOnBlockBreak.get()) {
                if(lastMinedTime != -1 && currentMinedTime - lastMinedTime < ItemMiner.ITEM_MINER_CONFIGS.itemSpawnRate.get().longValue()) {
                    return;
                }
            }
            cap.setLastMinedTime(currentMinedTime);

            // Gets the level to use for item spawning.
            // If it is the hunted, uses the hunted's level for hunted's items.
            // Otherwise, using level 1 items for everyone else.
            int level = 1;
            boolean isCurrentlyHuntedPlayer = player.getName().getContents().equals(ItemMiner.ITEM_MINER_CONFIGS.huntedName.get());
            if((ITEM_MINERS_BLOCKS_HUNTED.contains(blockBeingMined) && isCurrentlyHuntedPlayer) ||
                (ITEM_MINERS_BLOCKS_HUNTER.contains(blockBeingMined) && !isCurrentlyHuntedPlayer))
            {
                if (isCurrentlyHuntedPlayer) {
                    level = Math.min(cap.getLevel(), ItemCollections.MAX_LEVEL);
                }

                // grabs the collection of items and picks a random one
                int progress = cap.getProgress();
                List<Item> collection = getItemCollectionToUse(level, progress, world.random);
                if(!collection.isEmpty()) {
                    ItemStack newItemStack = collection.get(world.random.nextInt(collection.size())).getDefaultInstance();

                    if(ItemMiner.ITEM_MINER_CONFIGS.spawnMobsFromSpawnEggs.get() && newItemStack.getItem() instanceof SpawnEggItem) {
                        SpawnEggItem spawnEgg = (SpawnEggItem) newItemStack.getItem();
                        EntityType<?> entityType = ((SpawnEggItemAccessor)spawnEgg).getDefaultType();
                        entityType.spawn((ServerWorld)world, newItemStack, player, pos.above(), SpawnReason.SPAWN_EGG, false, false);
                    }
                    else {
                        // spawns the new item in world above block
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
                    }
                }

                // Update progress on entity (applies to all players in case we want to switch to per-player progress)
                int itemsForMaxProgress = getItemsForMaxProgress(level);
                if(progress + 1 >= itemsForMaxProgress) {
                    // Makes it never go above the final level from itemsPerList config.
                    if(level < ItemCollections.MAX_LEVEL) {
                        cap.setProgress(0);
                        cap.setLevel(level + 1);
                    }
                    else {
                        cap.setProgress(progress + 1);
                    }
                }
                else {
                    cap.setProgress(progress + 1);
                }

                // Send the new level and progress to client to display visually is it is the hunted that is mining.
                if(isCurrentlyHuntedPlayer) {
                    PacketChannel.sendToAllPlayers(new LevelProgressPacketHandler(cap.getLevel(), cap.getProgress(), getItemsForMaxProgress(cap.getLevel())));
                }
            }
        }
    }

    private static List<Item> getItemCollectionToUse(int level, int currentProgress, Random random) {
        List<Item> vanillaList = ItemCollections.LEVEL_TO_ITEMS.get(level).getFirst();
        List<Item> moddedList = ItemCollections.LEVEL_TO_ITEMS.get(level).getSecond();
        List<? extends Integer> moddedItemRates = ItemMiner.ITEM_MINER_CONFIGS.moddedItemRates.get();
        int modulo = 2;

        if(level <= moddedItemRates.size() && moddedItemRates.get(level - 1) >= 0) {
            modulo = moddedItemRates.get(level - 1) + 1;
        }
        // spawn either vanilla or modded at equal chance in proportion to their list size
        if(!moddedList.isEmpty() && currentProgress % modulo == 0) {
            return moddedList;
        }
        else {
            return vanillaList;
        }
    }

    private static int getItemsForMaxProgress(int level) {
        if(level == ItemCollections.MAX_LEVEL) return -1;

        List<? extends Integer> itemsForLevelingUp = ItemMiner.ITEM_MINER_CONFIGS.itemsPerLevelUp.get();
        int maxProgressAmount;

        if(level <= itemsForLevelingUp.size()) {
            maxProgressAmount = itemsForLevelingUp.get(level - 1);
        }
        else {
            if(itemsForLevelingUp.isEmpty()) maxProgressAmount = 100;
            else maxProgressAmount = itemsForLevelingUp.get(itemsForLevelingUp.size() - 1);
        }
        return maxProgressAmount;
    }
}
