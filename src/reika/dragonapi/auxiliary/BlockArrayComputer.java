/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.auxiliary;

import reika.dragonapi.instantiable.data.blockstruct.BlockArray;

public class BlockArrayComputer implements Runnable {

	private final BlockArray blocks;

	private Operation op;

	public BlockArrayComputer(BlockArray arr) {
		blocks = arr;
	}

	/** This is called manually! */
	@Override
	public void run() {
		if (op == null)
			return;
		switch(op) {
		case FILL:
			break;
		case ITERATE:
			break;
		case LOAD:
			break;
		default:
			break;
		}
	}

	public BlockArrayComputer setOperation(Operation set) {
		op = set;
		return this;
	}

	public enum Operation {
		LOAD(),
		FILL(),
		ITERATE();
	}

}
