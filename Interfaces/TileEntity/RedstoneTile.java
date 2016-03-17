package Reika.DragonAPI.Interfaces.TileEntity;

import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.util.ForgeDirection;


public interface RedstoneTile {

	int getStrongPower(IBlockAccess world, int x, int y, int z, ForgeDirection side);
	int getWeakPower(IBlockAccess world, int x, int y, int z, ForgeDirection side);

}
