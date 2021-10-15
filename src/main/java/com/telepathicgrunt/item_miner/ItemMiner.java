package com.telepathicgrunt.item_miner;

import com.telepathicgrunt.item_miner.capabilities.CapabilityEventHandler;
import com.telepathicgrunt.item_miner.capabilities.CapabilityPlayerLevelAndProgress;
import com.telepathicgrunt.item_miner.client.ItemMinerClient;
import com.telepathicgrunt.item_miner.packets.PacketChannel;
import com.telepathicgrunt.item_miner.utils.ConfigHelper;
import net.minecraft.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(ItemMiner.MODID)
public class ItemMiner
{
    public static final String MODID = "item_miner";
    public static final Logger LOGGER = LogManager.getLogger();
    public static ItemMinerConfigs.ItemMinerConfigValues ITEM_MINER_CONFIGS = null;

    public ItemMiner() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;

        forgeBus.addListener(MiningBehavior::PlayerJoinEvent);
        forgeBus.addListener(MiningBehavior::BlockDestroyEvent);
        forgeBus.addListener(MiningBehavior::BlockBreakingEvent);
        forgeBus.addListener(MiningBehavior::BlockDestroyByMobEvent);
        forgeBus.addListener(MiningBehavior::BlockDestroyByExplosionEvent);
        forgeBus.addGenericListener(Entity.class, CapabilityEventHandler::onAttachCapabilitiesToEntities);
        modEventBus.addListener(this::setup);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> ItemMinerClient::subscribeClientEvents);

        ITEM_MINER_CONFIGS = ConfigHelper.register(ModConfig.Type.COMMON, ItemMinerConfigs.ItemMinerConfigValues::new, "item_miner.toml");
    }

    private void setup(final FMLCommonSetupEvent event) {
        PacketChannel.init();
        MiningBehavior.setupItemMinerSet();
        ItemCollections.populateLevelsWithItems();
        CapabilityPlayerLevelAndProgress.register();
    }
}
