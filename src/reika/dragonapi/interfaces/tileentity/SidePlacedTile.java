/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces.tileentity;

public interface SidePlacedTile {

	public void placeOnSide(int s);

	public boolean checkLocationValidity();

	public void drop();

}
