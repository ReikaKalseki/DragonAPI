/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2016
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package reika.dragonapi.instantiable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import reika.dragonapi.DragonAPICore;
import reika.dragonapi.libraries.ReikaFluidHelper;
import reika.dragonapi.libraries.java.ReikaJavaLibrary;

/** A tank class that can handle direct operations as well as standard Forge Liquid operations. */
public class HybridTank extends FluidTank {

	protected final String name;

	public HybridTank(String name, int capacity) {
		super(capacity);
		this.name = name;
	}

	public HybridTank(String name, FluidStack stack, int capacity) {
		this(name, capacity);
		this.setFluid(stack);
	}

	public HybridTank(String name, Fluid fluid, int amount, int capacity) {
		this(name, new FluidStack(fluid, amount), capacity);
	}

	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound NBT) {
		NBTTagCompound tankData = new NBTTagCompound();
		super.writeToNBT(tankData);

		String fluidName = tankData.getString("FluidName");
		String repl = ReikaFluidHelper.getFluidNameSwap(fluidName);
		if (repl != null && FluidRegistry.getFluid(repl) != null) {
			tankData.setString("FluidName", repl);
			DragonAPICore.log("Tank "+this+" has replaced its FluidName of '"+fluidName+"' with '"+repl+"', as the fluid has changed names.");
		}

		NBT.setTag(name, tankData);

		return NBT;
	}

	@Override
	public final FluidTank readFromNBT(NBTTagCompound NBT) {
		try {
			if (NBT.hasKey(name)) {
				NBTTagCompound tankData = NBT.getCompoundTag(name);
				String fluidName = tankData.getString("FluidName");
				String repl = ReikaFluidHelper.getFluidNameSwap(fluidName);
				if (repl != null && FluidRegistry.getFluid(repl) != null) {
					tankData.setString("FluidName", repl);
					DragonAPICore.log("Tank "+this+" has replaced its FluidName of '"+fluidName+"' with '"+repl+"', as the fluid has changed names.");
				}
				super.readFromNBT(tankData);
			}
		}
		catch (IllegalArgumentException e) { //"Empty String not allowed!" caused by fluid save failure
			DragonAPICore.logError("Loading HybridTank '"+name+"' has errored, its machine will not keep its fluid!");
			e.printStackTrace();
		}
		return this;
	}

	public boolean isEmpty() {
		return this.getFluid() == null || this.getFluid().amount <= 0;
	}

	public boolean isFull() {
		return this.getFluid() != null && this.getFluid().amount >= this.getCapacity();
	}

	public int getLevel() {
		if (this.getFluid() == null)
			return 0;
		return this.getFluid().amount;
	}

	public void removeLiquid(int amt) {
		if (this.getFluid() == null) {
			DragonAPICore.logError("Could not remove liquid from empty tank!");
			ReikaJavaLibrary.dumpStack();
		}
		else if (amt <= 0) {
			DragonAPICore.logError("Cannot remove <= 0!");
			ReikaJavaLibrary.dumpStack();
		}
		else {
			this.drain(amt, true);
		}
	}

	public void addLiquid(int amt, Fluid type) {
		if (type == null)
			return;
		if (amt > capacity)
			amt = capacity;
		if (this.getFluid() == null) {
			this.fill(new FluidStack(type, amt), true);
		}
		else if (type.equals(this.getFluid().getFluid())) {
			this.fill(new FluidStack(this.getFluid().getFluid(), amt), true);
		}
	}

	public void empty() {
		this.drain(this.getLevel(), true);
	}

	public void setFluidType(Fluid type) {
		int amt = this.getLevel();
		this.drain(amt, true);
		this.fill(new FluidStack(type, amt), true);
	}

	public void setContents(int amt, Fluid f) {
		this.empty();
		this.addLiquid(amt, f);
	}

	public Fluid getActualFluid() {
		if (this.getFluid() == null)
			return null;
		return this.getFluid().getFluid();
	}

	public float getFraction() {
		return this.getLevel()/(float)this.getCapacity();
	}

	@Override
	public String toString() {
		if (this.isEmpty())
			return "Empty Tank "+name;
		return "Tank "+name+", containing "+this.getLevel()+" mB of "+this.getActualFluid().getLocalizedName();
	}

	public int getRemainingSpace() {
		return capacity-this.getLevel();
	}

	public boolean canTakeIn(int amt) {
		return this.getRemainingSpace() >= amt;
	}

	public boolean canTakeIn(Fluid f, int amt) {
		if (this.isEmpty()) {
			return capacity >= amt;
		}
		else {
			return this.getRemainingSpace() >= amt && this.getActualFluid().equals(f);
		}
	}

	public boolean canTakeIn(FluidStack fs) {
		int amt = fs.amount;
		return this.isEmpty() ? capacity >= amt : this.getRemainingSpace() >= amt && this.getActualFluid().equals(fs.getFluid());
	}

	public void setNBT(NBTTagCompound nbt) {
		if (this.getFluid() != null)
			this.getFluid().tag = nbt;
	}

	public void setNBTInt(String key, int val) {
		if (this.getFluid() != null) {
			if (this.getFluid().tag == null)
				this.getFluid().tag = new NBTTagCompound();
			this.getFluid().tag.setInteger(key, val);
		}
	}

	public void setNBTString(String key, String s) {
		if (this.getFluid() != null) {
			if (this.getFluid().tag == null)
				this.getFluid().tag = new NBTTagCompound();
			this.getFluid().tag.setString(key, s);
		}
	}

	public void setNBTBoolean(String key, boolean b) {
		if (this.getFluid() != null) {
			if (this.getFluid().tag == null)
				this.getFluid().tag = new NBTTagCompound();
			this.getFluid().tag.setBoolean(key, b);
		}
	}

	public int getNBTInt(String key) {
		return this.getFluid() != null && this.getFluid().tag != null ? this.getFluid().tag.getInteger(key) : 0;
	}

	public String getNBTString(String key) {
		return this.getFluid() != null && this.getFluid().tag != null ? this.getFluid().tag.getString(key) : "";
	}

	public boolean getNBTBoolean(String key) {
		return this.getFluid() != null && this.getFluid().tag != null ? this.getFluid().tag.getBoolean(key) : false;
	}

}
