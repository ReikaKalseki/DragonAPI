package Reika.DragonAPI.Interfaces.Registry;

import Reika.DragonAPI.Base.StructureBase;

public interface StructureEnum<V extends StructureBase> {

	public V getStructure();

	/** ie spawns naturally, with worldgen, as opposed to being built by the player */
	public boolean isNatural();

}
