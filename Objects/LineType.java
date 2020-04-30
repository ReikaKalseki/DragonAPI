package Reika.DragonAPI.Objects;

import org.lwjgl.opengl.GL11;

public enum LineType {

	SOLID((short)0xFFFF),
	DASHED((short)0xF0F0),
	DOTTED((short)0xAAAA);

	private final short value;

	private LineType(short s) {
		value = s;
	}

	public void setMode(int factor) {
		GL11.glEnable(GL11.GL_LINE_STIPPLE);
		GL11.glLineStipple(factor, value);
	}

}
