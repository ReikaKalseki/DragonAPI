package Reika.DragonAPI.Instantiable.ModInteract;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;

import net.minecraft.nbt.NBTTagCompound;
import thaumcraft.api.aspects.Aspect;
import Reika.DragonAPI.ModList;
import Reika.DragonAPI.ASM.DependentMethodStripper.ModDependent;

public class CompoundAspectTank {

	@ModDependent(ModList.THAUMCRAFT)
	private final HashMap<Aspect, Integer> aspects = new HashMap();

	protected final int capacity;
	public final int maxTypes;

	public CompoundAspectTank(int c) {
		this(c, Integer.MAX_VALUE);
	}

	public CompoundAspectTank(int c, int types) {
		capacity = c;
		maxTypes = types;
	}

	public int getMaxCapacity(Aspect a) {
		return capacity;
	}

	@ModDependent(ModList.THAUMCRAFT)
	public int getLevel(Aspect a) {
		Integer ret = aspects.get(a);
		return ret != null ? ret.intValue() : 0;
	}

	@ModDependent(ModList.THAUMCRAFT)
	public final int getRemainingSpace(Aspect a) {
		return this.getMaxCapacity(a)-this.getLevel(a);
	}

	@ModDependent(ModList.THAUMCRAFT)
	public Collection<Aspect> getAspects() {
		return Collections.unmodifiableCollection(aspects.keySet());
	}

	@ModDependent(ModList.THAUMCRAFT)
	public void setAspect(Aspect a, int amt) {
		if (this.canAccept(a))
			aspects.put(a, amt);
	}

	@ModDependent(ModList.THAUMCRAFT)
	public int addAspect(Aspect a, int amt) {
		if (this.canAccept(a)) {
			int add = Math.min(amt, this.getRemainingSpace(a));
			int newlevel = this.getLevel(a)+add;
			this.setAspect(a, newlevel);
			return add;
		}
		else
			return 0;
	}

	@ModDependent(ModList.THAUMCRAFT)
	public boolean canAccept(Aspect a) {
		return aspects.containsKey(a) || aspects.size() < maxTypes;
	}

	@ModDependent(ModList.THAUMCRAFT)
	public int drainAspect(Aspect a, int amt) {
		int level = this.getLevel(a);
		int rem = Math.min(amt, level);
		int newlevel = level-rem;
		if (newlevel <= 0)
			aspects.remove(a);
		else
			this.setAspect(a, newlevel);
		return rem;
	}

	public void writeToNBT(NBTTagCompound NBT) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return;
		NBTTagCompound dat = new NBTTagCompound();
		for (Aspect a : aspects.keySet()) {
			dat.setInteger(a.getTag(), this.getLevel(a));
		}
		NBT.setTag("aspectData", dat);
	}

	public void readFromNBT(NBTTagCompound NBT) {
		if (!ModList.THAUMCRAFT.isLoaded())
			return;
		NBTTagCompound dat = NBT.getCompoundTag("aspectData");
		aspects.clear();
		for (Object o : dat.func_150296_c()) {
			String s = (String)o;
			Aspect a = Aspect.getAspect(s);
			aspects.put(a, dat.getInteger(s));
		}
	}

	public void empty() {
		aspects.clear();
	}

	public Aspect getFirstAspect() {
		return this.isEmpty() ? null : (Aspect)aspects.keySet().toArray()[0];
	}

	public boolean isEmpty() {
		return aspects.isEmpty();
	}

	//----------------Prefabs----------------
	public static class PrimalOnlyAspectTank extends CompoundAspectTank {

		public PrimalOnlyAspectTank(int c) {
			super(c);
		}

		public PrimalOnlyAspectTank(int c, int types) {
			super(c, types);
		}

		@Override
		public boolean canAccept(Aspect a) {
			return a.isPrimal();
		}

	}

}
