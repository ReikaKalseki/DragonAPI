/*******************************************************************************
 * @author Reika
 * 
 * This code is the property of and owned and copyrighted by Reika.
 * Unless given explicit written permission - electronic writing is acceptable - no user may
 * copy, edit, or redistribute this source code nor any derivative works.
 * Failure to comply with these restrictions is a violation of
 * copyright law and will be dealt with accordingly.
 ******************************************************************************/
package Reika.DragonAPI.Libraries;

import net.minecraft.block.Block;
import Reika.DragonAPI.DragonAPICore;

public final class ReikaBlockHelper extends DragonAPICore {

	public static boolean alwaysDropsSelf(int ID) {
		int k = 0;
		//for (k = 0; k <= 20; k++)
			for (int i = 0; i < 16; i++)
				if (ID != Block.blocksList[ID].idDropped(i, rand, k) && ID-256 != Block.blocksList[ID].idDropped(i, rand, k))
					return false;/*
		for (int i = 0; i < 16; i++)
			if (Block.blocksList[ID].damageDropped(i) != i)
				return false;*/
		return true;
	}

	public static boolean neverDropsSelf(int ID) {
		boolean hasID = false;
		boolean hasMeta = false;
		for (int k = 0; k <= 20 && !hasID; k++)
			for (int i = 0; i < 16 && !hasID; i++)
				if (ID == Block.blocksList[ID].idDropped(i, rand, k) || ID-256 == Block.blocksList[ID].idDropped(i, rand, k))
					hasID = true;/*
		for (int i = 0; i < 16 && !hasMeta; i++)
			if (Block.blocksList[ID].damageDropped(i) == i)*/
				hasMeta = true;
		return (hasID && hasMeta);
	}

}
