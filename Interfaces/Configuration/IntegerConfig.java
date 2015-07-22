package Reika.DragonAPI.Interfaces.Configuration;

public interface IntegerConfig extends ConfigList {

	public boolean isNumeric();

	//public int setValue(Configuration config);

	public int getValue();

	public int getDefaultValue();

}
