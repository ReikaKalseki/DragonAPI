/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Exception;

import Reika.DragonAPI.Base.DragonAPIMod;

public class MisuseException extends DragonAPIException {

	public MisuseException(String msg) {
		message.append("DragonAPI or one of its subclasses or methods was used incorrectly!\n");
		message.append("The current error was caused by the following:\n");
		message.append(msg);
		this.crash();
	}

	public MisuseException(DragonAPIMod mod, String msg) {
		message.append("DragonAPI or one of its subclasses or methods was used incorrectly by "+mod.getDisplayName()+"!\n");
		message.append("The current error was caused by the following:\n");
		message.append(msg);
		this.crash();
	}

}
