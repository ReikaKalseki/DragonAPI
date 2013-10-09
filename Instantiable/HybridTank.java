/*******************************************************************************
 * @author Reika Kalseki
 * 
 * Copyright 2013
 * 
 * All rights reserved.
 * Distribution of the software in any form is only allowed with
 * explicit, prior permission from the owner.
 ******************************************************************************/
package Reika.DragonAPI.Instantiable;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;
import Reika.DragonAPI.Libraries.Java.ReikaJavaLibrary;

/** A tank class that can handle direct operations as well as standard Forge Liquid operations. */
public class HybridTank extends FluidTank {

	private final String name;

	public HybridTank(String name, int capacity) {
		super(capacity);
		this.name = name;
	}

	public HybridTank(String name, FluidStack stack, int capacity) {
		super(stack, capacity);
		this.name = name;
	}

	public HybridTank(String name, Fluid fluid, int amount, int capacity) {
		super(fluid, amount, capacity);
		this.name = name;
	}

	@Override
	public final NBTTagCompound writeToNBT(NBTTagCompound NBT) {
		NBTTagCompound tankData = new NBTTagCompound();
		super.writeToNBT(tankData);
		NBT.setCompoundTag(name, tankData);
		return NBT;
	}

	@Override
	public final FluidTank readFromNBT(NBTTagCompound NBT) {
		if (NBT.hasKey(name)) {
			NBTTagCompound tankData = NBT.getCompoundTag(name);
			super.readFromNBT(tankData);
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
			ReikaJavaLibrary.pConsole("Could not remove liquid from empty tank!");
			Thread.dumpStack();
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

}
