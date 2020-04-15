package Reika.DragonAPI.Interfaces.TileEntity;

import net.minecraftforge.fluids.Fluid;

public interface NonIFluidTank {

	boolean allowAutomation();

	int addFluid(Fluid fluid, int amount, boolean doFill);

}
