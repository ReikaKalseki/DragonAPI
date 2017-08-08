/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2017
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import java.util.List;

public class ConfigObject<V> {

	private final Object def;
	private final Types type;

	public ConfigObject(Object defaultValue) {
		type = Types.getType(defaultValue);
		def = defaultValue;
	}

	public V getValue() {
		return (V)def;
	}

	private static enum Types {
		INTEGER(int.class),
		BOOLEAN(boolean.class),
		DECIMAL(float.class),
		LIST(List.class);

		public final Class objectClass;

		private static final Types[] list = values();

		private Types(Class c) {
			objectClass = c;
		}

		public static Types getType(Object o) {
			for (int i = 0; i < list.length; i++) {
				Class c = list[i].objectClass;
				if (c.isAssignableFrom(o.getClass()))
					return list[i];
			}
			throw new IllegalArgumentException(o+" of class "+o.getClass().getCanonicalName()+" is not a valid config parameter type!");
		}
	}

}
