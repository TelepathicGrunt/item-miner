package com.telepathicgrunt.item_miner;

import com.telepathicgrunt.item_miner.utils.ConfigHelper;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ItemMinerConfigs {

    public static class ItemMinerConfigValues {

        public ConfigHelper.ConfigValueListener<List<String>> itemMinerBlocks;
        public ConfigHelper.ConfigValueListener<String> huntedName;
        public ConfigHelper.ConfigValueListener<Integer> miningSpeed;
        public ConfigHelper.ConfigValueListener<Integer> itemsToLevelUp;
        public ConfigHelper.ConfigValueListener<List<String>> itemsPerList;

        public ItemMinerConfigValues(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber) {

            itemMinerBlocks = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            "A list of items that should become unbreakable and spawns items when mined.")
                    .translation("item_miner.config.itemminerblocks")
                    .define("itemMinerBlocks", Arrays.asList("minecraft:gold_block", "minecraft:diamond_block")));

            huntedName = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            "The name of the hunted person whose progress is shown to everyone else.")
                    .translation("item_miner.config.huntedname")
                    .define("huntedName", "Dev"));

            miningSpeed = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            "How many ticks is the delay in item spawning if holding down the mining button on the item miner block.")
                    .translation("item_miner.config.miningSpeed")
                    .define("miningspeed", 7));

            itemsToLevelUp = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            "How many items to mine to level up once.")
                    .translation("item_miner.config.itemstolevelup")
                    .define("itemsToLevelUp", 100));

            itemsPerList = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            "What modids and items each level can spawn. First string is for level 1. Second string is for level 2 and so on.",
                            "Within each string, state modids or item registry names separated by a comma and those will be what spawns.",
                            "For a modid, all of that mod's items will spawn. If the mod isn't on, an message will be logged out in the logs but game will continue on.",
                            "Default entry below uses all minecraft's items for level 1. For level 2, it uses all of useless_sword mod's items plus Netherite Sword and Diamond Sword items.")
                    .translation("item_miner.config.itemsperlist")
                    .define("itemsPerList", Arrays.asList("minecraft", "useless_sword, minecraft:netherite_sword, minecraft:diamond_sword")));
        }
    }
}
