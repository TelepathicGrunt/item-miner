package com.telepathicgrunt.item_miner.capabilities;

import net.minecraft.nbt.CompoundNBT;

public interface IPlayerLevelAndProgress {

	//what methods the capability will have and what the capability is
	
	void setLevel(int level);
	void setProgress(int progress);
	void setLastMinedTime(long lastMinedTime);

	int getLevel();
	int getProgress();
	long getLastMinedTime();

	CompoundNBT saveNBTData();
	void loadNBTData(CompoundNBT nbtTag);
}
