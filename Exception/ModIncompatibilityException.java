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

public class ModIncompatibilityException extends RuntimeException {

	public ModIncompatibilityException(DragonAPIMod mod, String otherModName, String msg) {
		super(mod.getDisplayName()+" has compatibility issues with the following mod: "+otherModName+"\nReason: "+msg);
		this.printStackTrace();
	}

}
