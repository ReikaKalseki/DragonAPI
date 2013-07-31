package Reika.DragonAPI.ModInteract;

public class ReikaTwilightHelper {

	public static boolean isTwilightForestBoss(String name) {
		if (name == null)
			return false;
		if (name.equals("Ur-Ghast"))
			return true;
		if (name.equals("Hydra"))
			return true;
		if (name.equals("Naga"))
			return true;
		return false;
	}

}
