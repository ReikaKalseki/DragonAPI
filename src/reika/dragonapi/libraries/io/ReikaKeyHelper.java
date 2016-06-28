/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.libraries.io;

import org.lwjgl.input.Keyboard;

//We are assuming default bindings for now
public class ReikaKeyHelper {

	public static int getForwardKey() {
		//return Minecraft.getMinecraft().gameSettings.keyBindForward.keyCode;
		return Keyboard.KEY_W;
	}

	public static int getJumpKey() {
		//return Minecraft.getMinecraft().gameSettings.keyBindJump.keyCode;
		return Keyboard.KEY_SPACE;
	}

	public static int getSneakKey() {
		//return Minecraft.getMinecraft().gameSettings.keyBindJump.keyCode;
		return Keyboard.KEY_LSHIFT;
	}

	public static boolean isKeyPressed(int key) {
		return Keyboard.isKeyDown(key);
	}

}
