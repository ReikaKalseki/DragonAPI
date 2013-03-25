package Reika.DragonAPI;

import Reika.DragonAPI.*;

import net.minecraft.*;
import net.minecraft.src.*;
import net.minecraft.block.*;
import net.minecraft.client.*;
import net.minecraft.entity.*;
import net.minecraft.inventory.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import net.minecraft.network.*;
import net.minecraft.client.model.*;
import net.minecraft.block.material.*;
import net.minecraft.client.audio.*;
import net.minecraft.client.entity.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.client.particle.*;
import net.minecraft.tileentity.*;
import net.minecraft.world.*;
import net.minecraft.util.*;
import net.minecraft.enchantment.*;
import net.minecraft.pathfinding.*;
import net.minecraft.potion.*;
import net.minecraft.profiler.*;
import net.minecraft.client.renderer.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.*;
import net.minecraft.world.gen.*;
import net.minecraft.world.gen.feature.*;
import net.minecraft.world.gen.layer.*;
import net.minecraft.world.gen.structure.*;
import net.minecraft.world.storage.*;
import net.minecraft.client.renderer.culling.*;
import net.minecraft.command.*;
import net.minecraft.crash.*;
import net.minecraft.creativetab.*;
import net.minecraft.dispenser.*;
import net.minecraft.item.crafting.*;
import net.minecraft.network.packet.*;
import net.minecraft.server.gui.*;
import net.minecraft.village.*;

import cpw.mods.*;
import cpw.mods.fml.*;
import cpw.mods.fml.client.*;
import cpw.mods.fml.client.modloader.*;
import cpw.mods.fml.client.registry.*;
import cpw.mods.fml.common.*;
import cpw.mods.fml.common.asm.*;
import cpw.mods.fml.common.event.*;
import cpw.mods.fml.common.functions.*;
import cpw.mods.fml.server.*;

import java.util.*;
import java.lang.*;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class ReikaMathLibrary {
	
	public static final double e = 2.718;				// s/e
	public static final double pi = 3.1415926535;		// s/e
	public static final double G = 6.67*0.00000000001;	// Grav Constant
	public static final double patm = 101300;			// Atmosphere Sealevel pressure
	public static final double rhog = 19300;			// Gold Density
	public static final double rhofe = 8200;			// Iron Density
	public static final double rhow = 1000;				// Water density
	
	/** Returns the pythagorean sum of the three inputs. Used mainly for vector magnitudes.
	 * Args: x,y,z */
	public static double py3d(double dx, double dy, double dz) {
		double val;
		val = dx*dx+dy*dy+dz*dz;
		return MathHelper.sqrt_double(val);
	}
	
	/** Returns true if the input is within a percentage of its size of another value.
	 * Args: Input, target, percent tolerance */
	public static boolean approxp(double input, double target, double percent) {
		double low = input - input*percent/100;
		double hi = input + input*percent/100;
		if ((target >= low) && (target <= hi))
			return true;
		else
			return false;
	}
	
	/** Returns true if the input is within [target-range,target+range]. Args: input, target, range */
	public static boolean approxr(double input, double target, double range) {
		double low = input - range;
		double hi = input + range;
		if ((target >= low) && (target <= hi))
			return true;
		else
			return false;
	}
	
	/** Returns the value of a double raised to an integer power. Args: Base, power */
	public static double intpow(double base, int pow) {
		double val = 1.0D;
		for (int i = 0; i < pow; i++) {
			val *= base;
		}
		return val;
	}
	
	/** Returns the value of a double raised to an decimal power. Args: Base, power */
	public static double doubpow(double base, double pow) {
		double val = 1.0D;
		return Math.pow(base, pow);
	}

	/** Returns a random integer between two specified bounds. Args: Low bound, hi bound */
	public static int randinrange(int low, int hi) {
		int val = low;
		// += Random.nextInt(hi-low);
		return val;
	}
	
	/** Calculates the magnitude of the difference between two values. Args: a, b */
	public static double leftover(double a, double b) {
		double val;
		if (a > b)
			val = a - b;
		else
			val = b - a;
		return val;
	}
	
	/** Returns the logarithm of a specified base. Args: input, logbase */
	public static double logbase(double inp, double base) {
		double val = Math.log(inp);
		return val/(Math.log(base));
	}
	
	/** Returns the abs-max, abs-min, signed max, or signed min of the arguments,
	 * as specified. Args: a, b, operation. Operations: "min", "absmin", "max",
	 * "absmax". All other inputs will result in the method returning -987654321 */
	public static int extrema(int a, int b, String control) {
		if (control == "min") {
			if (a > b)
				return b;
			else
				return a;
		}
		if (control == "absmin") {
			if (a < 0)
				a *= -1;
			if (b < 0)
				b *= -1;
			if (a > b)
				return b;
			else
				return a;
		}
		if (control == "max") {
			if (a > b)
				return a;
			else
				return b;
		}
		if (control == "absmax") {
			if (a < 0)
				a *= -1;
			if (b < 0)
				b *= -1;
			if (a > b)
				return a;
			else
				return b;
		}
		return -987654321; // Seriously doubt this will happen
	}
	
	/** A double-IO version of extrema. */
	public static double extremad(double a, double b, String control) {
		if (control == "min") {
			if (a > b)
				return b;
			else
				return a;
		}
		if (control == "absmin") {
			if (a < 0)
				a *= -1;
			if (b < 0)
				b *= -1;
			if (a > b)
				return b;
			else
				return a;
		}
		if (control == "max") {
			if (a > b)
				return a;
			else
				return b;
		}
		if (control == "absmax") {
			if (a < 0)
				a *= -1;
			if (b < 0)
				b *= -1;
			if (a > b)
				return a;
			else
				return b;
		}
		return -987654321; // Seriously doubt this will happen
	}
	
	/** Returns the nearest higher power of 2. Args: input */
	public static int ceil2exp(int val) {
		val--;
		val = (val >> 1) | val;
		val = (val >> 2) | val;
		val = (val >> 4) | val;
		val = (val >> 8) | val;
		val = (val >> 16) | val;
		val++;
		return val;
	}
	
	/** Returns whether the two numbers are the same sign.
	 * Will return true if both are zero. Args: Input 1, Input 2*/
	public static boolean isSameSign(double val1, double val2) {
		if (val1 == 0 || val2 == 0)
			return true;
		if ((1000*val1)/val2 > 0)
			return true;
		return false;
	}
	
	/** Splits a number of items into an array; index 0 is number of stacks, index 1 is leftover
	 * Args: Number items, MaxStack size */
	public static int[] splitStacks(int number, int size) {
		int[] stacks = new int[2];
		if (number == 0) {
			stacks[0] = 0;
			stacks[1] = 0;
			return stacks;
		}
		while (number >= size) {
			stacks[0]++;
			number -= size;
		}
		stacks[1] = number;
		return stacks;
	}
	
	/** Returns the next multiple of a higher than b. Args: a, b */
	public static int nextMultiple(int a, int b) {
		while (b%a != 0) {
			b++;
		}
		return b;
	}
}
