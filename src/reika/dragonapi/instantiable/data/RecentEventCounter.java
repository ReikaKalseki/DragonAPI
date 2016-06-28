/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable.data;

import java.util.LinkedList;


public class RecentEventCounter {

	private final LinkedList<RecentEvent> data = new LinkedList();

	public int addEntry(long time, long life) {
		data.add(new RecentEvent(time, life));
		while (!data.isEmpty() && data.getFirst().isElapsed(time)) {
			data.removeFirst();
		}
		return this.getSize();
	}

	public int getSize() {
		return data.size();
	}

	private static class RecentEvent {

		private final long time;
		private final long lifespan;

		private RecentEvent(long t, long l) {
			time = t;
			lifespan = l;
		}

		public boolean isElapsed(long t) {
			return t-time >= lifespan;
		}

	}

}
