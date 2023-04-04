package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Event.Base.PositionEventClient;

/** The IBA may be null for this one, indicating that it is a call to getIcon(s, meta) */
public class BlockIconEvent extends PositionEventClient {

	public final Block blockType;
	public final int side;
	public final int meta;
	public final IIcon originalIcon;
	public IIcon icon;

	public BlockIconEvent(IBlockAccess world, int x, int y, int z, IIcon ico, Block b, int m, int s) {
		super(world, x, y, z);

		blockType = b;
		originalIcon = ico;
		icon = originalIcon;
		side = s;
		meta = m;
	}

	public static IIcon fire(Block b, IBlockAccess iba, int x, int y, int z, IIcon def, int s, int m) {
		BlockIconEvent evt = new BlockIconEvent(iba, x, y, z, def, b, m, s);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.icon;
	}

	public static IIcon fire(Block b, IBlockAccess iba, int x, int y, int z, int s) {
		int m = iba.getBlockMetadata(x, y, z);
		return fire(b, iba, x, y, z, b.getIcon(s, m), s, m);
	}

	public static IIcon fire(Block b, int s, int meta) {
		return fire(b, null, 0, 0, 0, b.blockIcon, s, meta);
	}

}
