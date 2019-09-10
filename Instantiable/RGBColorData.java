package Reika.DragonAPI.Instantiable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

import net.minecraft.nbt.NBTTagCompound;

import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;

import io.netty.buffer.ByteBuf;

public class RGBColorData {

	public boolean red;
	public boolean green;
	public boolean blue;

	public RGBColorData(boolean red, boolean green, boolean blue) {
		this.red = red;
		this.green = green;
		this.blue = blue;
	}

	public void intersect(RGBColorData dat) {
		red = red && dat.red;
		green = green && dat.green;
		blue = blue && dat.blue;
	}

	public void add(RGBColorData dat) {
		red = red || dat.red;
		green = green || dat.green;
		blue = blue || dat.blue;
	}

	public void invert() {
		red = !red;
		green = !green;
		blue = !blue;
	}

	public boolean isBlack() {
		return !red && !green && !blue;
	}

	public boolean isWhite() {
		return red && green && blue;
	}

	public boolean isPrimary() {
		int c = 0;
		if (red)
			c++;
		if (green)
			c++;
		if (blue)
			c++;
		return c == 1;
	}

	public boolean matchColor(RGBColorData o) {
		return o.red == red && o.green == green && o.blue == blue;
	}

	public int getRenderColor() {
		return this.isBlack() ? 0x101010 : ReikaColorAPI.RGBtoHex(red ? 255 : 0, green ? 255 : 0, blue ? 255 : 0);
	}

	public void readFromNBT(NBTTagCompound tag) {
		red = tag.getBoolean("red");
		green = tag.getBoolean("green");
		blue = tag.getBoolean("blue");
	}

	public void writeToNBT(NBTTagCompound tag) {
		tag.setBoolean("red", red);
		tag.setBoolean("green", green);
		tag.setBoolean("blue", blue);
	}

	public void writeBuf(ByteBuf data) {
		data.writeBoolean(red);
		data.writeBoolean(green);
		data.writeBoolean(blue);
	}

	public void readBuf(ByteBuf data) {
		red = data.readBoolean();
		green = data.readBoolean();
		blue = data.readBoolean();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof RGBColorData && this.matchColor((RGBColorData)o);
	}

	@Override
	public int hashCode() {
		return this.getRenderColor();
	}

	public RGBColorData copy() {
		return new RGBColorData(red, green, blue);
	}

	@Override
	public String toString() {
		return this.getName()+": "+red+"/"+green+"/"+blue+" : "+Integer.toHexString(this.getRenderColor()&0xffffff);
	}

	public String getName() {
		if (red) {
			if (green) {
				if (blue) {
					return "white";
				}
				else {
					return "yellow";
				}
			}
			else {
				if (blue) {
					return "magenta";
				}
				else {
					return "red";
				}
			}
		}
		else {
			if (green) {
				if (blue) {
					return "cyan";
				}
				else {
					return "green";
				}
			}
			else {
				if (blue) {
					return "blue";
				}
				else {
					return "black";
				}
			}
		}
	}

	public Collection<RGBColorData> getReductiveChildren(boolean allowSelf, boolean allowBlack) {
		Collection<RGBColorData> ret = this.getAllPossibilities();
		Iterator<RGBColorData> it = ret.iterator();
		while (it.hasNext()) {
			RGBColorData c = it.next();
			if (c.matchColor(this) && !allowSelf) {
				it.remove();
				continue;
			}
			if (c.isBlack() && !allowBlack) {
				it.remove();
				continue;
			}
			if (!red && c.red) {
				it.remove();
				continue;
			}
			if (!green && c.green) {
				it.remove();
				continue;
			}
			if (!blue && c.blue) {
				it.remove();
				continue;
			}
		}
		return ret;
	}

	public Collection<RGBColorData> getAdditiveChildren(boolean allowSelf, boolean allowWhite) {
		Collection<RGBColorData> ret = this.getAllPossibilities();
		Iterator<RGBColorData> it = ret.iterator();
		while (it.hasNext()) {
			RGBColorData c = it.next();
			if (c.matchColor(this) && !allowSelf) {
				it.remove();
				continue;
			}
			if (c.isWhite() && !allowWhite) {
				it.remove();
				continue;
			}
			if (red && !c.red) {
				it.remove();
				continue;
			}
			if (green && !c.green) {
				it.remove();
				continue;
			}
			if (blue && !c.blue) {
				it.remove();
				continue;
			}
		}
		return ret;
	}

	/** Only meaningful for secondary colors */
	public RGBColorData getColorNeededToMake(RGBColorData c) {
		RGBColorData c2 = this.copy();
		c2.invert();
		c2.add(c);
		return c2;
	}

	public static RGBColorData white() {
		return new RGBColorData(true, true, true);
	}

	public static RGBColorData black() {
		return new RGBColorData(false, false, false);
	}

	public static RGBColorData fromHex(int hex) {
		int r = ReikaColorAPI.getRed(hex);
		int g = ReikaColorAPI.getGreen(hex);
		int b = ReikaColorAPI.getBlue(hex);
		return new RGBColorData(r > 127, g > 127, b > 127);
	}

	public static Collection<RGBColorData> getAllPossibilities() {
		Collection<RGBColorData> ret = new ArrayList();
		for (int i = 0; i <= 1; i++) {
			for (int j = 0; j <= 1; j++) {
				for (int k = 0; k <= 1; k++) {
					ret.add(new RGBColorData(i > 0, j > 0, k > 0));
				}
			}
		}
		return ret;
	}

}
