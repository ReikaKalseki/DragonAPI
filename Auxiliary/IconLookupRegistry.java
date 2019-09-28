package Reika.DragonAPI.Auxiliary;

import java.lang.reflect.Method;
import java.util.HashMap;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.IconEnum;

public class IconLookupRegistry {

	public static final IconLookupRegistry instance = new IconLookupRegistry();

	private final HashMap<String, Class> enums = new HashMap();

	private IconLookupRegistry() {

	}

	public void registerIcons(DragonAPIMod mod, Class<? extends IconEnum> c) {
		if (!Enum.class.isAssignableFrom(c))
			throw new RegistrationException(mod, "Invalid icon class '"+c+"'; not an enum!");
		try {
			Method m = c.getMethod("values");
			IconEnum[] ar = (IconEnum[])m.invoke(null);
			this.registerIcons(mod, ar);
		}
		catch (Exception e) {
			throw new RegistrationException(mod, "Could not parse icon enum class!", e);
		}
	}

	public void registerIcons(DragonAPIMod mod, IconEnum[] ar) {
		for (IconEnum e : ar) {
			this.registerIcon(mod, e);
		}
	}

	public void registerIcon(DragonAPIMod mod, IconEnum e) {
		if (!(e instanceof Enum))
			throw new RegistrationException(mod, "Invalid icon object "+e+"!");
		enums.put(e.name(), e.getClass());
	}

	public IconEnum getIcon(String s) {
		Class c = enums.get(s);
		return c != null ? (IconEnum)Enum.valueOf(c, s) : null;
	}

}
