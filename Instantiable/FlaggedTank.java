package Reika.DragonAPI.Instantiable;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

public final class FlaggedTank extends HybridTank {

	private final TankWatcher te;

	public FlaggedTank(TankWatcher tw, String name, int capacity) {
		super(name, capacity);
		te = tw;
	}

	public static interface TankWatcher {

		public void onTankChangeFluidType(String name, Fluid from, Fluid to);
	}

	@Override
	public FluidStack drain(int maxDrain, boolean doDrain)
	{
		Fluid oldf = this.getActualFluid();
		FluidStack fs = super.drain(maxDrain, doDrain);
		Fluid newf = this.getActualFluid();
		if (oldf != newf)
			te.onTankChangeFluidType(name, oldf, newf);
		return fs;
	}

	@Override
	public int fill(FluidStack resource, boolean doFill)
	{
		Fluid oldf = this.getActualFluid();
		int fill = super.fill(resource, doFill);
		Fluid newf = this.getActualFluid();
		if (oldf != newf)
			te.onTankChangeFluidType(name, oldf, newf);
		return fill;
	}

	@Override
	public void setFluid(FluidStack fluid)
	{
		Fluid oldf = this.getActualFluid();
		super.setFluid(fluid);
		Fluid newf = this.getActualFluid();
		if (oldf != newf)
			te.onTankChangeFluidType(name, oldf, newf);
	}

}
