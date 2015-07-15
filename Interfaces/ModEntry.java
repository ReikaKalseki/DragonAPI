package Reika.DragonAPI.Interfaces;

public interface ModEntry {

	public boolean isLoaded();

	public String getVersion();

	public String getModLabel();

	public String getDisplayName();

	public Class getBlockClass();
	public Class getItemClass();

}
