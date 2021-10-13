package com.telepathicgrunt.item_miner;

import net.minecraft.item.Item;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class ItemCollections {
    public static Map<Integer, Set<Item>> LEVEL_TO_ITEMS = new HashMap<>();

    public static void populateLevelsWithItems() {

        LEVEL_TO_ITEMS.put(1, ForgeRegistries.ITEMS.getEntries().stream()
                .filter(entry -> entry.getKey().location().getNamespace().equals("minecraft"))
                .map(Map.Entry::getValue).collect(Collectors.toSet()));

        LEVEL_TO_ITEMS.put(2, ForgeRegistries.ITEMS.getEntries().stream()
                .filter(entry -> entry.getKey().location().getNamespace().equals("useless_sword"))
                .map(Map.Entry::getValue).collect(Collectors.toSet()));
    }
}
