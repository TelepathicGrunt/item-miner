package com.telepathicgrunt.item_miner;

import com.telepathicgrunt.item_miner.utils.ConfigHelper;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ItemMinerConfigs {

    public static class ItemMinerConfigValues {

        public ConfigHelper.ConfigValueListener<List<String>> itemMinerBlocks;
        public ConfigHelper.ConfigValueListener<String> huntedName;

        public ItemMinerConfigValues(ForgeConfigSpec.Builder builder, ConfigHelper.Subscriber subscriber) {

            itemMinerBlocks = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------\n",
                            "A list of items that should become unbreakable and spawns items when mined.")
                    .translation("item_miner.config.itemminerblocks")
                    .define("itemMinerBlocks", Arrays.asList("minecraft:gold_block", "minecraft:diamond_block")));

            huntedName = subscriber.subscribe(builder
                    .comment("\n-----------------------------------------------------\n",
                            "The name of the hunted person whose progress is shown to everyone else.")
                    .translation("item_miner.config.huntedname")
                    .define("huntedName", "TelepathicGrunt"));
        }
    }
}
