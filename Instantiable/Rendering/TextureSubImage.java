package Reika.DragonAPI.Instantiable.Rendering;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.IIcon;

public class TextureSubImage {

	public final double minU;
	public final double maxU;
	public final double minV;
	public final double maxV;

	public TextureSubImage(IIcon ico) {
		this(ico.getMinU(), ico.getMaxU(), ico.getMinV(), ico.getMaxV());
	}

	public TextureSubImage(double u, double du, double v, double dv) {
		minU = u;
		minV = v;
		maxU = du;
		maxV = dv;
	}

	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = new NBTTagCompound();
		tag.setDouble("u", minU);
		tag.setDouble("v", minV);
		tag.setDouble("du", maxU);
		tag.setDouble("dv", maxV);
		return tag;
	}

	public static TextureSubImage readFromNBT(NBTTagCompound tag) {
		return new TextureSubImage(tag.getDouble("u"), tag.getDouble("du"), tag.getDouble("v"), tag.getDouble("dv"));
	}

}
