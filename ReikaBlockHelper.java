package Reika.DragonAPI;

import java.util.Random;

import net.minecraft.block.Block;

public final class ReikaBlockHelper {
	
	static Random par5Random = new Random();
	
	private ReikaBlockHelper() {throw new RuntimeException("The class "+this.getClass()+" cannot be instantiated!");}
	
	public static boolean alwaysDropsSelf(int ID) {
		int k = 0;
		//for (k = 0; k <= 20; k++)
			for (int i = 0; i < 16; i++)
				if (ID != Block.blocksList[ID].idDropped(i, par5Random, k) && ID-256 != Block.blocksList[ID].idDropped(i, par5Random, k))
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
				if (ID == Block.blocksList[ID].idDropped(i, par5Random, k) || ID-256 == Block.blocksList[ID].idDropped(i, par5Random, k))
					hasID = true;/*
		for (int i = 0; i < 16 && !hasMeta; i++)
			if (Block.blocksList[ID].damageDropped(i) == i)*/
				hasMeta = true;
		return (hasID && hasMeta);
	}
	
}
