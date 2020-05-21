package Reika.DragonAPI.Instantiable.Rendering;

import net.minecraft.nbt.NBTTagCompound;

import Reika.DragonAPI.DragonAPICore;
import Reika.DragonAPI.Libraries.IO.ReikaTextureHelper;

public class TextureSlot extends TextureSubImage {

	private final Class reference;
	private final String texture;

	public TextureSlot(Class c, String tex, double u, double du, double v, double dv) {
		super(u, du, v, dv);

		reference = c;
		texture = tex;
	}

	public void bindTexture() {
		ReikaTextureHelper.bindTexture(reference, texture);
	}

	public static TextureSlot fromSpritesheet(Class c, String tex, int x, int y, int gridLength) {
		double u = x/(double)gridLength;
		double v = y/(double)gridLength;
		double du = u+1D/gridLength;
		double dv = v+1D/gridLength;
		return new TextureSlot(c, tex, u, du, v, dv);
	}

	@Override
	public NBTTagCompound writeToNBT() {
		NBTTagCompound tag = super.writeToNBT();
		tag.setString("class", reference.getName());
		tag.setString("tex", texture);
		return tag;
	}

	public static TextureSlot readFromNBT(NBTTagCompound tag) {
		TextureSubImage base = TextureSubImage.readFromNBT(tag);
		return new TextureSlot(getClass(tag.getString("class")), tag.getString("tex"), base.minU, base.maxU, base.minV, base.maxV);
	}

	private static Class getClass(String string) {
		try {
			return Class.forName(string);
		}
		catch (Exception e) {
			e.printStackTrace();
			return DragonAPICore.class;
		}
	}

}
