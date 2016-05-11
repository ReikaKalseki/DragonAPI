/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces.Block;

import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.IFluidHandler;

/** Implement this if the block can be treated as a liquid source block for some implementations
 * (eg an open-top vat being a valid source for a suction pump). */
public interface FluidBlockSurrogate {

	public Fluid getFluid(World world, int x, int y, int z);

	/** If the block supports non-bucket values (eg "remove 250mB of fluid") */
	public boolean supportsQuantization(World world, int x, int y, int z);

	/** Works like the same in {@link IFluidHandler}. */
	public int drain(World world, int x, int y, int z, Fluid f, int amt, boolean doDrain);

}
