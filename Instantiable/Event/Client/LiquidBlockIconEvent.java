package Reika.DragonAPI.Instantiable.Event.Client;

import net.minecraft.block.Block;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.MinecraftForge;

import Reika.DragonAPI.Instantiable.Event.Base.PositionEventClient;


public class LiquidBlockIconEvent extends PositionEventClient {

	public final Block blockType;
	public final int side;
	public final IIcon originalIcon;
	public IIcon icon;

	public LiquidBlockIconEvent(IBlockAccess world, int x, int y, int z, IIcon ico, Block b, int s) {
		super(world, x, y, z);

		blockType = b;
		originalIcon = ico;
		icon = originalIcon;
		side = s;
	}

	public static IIcon fire(Block b, IBlockAccess iba, int x, int y, int z, int s) {
		LiquidBlockIconEvent evt = new LiquidBlockIconEvent(iba, x, y, z, b.getIcon(s, iba.getBlockMetadata(x, y, z)), b, s);
		MinecraftForge.EVENT_BUS.post(evt);
		return evt.icon;
	}

}
