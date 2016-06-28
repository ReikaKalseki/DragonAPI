/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.exception;

public class MisuseException extends DragonAPIException {

	public MisuseException(String msg) {
		message.append("DragonAPI or one of its subclasses or methods was used incorrectly!\n");
		message.append("The current error was caused by the following:\n");
		message.append(msg);
		this.crash();
	}

}
