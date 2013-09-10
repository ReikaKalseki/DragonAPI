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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ParallelTicker {

	private final HashMap <String, Integer> tickers = new HashMap<String, Integer>();
	private final HashMap <String, Integer> caps = new HashMap<String, Integer>();
	private final List<String> keyList = new ArrayList<String>();

	public void updateAll() {
		for (int i = 0; i < keyList.size(); i++) {
			String key = keyList.get(i);
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

	public ParallelTicker addTicker() {
		String key = String.format("Ticker%d", keyList.size());
		tickers.put(key, 0);
		keyList.add(key);
		return this;
	}

	public ParallelTicker addTicker(String key) {
		tickers.put(key, 0);
		keyList.add(key);
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

}
