/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Interfaces;


public interface IDRegistry {

	public String getConfigName();

	public int getDefaultID();

	public boolean isBlock();

	public boolean isItem();

	public String getCategory();

}
