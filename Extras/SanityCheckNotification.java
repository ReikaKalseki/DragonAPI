package Reika.DragonAPI.Extras;

import Reika.DragonAPI.Auxiliary.Trackers.CrashNotifications.CrashNotification;


public class SanityCheckNotification implements CrashNotification {

	@Override
	public String getLabel() {
		return "Sanity Checker Notification";
	}

	@Override
	public String addMessage(Throwable crash) {
		return "The DragonAPI environment sanity checker is disabled. Note that the mod showing up in the stacktrace may not be the cause, especially if the crash involves general-use registries like recipes or biomes.";
	}

}
