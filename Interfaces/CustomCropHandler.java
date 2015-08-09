/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

import Reika.DragonAPI.Interfaces.Registry.CropHandler;
import Reika.DragonAPI.Interfaces.Registry.ModEntry;
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
