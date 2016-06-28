/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable;

import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class MetadataFluid extends Fluid {

	private final int metas;

	public MetadataFluid(String fluidName, int nmeta) {
		super(fluidName);
		metas = nmeta;
	}

	public int getNumberMetadatas() {
		return metas;
	}

	/** Use this instead of manual registration! */
	public final void register() {
		for (int i = 0; i < metas; i++) {
			Fluid f = new Fluid(this.getTag(i));
			FluidRegistry.registerFluid(f);
		}
	}

	private final String getTag(int i) {
		return String.format("%s:%d", fluidName, i);
	}

	public final Fluid getFluid(int metadata) {
		return FluidRegistry.getFluid(this.getTag(metadata));
	}

	@Override
	public final boolean equals(Object o) {
		return ((Fluid)o).getName().contains(fluidName) && o instanceof MetadataFluid;
	}
}
