/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2014
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Auxiliary;

import net.minecraft.launchwrapper.IClassTransformer;

public class DragonAPIClassTransfomer implements IClassTransformer {

	@Override
	public byte[] transform(String arg0, String arg1, byte[] arg2) {
		return arg2;
	}

}
