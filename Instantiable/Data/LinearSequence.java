/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable.Data;

import java.util.LinkedList;

public class LinearSequence {

	private LinkedList sequence = new LinkedList();

	private int currentIndex;

	public LinearSequence addObject(Object o) {
		sequence.addLast(o);
		return this;
	}

	public Object getEntry() {
		return sequence.get(currentIndex);
	}

	public void step() {
		currentIndex++;
	}

	public Object getNext() {
		int index = currentIndex;
		this.step();
		return sequence.get(index);
	}

}