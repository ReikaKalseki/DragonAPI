package Reika.DragonAPI.ModInteract.Bees;

import forestry.api.genetics.IAllele;
import forestry.api.genetics.IChromosomeType;


public abstract class GeneBase implements IAllele {

	protected final String uid;
	public final String name;
	public final IChromosomeType geneType;

	public GeneBase(String uid, String name, IChromosomeType type) {
		this.uid = uid;
		this.name = name;
		geneType = type;
	}

	@Override
	public final String getUID() {
		return uid;
	}

	@Override
	public final String getName() {
		return name;
	}

	@Override
	public final String getUnlocalizedName() {
		return uid;
	}

	public final boolean equals(IAllele ia) {
		return ia != null && ia.getClass() == this.getClass() && ia.getUID().equals(uid);
	}

}
