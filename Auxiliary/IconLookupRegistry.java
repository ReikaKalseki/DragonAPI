package Reika.DragonAPI.Auxiliary;

import java.util.HashMap;

import net.minecraft.util.IIcon;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

import Reika.DragonAPI.Base.DragonAPIMod;
import Reika.DragonAPI.Exception.RegistrationException;
import Reika.DragonAPI.Interfaces.IconEnum;

public class IconLookupRegistry {

	public static final IconLookupRegistry instance = new IconLookupRegistry();

	private final HashMap<String, Class> enums = new HashMap();

	private IconLookupRegistry() {

	}

	public <T extends Enum & IconEnum> void registerIcons(DragonAPIMod mod, Class<T> c) {
		this.registerIcons(mod, c.getEnumConstants());
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
		if (s.startsWith("forgefluid_")) {
			Fluid f = FluidRegistry.getFluid(s.substring("forgefluid_".length()));
			return f != null ? new FluidDelegate(f) : null;
		}
		Class c = enums.get(s);
		return c != null ? (IconEnum)Enum.valueOf(c, s) : null;
	}

	public static class FluidDelegate implements IconEnum {

		public final Fluid fluid;

		public FluidDelegate(Fluid f) {
			fluid = f;
		}

		@Override
		public String name() {
			return "forgefluid_"+fluid.getName();
		}

		@Override
		public IIcon getIcon() {
			return fluid.getStillIcon();
		}

	}

}
