/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Exception;

import Reika.DragonAPI.Interfaces.DragonAPIMod;

public class WTFException extends IllegalArgumentException {

	public WTFException(DragonAPIMod mod, String msg) {
		super("Either you or "+mod.getDisplayName()+" did something really stupid: "+msg);
		this.printStackTrace();
	}

}
