/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI;

import cpw.mods.fml.common.FMLCommonHandler;

public class WTFException extends IllegalArgumentException {

	public WTFException(Object mod, String msg) {
		super("Either you or "+FMLCommonHandler.instance().findContainerFor(mod).getName()+" did something really stupid: "+msg);
		this.printStackTrace();
	}

}
