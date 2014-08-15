/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Libraries.Java;

import org.lwjgl.opengl.GL11;

public class ReikaGLHelper {

	public static enum BlendMode {
		DEFAULT(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA),
		ALPHA(GL11.GL_ONE, GL11.GL_SRC_ALPHA),
		PREALPHA(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_ALPHA),
		MULTIPLY(GL11.GL_DST_COLOR, GL11.GL_ONE_MINUS_SRC_ALPHA),
		ADDITIVE(GL11.GL_ONE, GL11.GL_ONE),
		ADDITIVEDARK(GL11.GL_ONE, GL11.GL_ONE_MINUS_SRC_COLOR),
		OVERLAYDARK(GL11.GL_SRC_COLOR, GL11.GL_ONE),
		ADDITIVE2(GL11.GL_SRC_ALPHA, GL11.GL_ONE);

		public final int sfactor;
		public final int dfactor;

		private BlendMode(int s, int d) {
			sfactor = s;
			dfactor = d;
		}

		public void apply() {
			GL11.glBlendFunc(sfactor, dfactor);
		}
	}

}