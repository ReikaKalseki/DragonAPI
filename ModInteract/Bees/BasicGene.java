/*******************************************************************************
 * @author Reika Kalseki
 *
 * Copyright 2017
 *
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.ModInteract.Bees;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IChromosomeType;

public abstract class BasicGene extends GeneBase {

	public BasicGene(String uid, String name, IChromosomeType type) {
		super(uid, name, type);
		this.preInit();
		AlleleManager.alleleRegistry.registerAllele(this, type);
	}

	protected void preInit() {

	}

	@Override
	public boolean isDominant() {
		return true;
	}

}
