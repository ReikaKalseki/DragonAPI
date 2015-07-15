package Reika.DragonAPI.Interfaces;

import Reika.DragonAPI.ModRegistry.ModCropList;

public interface CustomCropHandler extends CropHandler {

	/** Use a {@link ModList} entry if it already exists. Else, just create your own ModEntry object. */
	public ModEntry getMod();

	/** Used for displays like the GPR. */
	public int getColor();

	/** Be careful not to conflict with anything in {@link ModCropList}! */
	public String getEnumEntryName();

	/** Whether the crop is a TileEntity and that data affects harvesting. */
	public boolean isTileEntity();

}
