package Reika.DragonAPI.Interfaces;

public interface EntityEnum extends RegistryEntry {

	public int getTrackingDistance();

	public boolean sendsVelocityUpdates();

	public boolean hasSpawnEgg();

	public int eggColor1();
	public int eggColor2();

}
