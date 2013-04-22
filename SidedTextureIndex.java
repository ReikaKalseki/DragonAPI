package Reika.DragonAPI;

import Reika.RotaryCraft.ClientProxy;

public interface SidedTextureIndex {
	
	public int getBlockTextureFromSideAndMetadata(int side, int metadata);
	public int getRenderType();
}
