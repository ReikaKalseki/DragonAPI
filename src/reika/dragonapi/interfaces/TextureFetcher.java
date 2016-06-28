/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.interfaces;

import reika.dragonapi.interfaces.tileentity.RenderFetcher;


public interface TextureFetcher {

	public String getImageFileName(RenderFetcher te);

}
