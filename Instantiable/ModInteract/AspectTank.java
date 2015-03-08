package Reika.DragonAPI.Instantiable.ModInteract;

import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;

public class AspectTank {

	public final int capacity;
	private int level;

	@ModDependent(ModList.THAUMCRAFT)
	private Aspect aspect;

	public AspectTank(int c) {
		capacity = c;
	}

	public int getLevel() {
		return level;
	}

	public final int getRemainingSpace() {
		return capacity-level;
	}

	@ModDependent(ModList.THAUMCRAFT)
	public Aspect getAspect() {
		return aspect;
	}

	@ModDependent(ModList.THAUMCRAFT)
	public void setAspect(Aspect a, int amt) {
		if (this.canAccept(a)) {
			aspect = a;
			level = amt;
		}
	}

	@ModDependent(ModList.THAUMCRAFT)
	public int addAspect(Aspect a, int amt) {
		if (this.canAccept(a)) {
			int add = Math.min(amt, this.getRemainingSpace());
			level += add;
			aspect = a;
			return add;
		}
		else
			return 0;
	}

	@ModDependent(ModList.THAUMCRAFT)
	public boolean canAccept(Aspect a) {
		return aspect == null || a == aspect;
	}

	public int drainAspect(int amt) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return 0;
		int rem = Math.min(amt, level);
		level -= rem;
		if (level <= 0)
			aspect = null;
		return rem;
	}

	public void writeToNBT(NBTTagCompound NBT) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return;
		NBT.setString("aspect", aspect != null ? aspect.getTag() : "null");
		NBT.setInteger("amount", level);
	}

	public void readFromNBT(NBTTagCompound NBT) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return;
		String s = NBT.getString("aspect");
		aspect = s.equals("null") || s.isEmpty() ? null : Aspect.getAspect(s);
		level = NBT.getInteger("amount");
	}

	public void empty() {
		aspect = null;
		level = 0;
	}

}
