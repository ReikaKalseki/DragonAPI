package Reika.DragonAPI.Instantiable.Event.Base;

import net.minecraft.world.IBlockAccess;
import net.minecraft.world.biome.BiomeGenBase;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public abstract class PositionEventClient extends PositionEventBase {

	public PositionEventClient(IBlockAccess world, int x, int y, int z) {
		super(world, x, y, z);
	}

	public final BiomeGenBase getBiome() {
		return isFakeWorld ? BiomeGenBase.ocean : access.getBiomeGenForCoords(xCoord, zCoord);
	}

}
