package com.telepathicgrunt.item_miner;

import com.telepathicgrunt.item_miner.utils.ConfigHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod("item_miner")
public class ItemMiner
{
    private static final Logger LOGGER = LogManager.getLogger();
    public static ItemMinerConfigs.ItemMinerConfigValues ITEM_MINER_CONFIGS = null;

    public ItemMiner() {
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        MinecraftForge.EVENT_BUS.register(this);
        ITEM_MINER_CONFIGS = ConfigHelper.register(ModConfig.Type.COMMON, ItemMinerConfigs.ItemMinerConfigValues::new, "item_miner.toml");
    }

    private void setup(final FMLCommonSetupEvent event) {
        ItemCollections.populateLevelsWithItems();
    }
}
