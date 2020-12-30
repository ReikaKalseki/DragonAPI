package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.block.Block;
import net.minecraft.block.BlockGrass;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Event.Base.PositionEventClient;


public class GrassIconEvent extends PositionEventClient {

	public final boolean isTop;
	public final Block blockType;
	public final IIcon originalIcon;
	public IIcon icon;

	public GrassIconEvent(IBlockAccess world, int x, int y, int z, IIcon ico, Block b, boolean top) {
		super(world, x, y, z);

		blockType = b;
		originalIcon = ico;
		icon = originalIcon;
		isTop = top;
	}

	public static IIcon fire(IIcon orig, Block b, IBlockAccess iba, int x, int y, int z) {
		//if (b == Blocks.grass) {
		return doFire(orig, b, iba, x, y, z, true);
		/*
	}
	else {
		return orig;
	}*/
	}

	private static IIcon doFire(IIcon orig, Block b, IBlockAccess iba, int x, int y, int z, boolean top) {
		GrassIconEvent evt = new GrassIconEvent(iba, x, y, z, orig, b, top);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.icon;

	}

	public static IIcon fireSide(Block b, IBlockAccess iba, int x, int y, int z) {
		return doFire(BlockGrass.getIconSideOverlay(), b, iba, x, y, z, false);
	}

}
