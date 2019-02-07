package Reika.DragonAPI.Libraries.Java;

import java.lang.reflect.Modifier;


public enum ClassModifiers {

	PUBLIC(),
	PRIVATE(),
	PROTECTED(),
	STATIC(),
	FINAL(),
	SYNCHRONIZED(),
	VOLATILE(),
	TRANSIENT(),
	NATIVE(),
	INTERFACE(),
	ABSTRACT(),
	STRICT();

	private final int bits;

	private ClassModifiers() {
		try {
			int val = Modifier.class.getDeclaredField(this.name()).getInt(null);
			bits = val;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public boolean match(int modifiers) {
		return (modifiers & bits) != 0;
	}

	@Override
	public String toString() {
		return this.name();
	}
}
