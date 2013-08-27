/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import org.lwjgl.input.Keyboard;

public class ReikaKeyHelper {

	public static int getForwardKey() {
		return 0;
	}

	public static boolean isKeyPressed(int key) {
		return Keyboard.isKeyDown(key);
	}

}
