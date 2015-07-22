package Reika.DragonAPI.Interfaces.Registry;

public interface ModEntry {

	public boolean isLoaded();

	public String getVersion();

	public String getModLabel();

	public String getDisplayName();

	public Class getBlockClass();
	public Class getItemClass();

}
