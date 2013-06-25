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

import java.util.Random;

public class DragonAPICore {

	protected DragonAPICore() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}

	protected static final Random rand = new Random();

	public static final boolean hasAllClasses() {
		return true;
	}

	//TODO Add handler for custom death messages

}
