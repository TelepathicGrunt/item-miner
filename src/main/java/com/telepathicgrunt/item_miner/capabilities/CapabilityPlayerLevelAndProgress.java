package com.telepathicgrunt.item_miner.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;

import javax.annotation.Nullable;

public class CapabilityPlayerLevelAndProgress {
		//registers the capability and defines how it will read/write data from nbt
		public static void register() {
			CapabilityManager.INSTANCE.register(IPlayerLevelAndProgress.class, new Capability.IStorage<IPlayerLevelAndProgress>()
			{
				@Override
				@Nullable
				public INBT writeNBT(Capability<IPlayerLevelAndProgress> capability, IPlayerLevelAndProgress instance, Direction side)
				{
					return instance.saveNBTData();
				}

				@Override
				public void readNBT(Capability<IPlayerLevelAndProgress> capability, IPlayerLevelAndProgress instance, Direction side, INBT nbt)
				{
					instance.loadNBTData((CompoundNBT) nbt);
				}
			}, PlayerLevelAndProgress::new);
		}
}
