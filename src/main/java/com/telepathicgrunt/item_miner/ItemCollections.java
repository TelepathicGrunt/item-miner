package com.telepathicgrunt.item_miner;

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
    public static Map<Integer, List<Item>> LEVEL_TO_ITEMS = new HashMap<>();

    public static void populateLevelsWithItems() {
        for(int level = 1; level <= ItemMiner.ITEM_MINER_CONFIGS.itemsPerList.get().size(); level++) {
            List<String> parsedLevel = Arrays.asList(ItemMiner.ITEM_MINER_CONFIGS.itemsPerList.get().get(level - 1).split(","));
            parsedLevel.replaceAll(String::trim);
            LEVEL_TO_ITEMS.put(level, new ArrayList<>());

            for(String configEntry : parsedLevel) {
                // empty entry like a trailing comma accidentally left in. Skip
                if(configEntry.isEmpty()) continue;

                // item resourcelocation format. Parse it and retrieve that item to add to this level
                if(configEntry.contains(":")) {
                    ResourceLocation itemRL = new ResourceLocation(configEntry);
                    if(!ForgeRegistries.ITEMS.containsKey(itemRL)) {
                        ItemMiner.LOGGER.warn("Item Miner: Unable to resolve the following item {} for level {} in itemsPerList config", configEntry, level);
                        continue;
                    }
                    LEVEL_TO_ITEMS.get(level).add(ForgeRegistries.ITEMS.getValue(itemRL));
                }
                // Assumed to be a modid entry
                else {
                    List<Item> modidItems = ForgeRegistries.ITEMS.getEntries().stream()
                            .filter(entry -> entry.getKey().location().getNamespace().equals(configEntry))
                            .map(Map.Entry::getValue).collect(Collectors.toList());

                    if(modidItems.isEmpty()) {
                        ItemMiner.LOGGER.warn("Item Miner: Unable to resolve the following modid {} for level {} in itemsPerList config", configEntry, level);
                        continue;
                    }
                    LEVEL_TO_ITEMS.get(level).addAll(modidItems);
                }
            }

            MAX_LEVEL = level;
        }
    }
}
