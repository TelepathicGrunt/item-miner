package com.telepathicgrunt.item_miner.client;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;

public class ItemMinerClient {
    public static int currentLevelToDisplay = 0;
    public static int currentProgressToDisplay = 0;
    public static int currentMaxProgressToDisplay = 0;

    public static void subscribeClientEvents() {
        IEventBus forgeBus = MinecraftForge.EVENT_BUS;
        forgeBus.addListener(ProgressGUI::HUDRenderEvent);
    }
}
