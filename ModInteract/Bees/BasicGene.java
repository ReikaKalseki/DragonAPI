package Reika.DragonAPI.ModInteract.Bees;

import forestry.api.genetics.AlleleManager;
import forestry.api.genetics.IAllele;

public abstract class BasicGene implements IAllele {

	private final String uid;
	public final String name;

	public BasicGene(String uid, String name) {
		this.uid = uid;
		this.name = name;
		this.preInit();
		AlleleManager.alleleRegistry.registerAllele(this);
	}

	protected void preInit() {

	}

	@Override
	public final String getUID() {
		return uid;
	}

	@Override
	public boolean isDominant() {
		return true;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getUnlocalizedName() {
		return uid;
	}

}
