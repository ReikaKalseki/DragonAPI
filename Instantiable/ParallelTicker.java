/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;

public class ParallelTicker {

	private final HashMap <String, Integer> tickers = new HashMap<String, Integer>();
	private final HashMap <String, Integer> caps = new HashMap<String, Integer>();

	public void updateAll() {
		for (String key : tickers.keySet()) {
			this.updateTicker(key);
		}
	}

	public int getTickOf(String key) {
		if (!tickers.containsKey(key))
			return -1;
		return tickers.get(key);
	}

	public int getCapOf(String key) {
		if (!caps.containsKey(key))
			return -1;
		return caps.get(key);
	}

	public void updateTicker(String key) {
		if (tickers.containsKey(key)) {
			tickers.put(key, tickers.get(key)+1);
		}
	}

	public ParallelTicker addTicker(String key) {
		tickers.put(key, 0);
		return this;
	}

	public ParallelTicker addTicker(String key, int cap) {
		this.addTicker(key);
		this.setCap(key, cap);
		return this;
	}

	public void setCap(String key, int cap) {
		if (tickers.containsKey(key)) {
			caps.put(key, cap);
		}
	}

	private boolean isAtCap(String key) {
		if (tickers.containsKey(key) && caps.containsKey(key)) {
			return tickers.get(key) >= caps.get(key);
		}
		return false;
	}

	/*
	/** For easier integration with if() return;. Does not reset. *//*
	public boolean isNotAtCap(String key) {
		return !this.isAtCap(key);
	}*/

	public boolean checkCap(String key) {
		boolean cap = this.isAtCap(key);
		if (cap)
			this.resetTicker(key);
		return cap;
	}

	public void resetTicker(String key) {
		this.setTickOf(key, 0);
	}

	public void setTickOf(String key, int val) {
		if (tickers.containsKey(key)) {
			tickers.put(key, val);
		}
	}

	public float getPortionOfCap(String key) {
		if (tickers.containsKey(key) && caps.containsKey(key)) {
			if (caps.get(key) == 0)
				return -1F;
			else
				return (float)tickers.get(key)/(float)caps.get(key);
		}
		return 0F;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (String key : tickers.keySet()) {
			String s = String.format("Timer %s: %d/%d (%.2f%s)", key, this.getTickOf(key), this.getCapOf(key), this.getPortionOfCap(key)*100F, "%");
			sb.append(s+"\n");
		}
		return sb.toString();
	}

	public void writeToNBT(NBTTagCompound NBT, String id) {
		for (String ticker : tickers.keySet()) {
			String s = id+ticker;
			NBT.setInteger(s+"cap", this.getCapOf(ticker));
			NBT.setInteger(s+"tick", this.getTickOf(ticker));
		}
	}

	public void readFromNBT(NBTTagCompound NBT, String id) {
		for (String ticker : tickers.keySet()) {
			String s = id+ticker;
			this.setCap(ticker, NBT.getInteger(s+"cap"));
			this.setTickOf(ticker, NBT.getInteger(s+"tick"));
		}
	}

}
