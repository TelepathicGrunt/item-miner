package com.telepathicgrunt.item_miner;

import com.telepathicgrunt.item_miner.utils.ConfigHelper;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.Arrays;
import java.util.List;

public class ItemMinerConfigs {

    public static class ItemMinerConfigValues {

        public ConfigHelper.ConfigValueListener<String> huntedName;
        public ConfigHelper.ConfigValueListener<List<String>> itemMinerBlocks;
        public ConfigHelper.ConfigValueListener<List<String>> itemsAllowedPerList;
        public ConfigHelper.ConfigValueListener<List<String>> itemsDisllowedPerList;
        public ConfigHelper.ConfigValueListener<List<Integer>> itemsPerLevelUp;
        public ConfigHelper.ConfigValueListener<List<Float>> moddedItemRates;
        public ConfigHelper.ConfigValueListener<Boolean> dropOnlyOnBlockBreak;
        public ConfigHelper.ConfigValueListener<Integer> itemSpawnRate;
        public ConfigHelper.ConfigValueListener<Integer> miningSpeed;
        public ConfigHelper.ConfigValueListener<Boolean> spawnMobsFromSpawnEggs;

        public ItemMinerConfigValues(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber) {

            huntedName = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            " The name of the hunted person whose progress is shown to everyone else.")
                    .translation("item_miner.config.huntedname")
                    .define("huntedName", "Dev"));

            itemMinerBlocks = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            " A list of items that should become unable to be permanently destroyed and will spawns random items when mined.")
                    .translation("item_miner.config.itemminerblocks")
                    .define("itemMinerBlocks", Arrays.asList("minecraft:gold_block", "minecraft:diamond_block")));

            itemsPerLevelUp = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            " How many items to mine to level up. This is an array per level.",
                            " Example: if there are 3 levels, the first number of this config is number of items for first level.",
                            " Second number for second number. The last level will not read this config as last level has no progress toward another level as there is none.",
                            " If this list is shorter than number of levels or is empty, the levels not listed will default to 100 items.")
                    .translation("item_miner.config.itemsperlevelup")
                    .define("itemsPerLevelUp", Arrays.asList(100, 300)));

            itemsAllowedPerList = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            " What modids and items each level can spawn. First string is for level 1. Second string is for level 2 and so on.",
                            " Within each string, state modids or item registry names separated by a comma and those will be what spawns.",
                            " NOTE: use \"modded*\" to select all modded items to spawn.",
                            " For a modid, all of that mod's items will spawn. If the mod isn't on, an message will be logged out in the logs but game will continue on.",
                            " Default entry below uses all minecraft's items for level 1. For level 2, it uses all of useless_sword mod's items plus Netherite Sword and Diamond Sword items.")
                    .translation("item_miner.config.itemsallowedperlist")
                    .define("itemsAllowedPerList", Arrays.asList("minecraft", "minecraft, modded*", "minecraft, modded*")));

            itemsDisllowedPerList = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            " What modids and items each level CANNOT spawn.",
                            " Works the same way as itemsAllowedPerList except that this list will",
                            " prevent items from spawning even if itemsAllowedPerList allowed that item.",
                            " NOTE: You cannot use \"modded*\" to deselect all modded items as that doesn't make sense as",
                            " you can just remove \"modded*\" or modded entries from the itemsAllowedPerList instead lmao.")
                    .translation("item_miner.config.itemsdisllowedperlist")
                    .define("itemsDisllowedPerList", Arrays.asList("", "", "")));

            moddedItemRates = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            " A list of how common modded items are per level.",
                            " First entry is for level 1. Second for level 2. Etc",
                            " If a level does not have modded items, the entry here for that level is ignored.",
                            " If this list is shorter than number of levels, levels not listed here will spawn items as if they all have an equal chance.",
                            " Range is 0 to 1 with 1 being 100% chance.",
                            " Pick a negative number to make the vanilla and modded items spawn as if they are all equal chance.")
                    .translation("item_miner.config.moddeditemrates")
                    .define("moddedItemRates", Arrays.asList(-1.0f, 0.1f, 0.5f)));

            dropOnlyOnBlockBreak = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            " Drops a random item only when the block is fully mined.",
                            " Set this to false if you want item to spawn over a set time regardless if the block is fully mined or not.")
                    .translation("item_miner.config.droponlyonblockbreak")
                    .define("dropOnlyOnBlockBreak", true));

            itemSpawnRate = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            " How many ticks is the delay in item spawning if holding down the mining button on the item miner block.",
                            " NOTE: this config gets turned off if dropOnlyOnBlockBreak config is set to true.")
                    .translation("item_miner.config.itemspawnrate")
                    .define("itemSpawnRate", 7));

            miningSpeed = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            " Overrides how fast or slow mining the block is.",
                            " If dropOnlyOnBlockBreak is true, this affects the mining overlay on the block.",
                            " If set to any negative number, the block's break speed will remain unchanged from vanilla.")
                    .translation("item_miner.config.miningSpeed")
                    .define("miningspeed", -1));

            spawnMobsFromSpawnEggs = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------",
                            " Whether mobs should spawn immediately if a spawn egg item is selected.")
                    .translation("item_miner.config.spawnmobsfromspawneggs")
                    .define("spawnMobsFromSpawnEggs", true));
        }
    }
}
