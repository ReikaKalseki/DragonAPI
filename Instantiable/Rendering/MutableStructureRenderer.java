package Reika.DragonAPI.Instantiable.Rendering;

import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import Reika.DragonAPI.Instantiable.Data.BlockStruct.FilledBlockArray;


public class MutableStructureRenderer extends StructureRenderer {

	private final int range;

	public MutableStructureRenderer(World world, int range) {
		super(getArray(world, range));
		this.range = range;
	}

	private static FilledBlockArray getArray(World world, int r) {
		FilledBlockArray arr = new FilledBlockArray(world);
		arr.setBlock(-r, -r, -r, Blocks.air);
		arr.setBlock(r, r, r, Blocks.air);
		return arr;
	}

	public void addBlock(int x, int y, int z, Block b, int meta, TileEntity te) {
		//ReikaJavaLibrary.pConsole(access.negativeCorner);
		//ReikaJavaLibrary.pConsole(Arrays.deepToString(access.data));
		//array.setBlock(x, y, z, b, meta);
		//access.data[x+range][y+range][z+range] = new PositionData(b, meta, te);
		//ReikaJavaLibrary.pConsole("Adding "+access.data[x+range][y+range][z+range]+" to REL ("+x+","+y+","+z+")");
		//ReikaJavaLibrary.pConsole("Adding "+access.data[dx][dy][dz]+" to "+(x+access.negativeCorner.xCoord)+","+(y+access.negativeCorner.yCoord)+","+(z+access.negativeCorner.zCoord)+" ("+x+","+y+","+z+")");

		int dx = x-access.negativeCorner.xCoord+access.offset.xCoord;
		int dy = y-access.negativeCorner.yCoord+access.offset.yCoord;
		int dz = z-access.negativeCorner.zCoord+access.offset.zCoord;
		//ReikaJavaLibrary.pConsole(access.negativeCorner+"/"+access.offset);
		access.data[dx][dy][dz] = new PositionData(b, meta, te);
		//ReikaJavaLibrary.pConsole("Adding "+access.data[dx][dy][dz]+" to rel ("+x+","+y+","+z+") (index "+dx+", "+dy+","+dz+")");
	}

}
