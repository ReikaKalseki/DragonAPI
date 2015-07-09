/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2015
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Exception;


public abstract class DragonAPIException extends RuntimeException {

	protected StringBuilder message = new StringBuilder();

	protected DragonAPIException() {
		super();
	}

	protected DragonAPIException(Throwable t) {
		super(t);
	}

	@Override
	public final String getMessage() {
		return message.toString();
	}

	protected void crash() {
		//Minecraft.getMinecraft().crashed(CrashReport.makeCrashReport(this, this.getMessage()));
		//this.printStackTrace();
		throw this;
	}

}
