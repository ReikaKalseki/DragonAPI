package Reika.DragonAPI.Instantiable.Data.Collections;

import java.util.HashMap;
import java.util.Iterator;


public class LastCallTimer<V> {

	private final HashMap<V, Timer> times = new HashMap();

	private final int loadFactor;

	public LastCallTimer(int factor) {
		this.loadFactor = factor;
	}

	public void add(V obj, long time, long dur) {
		times.put(obj, new Timer(time, dur));
		if (times.size() > loadFactor) {
			Iterator<Timer> it = times.values().iterator();
			while (it.hasNext()) {
				Timer t = it.next();
				if (t.time+t.duration < time)
					it.remove();
			}
		}
	}

	public boolean contains(V obj, long time) {
		Timer t = this.times.get(obj);
		return t != null && t.time+t.duration >= time;
	}

	private static class Timer {

		private final long time;
		private final long duration;

		private Timer(long t, long d) {
			this.time = t;
			this.duration = d;
		}

	}

}
