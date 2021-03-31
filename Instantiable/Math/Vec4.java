package Reika.DragonAPI.Instantiable.Math;

/** This is meant for shader code, but is usable elsewhere. */
public class Vec4 {

	public float a;
	public float b;
	public float c;
	public float d;

	public Vec4() {
		this(0, 0, 0, 0);
	}

	public Vec4(double d1, double d2, double d3, double d4) {
		this((float)d1, (float)d2, (float)d3, (float)d4);
	}

	public Vec4(float d1, float d2, float d3, float d4) {
		a = d1;
		b = d2;
		c = d3;
		d = d4;
	}

}
