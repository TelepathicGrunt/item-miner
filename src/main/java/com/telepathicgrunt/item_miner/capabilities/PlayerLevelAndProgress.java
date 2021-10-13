package com.telepathicgrunt.item_miner.capabilities;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3d;


public class PlayerLevelAndProgress implements IPlayerLevelAndProgress
{
	private int level = 1;
	private int progress = 0;
	private long lastMinedTime = -1;

	@Override
	public void setLevel(int level) {
		this.level = level;
	}

	@Override
	public void setProgress(int progress) {
		this.progress = progress;
	}

	@Override
	public void setLastMinedTime(long lastMinedTime) {
		this.lastMinedTime = lastMinedTime;
	}

	@Override
	public int getLevel() {
		return this.level;
	}

	@Override
	public int getProgress() {
		return this.progress;
	}

	@Override
	public long getLastMinedTime() {
		return this.lastMinedTime;
	}

	@Override
	public CompoundNBT saveNBTData()
	{
		CompoundNBT nbt = new CompoundNBT();
		nbt.putInt("Level", this.level);
		nbt.putInt("Progress", this.progress);
		nbt.putLong("LastMinedTime", this.lastMinedTime);
		return nbt;
	}


	@Override
	public void loadNBTData(CompoundNBT nbtTag)
	{
		this.setLevel(nbtTag.getInt("Level"));
		this.setProgress(nbtTag.getInt("Progress"));
		this.setLastMinedTime(nbtTag.getLong("LastMinedTime"));
	}
}