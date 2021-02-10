package Reika.DragonAPI.Interfaces.Registry;

import java.util.Collection;

import Reika.DragonAPI.Instantiable.IO.SoundVariant;

public interface VariableSound extends SoundEnum {

	public Collection<SoundVariant> getVariants();

	public SoundVariant getVariant(String name);

}
