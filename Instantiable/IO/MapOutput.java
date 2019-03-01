package Reika.DragonAPI.Instantiable.IO;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public abstract class MapOutput<V> {

	public final String worldName;
	private final int dimensionID;

	private final int originX;
	private final int originZ;
	private final int range;

	private final int resolution;
	private final int gridSize;
	private final boolean fullGrid;

	public final long startTime;

	private final int[][] data;

	protected MapOutput(String name, int dim, int x, int z, int r, int res, int grid, boolean fgrid) {
		startTime = System.currentTimeMillis();

		worldName = name;
		dimensionID = dim;

		originX = x;
		originZ = z;
		range = r;
		resolution = res;
		gridSize = grid;
		fullGrid = fgrid;

		data = new int[range*2/resolution+1][range*2/resolution+1];
	}

	public void addGrid() {
		if (gridSize > 0) {
			for (int dx = originX-range; dx <= originX+range; dx += resolution) {
				for (int dz = originZ-range; dz <= originZ+range; dz += resolution) {
					int i = (range+(dx-originX))/resolution;
					int k = (range+(dz-originZ))/resolution;
					int i2 = dx-originX;
					int k2 = dz-originZ;
					boolean flag1 = i2%gridSize == 0;
					boolean flag2 = k2%gridSize == 0;
					if ((flag1 || flag2) && ((flag1 && flag2) || fullGrid)) {
						data[i][k] = ReikaColorAPI.mixColors(data[i][k], i2 == 0 && k2 == 0 ? 0xffff0000 : 0xffffffff, 0.25F);
						if (i-1 >= 0)
							data[i-1][k] = ReikaColorAPI.mixColors(data[i-1][k], 0xff000000, 0.5F);
						if (i+1 < data.length)
							data[i+1][k] = ReikaColorAPI.mixColors(data[i+1][k], 0xff000000, 0.5F);
						if (k-1 >= 0)
							data[i][k-1] = ReikaColorAPI.mixColors(data[i][k-1], 0xff000000, 0.5F);
						if (k+1 < data[i].length)
							data[i][k+1] = ReikaColorAPI.mixColors(data[i][k+1], 0xff000000, 0.5F);
					}
				}
			}
		}
	}

	@SideOnly(Side.CLIENT)
	public final void addPoint(int x, int z, V data) {
		int c = 0xff000000 | this.getColor(x, z, data);
		int i = (range+(x-originX))/resolution;
		int k = (range+(z-originZ))/resolution;
		this.data[i][k] = c;
	}

	protected abstract int getColor(int x, int z, V data);

	@SideOnly(Side.CLIENT)
	public String createImage() throws IOException {
		String name = this.getFilename();
		File f = new File(DragonAPICore.getMinecraftDirectory(), name);
		if (f.exists())
			f.delete();
		f.getParentFile().mkdirs();
		f.createNewFile();
		BufferedImage img = new BufferedImage(data.length, data.length, BufferedImage.TYPE_INT_ARGB);
		for (int i = 0; i < data.length; i++) {
			for (int k = 0; k < data[i].length; k++) {
				img.setRGB(i, k, data[i][k]);
			}
		}
		ImageIO.write(img, "png", f);

		this.onImageCreate(f);

		return f.getAbsolutePath();
	}

	protected void onImageCreate(File f) throws IOException {

	}

	private String getFilename() {
		String sr = String.valueOf(range*2+1);
		String ret = this.getClass().getSimpleName()+"/"+worldName+"/DIM"+dimensionID+"/"+originX+", "+originZ+" ("+sr+"x"+sr+"; [R="+resolution+" b-px, G="+gridSize+"-"+fullGrid+"]).png";
		if (worldName.contains("SEED=")) {
			ret = this.getClass().getSimpleName()+"/Forced/"+worldName+"; "+originX+", "+originZ+" ("+sr+"x"+sr+"; [R="+resolution+" b-px, G="+gridSize+"-"+fullGrid+"]).png";
		}
		return ret;
	}

}
