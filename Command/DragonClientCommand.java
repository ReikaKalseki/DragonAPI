/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Command;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

//@SideOnly(Side.CLIENT)
public abstract class DragonClientCommand extends DragonCommandBase {

	@Override
	protected final boolean isAdminOnly() {
		return false;
	}

}
