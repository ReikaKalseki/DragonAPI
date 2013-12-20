/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Base;

public abstract class CropHandlerBase extends ModHandlerBase {

	public abstract int getRipeMeta();

	public abstract int getFreshMeta();

	public abstract boolean isCrop(int id);

	public abstract boolean isRipeCrop(int id, int meta);

}
