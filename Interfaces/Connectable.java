/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;

public interface Connectable {

	public boolean isEmitting();

	public void reset();

	public void resetOther();

	public boolean setSource(int x, int y, int z);

	public boolean setTarget(int x, int y, int z);

}
