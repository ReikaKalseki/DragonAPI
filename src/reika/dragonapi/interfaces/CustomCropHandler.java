/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces;

import reika.dragonapi.interfaces.registry.CropHandler;
import reika.dragonapi.interfaces.registry.ModEntry;
import reika.dragonapi.mod.registry.ModCropList;

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
