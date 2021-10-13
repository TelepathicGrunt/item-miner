package com.telepathicgrunt.item_miner.capabilities;

import com.telepathicgrunt.item_miner.ItemMiner;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AttachCapabilitiesEvent;

public class CapabilityEventHandler
{
	public static final ResourceLocation PLAYER_LEVEL_AND_PROGRESS = new ResourceLocation(ItemMiner.MODID, "player_level_and_progress");

	public static void onAttachCapabilitiesToEntities(AttachCapabilitiesEvent<Entity> e)
	{
		Entity ent = e.getObject();
		if (ent instanceof PlayerEntity)
		{
			e.addCapability(PLAYER_LEVEL_AND_PROGRESS, new PlayerLevelAndProgressProvider());
		}
	}
}