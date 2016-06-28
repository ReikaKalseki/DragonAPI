/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.extras;

import net.minecraft.block.material.MapColor;
import net.minecraft.block.material.Material;

public class PseudoAirMaterial extends Material {

	public static final PseudoAirMaterial instance = new PseudoAirMaterial();

	private PseudoAirMaterial() {
		super(MapColor.airColor);

		this.setReplaceable();
		this.setNoPushMobility();
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public boolean blocksMovement()
	{
		return false;
	}

	@Override
	public boolean isOpaque() {
		return false;
	}

	@Override
	public boolean getCanBlockGrass()
	{
		return false;
	}

}
