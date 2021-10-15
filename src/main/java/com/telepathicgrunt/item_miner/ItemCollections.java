package com.telepathicgrunt.item_miner;

import com.mojang.datafixers.util.Pair;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ItemCollections {
    public static int MAX_LEVEL = 0;

    // First list in level is vanilla items. Second is modded items. Used for mixing rates of vanilla to modded item spawnrates
    public static Map<Integer, Pair<List<Item>, List<Item>>> LEVEL_TO_ITEMS = new HashMap<>();

    public static void populateLevelsWithItems() {
        for(int level = 1; level <= ItemMiner.ITEM_MINER_CONFIGS.itemsAllowedPerList.get().size(); level++) {
            List<String> parsedLevel = Arrays.asList(ItemMiner.ITEM_MINER_CONFIGS.itemsAllowedPerList.get().get(level - 1).split(","));
            parsedLevel.replaceAll(String::trim);
            LEVEL_TO_ITEMS.put(level, Pair.of(new ArrayList<>(), new ArrayList<>()));

            for(String configEntry : parsedLevel) {
                // empty entry like a trailing comma accidentally left in. Skip
                if(configEntry.isEmpty()) continue;

                // item resourcelocation format. Parse it and retrieve that item to add to this level
                if(configEntry.contains(":")) {
                    ResourceLocation itemRL = new ResourceLocation(configEntry);
                    if(!ForgeRegistries.ITEMS.containsKey(itemRL)) {
                        ItemMiner.LOGGER.warn("Item Miner: Unable to resolve the following item {} for level {} in itemsAllowedPerList config", configEntry, level);
                        continue;
                    }

                    if(itemRL.getNamespace().equals("minecraft")) {
                        LEVEL_TO_ITEMS.get(level).getFirst().add(ForgeRegistries.ITEMS.getValue(itemRL));
                    }
                    else {
                        LEVEL_TO_ITEMS.get(level).getSecond().add(ForgeRegistries.ITEMS.getValue(itemRL));
                    }
                }
                // grabs all modded items
                else if(configEntry.equals("modded*")) {
                    List<Item> modidItems = ForgeRegistries.ITEMS.getEntries().stream()
                            .filter(entry -> !entry.getKey().location().getNamespace().equals("minecraft"))
                            .map(Map.Entry::getValue).collect(Collectors.toList());
                    LEVEL_TO_ITEMS.get(level).getSecond().addAll(modidItems);
                }
                // Assumed to be a modid entry
                else {
                    List<Item> modidItems = ForgeRegistries.ITEMS.getEntries().stream()
                            .filter(entry -> entry.getKey().location().getNamespace().equals(configEntry))
                            .map(Map.Entry::getValue).collect(Collectors.toList());

                    if(modidItems.isEmpty()) {
                        ItemMiner.LOGGER.warn("Item Miner: Unable to resolve the following modid {} for level {} in itemsAllowedPerList config", configEntry, level);
                        continue;
                    }

                    if(configEntry.equals("minecraft")) {
                        LEVEL_TO_ITEMS.get(level).getFirst().addAll(modidItems);
                    }
                    else {
                        LEVEL_TO_ITEMS.get(level).getSecond().addAll(modidItems);
                    }
                }
            }

            MAX_LEVEL = level;
        }


        for(int level = 1; level <= Math.min(ItemMiner.ITEM_MINER_CONFIGS.itemsDisllowedPerList.get().size(), LEVEL_TO_ITEMS.size()); level++) {
            List<String> parsedLevel = Arrays.asList(ItemMiner.ITEM_MINER_CONFIGS.itemsDisllowedPerList.get().get(level - 1).split(","));
            parsedLevel.replaceAll(String::trim);

            for(String configEntry : parsedLevel) {
                // empty entry like a trailing comma accidentally left in. Skip
                if(configEntry.isEmpty()) continue;

                // item resourcelocation format. Parse it and retrieve that item to remove from this level
                if(configEntry.contains(":")) {
                    ResourceLocation itemRL = new ResourceLocation(configEntry);
                    if(!ForgeRegistries.ITEMS.containsKey(itemRL)) {
                        ItemMiner.LOGGER.warn("Item Miner: Unable to resolve the following item {} for level {} in itemsDisllowedPerList config", configEntry, level);
                        continue;
                    }

                    if(itemRL.getNamespace().equals("minecraft")) {
                        LEVEL_TO_ITEMS.get(level).getFirst().remove(ForgeRegistries.ITEMS.getValue(itemRL));
                    }
                    else {
                        LEVEL_TO_ITEMS.get(level).getSecond().remove(ForgeRegistries.ITEMS.getValue(itemRL));
                    }
                }
                // Assumed to be a modid entry
                else {
                    List<Item> modidItems = ForgeRegistries.ITEMS.getEntries().stream()
                            .filter(entry -> entry.getKey().location().getNamespace().equals(configEntry))
                            .map(Map.Entry::getValue).collect(Collectors.toList());

                    if(modidItems.isEmpty()) {
                        ItemMiner.LOGGER.warn("Item Miner: Unable to resolve the following modid {} for level {} in itemsDisllowedPerList config", configEntry, level);
                        continue;
                    }

                    if(configEntry.equals("minecraft")) {
                        LEVEL_TO_ITEMS.get(level).getFirst().removeAll(modidItems);
                    }
                    else {
                        LEVEL_TO_ITEMS.get(level).getSecond().removeAll(modidItems);
                    }
                }
            }
        }
    }
}
