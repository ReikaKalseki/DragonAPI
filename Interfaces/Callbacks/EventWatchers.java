package Reika.DragonAPI.Interfaces.Callbacks;

import java.util.Comparator;

public class EventWatchers {

	public static final Comparator<EventWatcher> comparator = new Comparator<EventWatcher>() {

		@Override
		public int compare(EventWatcher o1, EventWatcher o2) {
			return Integer.compare(o1.watcherSortIndex(), o2.watcherSortIndex());
		}

	};

	public static interface EventWatcher {

		public int watcherSortIndex();

	}

}
