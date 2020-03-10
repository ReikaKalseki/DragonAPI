package Reika.DragonAPI.Auxiliary.Trackers;


public class SettingInterferenceTracker {

	public static final SettingInterferenceTracker instance = new SettingInterferenceTracker();

	private SettingInterferenceTracker() {

	}

	public static abstract interface SettingInterference {

		public boolean isCurrentlyRelevant();

		public boolean isSetToInterfere();

	}

}
