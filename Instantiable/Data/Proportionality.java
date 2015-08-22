package Reika.DragonAPI.Instantiable.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import net.minecraft.client.renderer.Tessellator;

import org.lwjgl.opengl.GL11;

import Reika.DragonAPI.Libraries.IO.ReikaColorAPI;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;


public class Proportionality<F> {

	private static final Random rand = new Random();

	private final HashMap<F, Double> data = new HashMap();

	private double totalValue = 0;

	public Proportionality() {

	}

	public void addValue(F o, double amt) {
		Double get = data.get(o);
		double val = get != null ? get.doubleValue() : 0;
		data.put(o, val+amt);
		this.totalValue += amt;
	}

	public void removeValue(F o, double amt) {
		Double get = data.get(o);
		double val = get != null ? get.doubleValue() : 0;
		double res = val-amt;
		this.totalValue -= Math.min(amt, val);
		if (res > 0)
			data.put(o, res);
		else
			data.remove(o);
	}

	public double getValue(F o) {
		Double get = data.get(o);
		return get != null ? get.doubleValue() : 0;
	}

	public double getFraction(F o) {
		return this.getValue(o)/this.totalValue;
	}

	public boolean hasMajority(F o) {
		return this.getFraction(o) >= 0.5;
	}

	public F getLargestCategory() {
		double max = -1;
		F big = null;
		for (F o : data.keySet()) {
			double has = this.getValue(o);
			if (has > max) {
				has = max;
				big = o;
			}
		}
		return big;
	}

	public void clear() {
		data.clear();
	}

	@SideOnly(Side.CLIENT)
	public void renderAsPie(double x, double y, double r, double zeroAng) {
		this.renderAsPie(x, y, r, zeroAng, null);
	}

	@SideOnly(Side.CLIENT)
	public void renderAsPie(double x, double y, double r, double zeroAng, HashMap<F, Integer> colorMap) {
		double ang = zeroAng;
		Tessellator v5 = Tessellator.instance;
		int i = 0;
		for (F o : data.keySet()) {
			double angw = 360D*this.getFraction(o);

			v5.startDrawing(GL11.GL_TRIANGLE_FAN);
			int c = 0;
			if (colorMap != null && colorMap.containsKey(o)) {
				c = colorMap.get(o);
			}
			else {
				if (i >= defaultColors.size()) {
					int newcolor = ReikaColorAPI.RGBtoHex(rand.nextInt(255), rand.nextInt(255), rand.nextInt(255));
					defaultColors.add(newcolor);
					c = newcolor;
				}
				else {
					c = defaultColors.get(i);
				}
				i++;
			}
			v5.setColorOpaque_I(c);

			v5.addVertex(x, y, 0);

			for (double d = ang; d <= ang+angw; d += 0.5) {
				double dx = r*Math.cos(Math.toRadians(d));
				double dy = r*Math.sin(Math.toRadians(d));
				v5.addVertex(dx, dy, 0);
			}

			v5.draw();

			ang += angw;
		}
	}

	private static ArrayList<Integer> defaultColors = new ArrayList();

	static {
		defaultColors.add(0xff0000);
		defaultColors.add(0x00ff00);
		defaultColors.add(0x0000ff);
		defaultColors.add(0xffff00);
		defaultColors.add(0xff00ff);
		defaultColors.add(0x00ffff);
		defaultColors.add(0xa0a0a0);
	}

	/*
	public void writeToNBT(NBTTagCompound NBT) {
		NBT.setDouble("total", totalValue);
		NBTTagList li = new NBTTagList();
		for (F o : data.keySet()) {
			NBTTagCompound tag = new NBTTagCompound();
			tag.setDouble("Prop_"+o.toString(), this.getValue(o));
		}
		NBT.setTag("data", li);
	}

	public void readFromNBT(NBTTagCompound NBT) {
		totalValue = NBT.getDouble("total");
		data.clear();


	}
	 */

}
