package Reika.DragonAPI.Interfaces.Configuration;


public interface SegmentedConfigList extends ConfigList {

	public String getCustomConfigFile();

	public boolean saveIfUnspecified();

}
