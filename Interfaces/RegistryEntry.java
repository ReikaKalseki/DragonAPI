package Reika.DragonAPI.Interfaces;

public interface RegistryEntry {

	public String getBasicName();

	/** Whether to create it or not */
	public boolean isDummiedOut();

	/** To avoid casting to Enum */
	public int ordinal();

	/** To avoid casting to Enum */
	public String name();

	public Class getObjectClass();

	public String getUnlocalizedName();

}
